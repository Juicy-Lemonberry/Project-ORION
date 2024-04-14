import serial
import serial.tools.list_ports
from evdev import InputDevice, categorize, ecodes # Only used for local testing with a gamepad connected to the Raspberry Pi physically
import threading
import time
import pantilthat as p
import socket
import datetime

import bluetooth
import subprocess
import threading


server_uuid = "35b4c90d-a811-4d7f-a988-575e8793b3f2"

#Global variable and lock to share data between threads safely
input_steer_value = 32767 #0 = left, 65535 = right, 32767 = center
input_gas_value = 0
input_brake_value = 0
input_toga_flag = False
input_pan_angle = 32767
input_tilt_angle = 32767
input_lights_flag = False
input_lights_brightness = 255
network_ssid = "N/C"
network_wlan0_ip = "N/C"
network_eth0_ip = "N/C"


input_pan_angle = 0
input_tilt_angle = 0

# New global variable for last UDP message timestamp
last_udp_msg_time = datetime.datetime.now()


input_steer_value_lock = threading.Lock()
input_gas_value_lock = threading.Lock()
input_brake_value_lock = threading.Lock()
input_toga_flag_lock = threading.Lock()
input_tilt_angle_lock = threading.Lock()
input_pan_angle_lock = threading.Lock()
input_lights_flag_lock = threading.Lock()
input_lights_brightness_lock = threading.Lock()
network_ssid_lock = threading.Lock()
network_wlan0_ip_lock = threading.Lock()
network_eth0_ip_lock = threading.Lock()


def map_range(value, leftMin, leftMax, rightMin, rightMax):
    # Maps a value from one range to another
    leftSpan = leftMax - leftMin
    rightSpan = rightMax - rightMin
    valueScaled = float(value - leftMin) / float(leftSpan)
    return round(rightMin + (valueScaled * rightSpan))

def clamp_max(value, min_value, max_value):
    if value > max_value:
        return max_value
    elif value < min_value:
        return min_value
    else:
        return value

def esp_32_thread():
    ports = serial.tools.list_ports.comports()
    target_device_port = None
    for port in ports:
        try:
            # Attempt to open the serial port
            with serial.Serial(port.device, 115200, timeout=2) as ser:
                print(f"Trying {port.device}...")
                ser.write(b'Hello')  # Simple handshake protocol
                time.sleep(1)
                if ser.readline().decode().strip() == 'Hello':
                    print(f"Device found on {port.device}")
                    target_device_port = port.device
                    break
        except (serial.SerialException, serial.SerialTimeoutException) as e:
            print(f"Could not open serial port {port.device}: {e}")
    
    if target_device_port:
        try:
            with serial.Serial(target_device_port, 115200, timeout=1) as ser:
                while True:
                    #Calculate the relative gas value (AKA, forward trigger)
                    with input_gas_value_lock:
                        relative_gas_value = map_range(input_gas_value, 0, 1024, 0, 100)

                    #Calculate the relative brake value (AKA, reverse trigger)
                    with input_brake_value_lock:
                        relative_brake_value = map_range(input_brake_value, 0, 1024, 0, 100)

                    #Calculate the net steering value
                    with input_steer_value_lock:
                        relative_steer_value = map_range(input_steer_value, 0, 65535, -100, 100)

                    net_absolute_speed = relative_gas_value - relative_brake_value
                    net_left_speed = net_absolute_speed
                    net_right_speed = net_absolute_speed

                    #Handle TOGA Boost here
                    with input_toga_flag_lock:
                        if input_toga_flag:
                            if net_absolute_speed > 0:
                                net_left_speed += 1
                                net_right_speed += 1
                            elif net_absolute_speed < 0:
                                net_left_speed -= 1
                                net_right_speed -= 1

                    if net_absolute_speed > 0:
                        if relative_steer_value > 0:
                            net_right_speed = net_right_speed - relative_steer_value
                        elif relative_steer_value < 0:
                            net_left_speed = net_left_speed + relative_steer_value
                    elif net_absolute_speed < 0:
                        if relative_steer_value > 0:
                            net_right_speed = net_right_speed + relative_steer_value
                        elif relative_steer_value < 0:
                            net_left_speed = net_left_speed - relative_steer_value

                    message = f"{net_left_speed},{net_right_speed},{net_left_speed},{net_right_speed}\n"
                    #print(f"WROTE: {message}")
                    ser.write(message.encode())

                    with network_ssid_lock, network_wlan0_ip_lock, network_eth0_ip_lock:
                        net_message = f"SSID:{network_ssid},IP:{network_wlan0_ip},{network_eth0_ip}\n"
                    ser.write(net_message.encode())
                    time.sleep(0.1)


        except serial.SerialException as e:
            print(f"Failed to open serial port: {e}")


def pantilt_thread():

    global last_udp_msg_time
    global input_steer_value, input_pan_angle, input_tilt_angle, input_lights_brightness, input_gas_value, input_brake_value, input_lights_flag

    r = 0
    g = 0
    b = 0
    pan_angle = 0
    tilt_angle = 0
    p.pan(pan_angle)
    p.tilt(tilt_angle)
    p.clear()
    p.light_mode(1) #1 = WS2812
    p.light_type(1) #1 = GRB
    p.set_all(r,g,b) #turn off all LEDs
    p.show() #flush the buffer to the LEDs
    lights_status = False

    while True:
        current_time = datetime.datetime.now()
        time_diff = current_time - last_udp_msg_time

        if time_diff.total_seconds() > 2:
            with input_gas_value_lock:
                input_gas_value = 0
            with input_brake_value_lock:
                input_brake_value = 0
            with input_steer_value_lock:
                input_steer_value = 32767
            with input_tilt_angle_lock:
                input_tilt_angle = 32767
            with input_pan_angle_lock:
                input_pan_angle = 32767
            with input_lights_flag_lock:
                input_lights_flag = False
            with input_lights_brightness_lock:
                input_lights_brightness = 255

            # Flash the LED light bar on/off with the color red
            p.clear()
            p.set_all(255, 0, 0)  # Set to red
            p.show()
            time.sleep(0.25)  # On for 1 second
            
            p.clear()  # Turn off
            p.show()
            time.sleep(0.25)  # Off for 1 second
            
        else:
            #print(f"pan_angle: {pan_angle}, tilt_angle: {tilt_angle}")
            with input_pan_angle_lock:
                p.pan(map_range(input_pan_angle, 0, 65535, -90, 90) * -1)
            with input_tilt_angle_lock:
                p.tilt(map_range(input_tilt_angle, 0, 65535, -90, 90))

            with input_lights_flag_lock:
                lights_status = input_lights_flag

            with input_lights_brightness_lock:
                loc_input_lights_brightness = input_lights_brightness
            if lights_status:
                r = loc_input_lights_brightness
                g = loc_input_lights_brightness
                b = loc_input_lights_brightness
            else:
                r = 0
                g = 0
                b = 0
            p.set_all(r,g,b)
            p.show()
            
            time.sleep(0.01)

def input_thread_old_old():
    gamepad = InputDevice('/dev/input/event4')
    print(gamepad)
    global input_steer_value
    global input_brake_value
    global input_gas_value
    global input_toga_flag
    global input_pan_angle
    global input_tilt_angle
    global input_lights_flag
    global input_lights_brightness
    
    with input_gas_value_lock:
        input_gas_value = 0
    with input_brake_value_lock:
        input_brake_value = 0

    for event in gamepad.read_loop():
        #print(f"STEERING: {input_steer_value}, GAS: {input_gas_value}, BRAKE: {input_brake_value}")
        print(event)
        if event.type == ecodes.EV_ABS:
            abs_event = categorize(event)
            if abs_event.event.code == ecodes.ABS_GAS:
                with input_gas_value_lock:
                    input_gas_value = abs_event.event.value
            elif abs_event.event.code == ecodes.ABS_BRAKE:
                with input_brake_value_lock:
                    input_brake_value = abs_event.event.value
            elif abs_event.event.code == ecodes.ABS_X:
                with input_steer_value_lock:
                    input_steer_value = abs_event.event.value
            elif abs_event.event.code == ecodes.ABS_RZ:
                with input_tilt_angle_lock:
                    input_tilt_angle = abs_event.event.value
            elif abs_event.event.code == ecodes.ABS_Z:
                with input_pan_angle_lock:
                    input_pan_angle = abs_event.event.value
            elif abs_event.event.code == ecodes.ABS_HAT0Y and abs_event.event.value == -1:
                with input_lights_brightness_lock:
                    input_lights_brightness = clamp_max(input_lights_brightness + 20, 10, 255)   
                print(f"LIGHTS BRIGHTNESS: {input_lights_brightness}")
            elif abs_event.event.code == ecodes.ABS_HAT0Y and abs_event.event.value == 1:
                with input_lights_brightness_lock:
                    input_lights_brightness = clamp_max(input_lights_brightness - 20, 10, 255)
                print(f"LIGHTS BRIGHTNESS: {input_lights_brightness}")


        
        if event.type == ecodes.EV_KEY:
            key_event = categorize(event)
            if key_event.event.code == ecodes.BTN_TL:
                with input_toga_flag_lock:
                    input_toga_flag = True if key_event.event.value else False
                print(f"TOGA: {'ON' if input_toga_flag else 'OFF'}")
            elif key_event.event.code == ecodes.BTN_Y and key_event.event.value == 1:
                with input_lights_flag_lock:
                    input_lights_flag = not input_lights_flag
                print(f"LIGHTS: {'ON' if input_lights_flag else 'OFF'}")

     
def input_thread(stop_event):
    # Setup UDP socket
    udp_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
    udp_socket.bind(('', 5000))  # Listen on all interfaces, port 5000

    print("UDP socket listening on port 5000")

    global input_steer_value
    global input_brake_value
    global input_gas_value
    global input_toga_flag
    global input_pan_angle
    global input_tilt_angle
    global input_lights_flag
    global input_lights_brightness
    global last_udp_msg_time
    
    # Initialize default values
    with input_gas_value_lock:
        input_gas_value = 0
    with input_brake_value_lock:
        input_brake_value = 0

    while not stop_event.is_set():
        udp_socket.settimeout(1.0)

        try:
            # Receive data from the socket
            data, addr = udp_socket.recvfrom(1024)  # buffer size is 1024 bytes
            message = data.decode().strip()
            #print(f"Received message: {message}")
            last_udp_msg_time = datetime.datetime.now()

            # Parsing the received message
            if message.startswith("GAS:"):
                value = float(message.split(":")[1])
                with input_gas_value_lock:
                    input_gas_value = int(value * 1024)  # Scale value to expected range
            elif message.startswith("BRK:"):
                value = float(message.split(":")[1])
                with input_brake_value_lock:
                    input_brake_value = int(value * 1024)  # Scale value to expected range
            elif message.startswith("J:"):
                axis, value = message.split(":")[1].split(",")
                axis = int(axis)
                value = float(value)
                if axis == 0:  # Left Thumbstick Left/Right
                    with input_steer_value_lock:
                        input_steer_value = int((value + 1) * (65535 / 2))
                elif axis == 1:  # Left Thumbstick Up/Down
                    # Assuming Up/Down on Left Thumbstick controls something like gas/brake or tilt
                    # You'll need to define how you want to handle this
                    pass
                elif axis == 11:  # Right Thumbstick Left/Right
                    with input_pan_angle_lock:
                        input_pan_angle = int((value + 1) * (65535 / 2))
                elif axis == 14:  # Right Thumbstick Up/Down
                    with input_tilt_angle_lock:
                        input_tilt_angle = int((value + 1) * (65535 / 2))
            elif message.startswith("Y:1"):
                with input_lights_flag_lock:
                    input_lights_flag = not input_lights_flag
        except:
            continue

def get_wlan_ip(interface='wlan0'):
    """Get the IP address of a specified interface using the `ip` command."""
    try:
        ip_output = subprocess.check_output(['ip', '-4', 'addr', 'show', interface])
        # Decode bytes to string, split by spaces, and get the fourth element which should be the IP
        ip_address = ip_output.decode().split('inet ')[1].split('/')[0]
        return ip_address
    except Exception as e:
        #print(f"Failed to get IP address for {interface}. Error: {e}")
        return None




def monitor_network_and_control_threads():
    input_thread_stop_event = threading.Event()
    input_thread_instance = None

    while True:
        ip_address = get_wlan_ip()

        if ip_address and not input_thread_instance:
            # If there's an IP and no input thread is running, start the thread
            input_thread_stop_event.clear()
            input_thread_instance = threading.Thread(target=input_thread, args=(input_thread_stop_event,))
            input_thread_instance.start()
            print("Input thread started.")
        elif not ip_address and input_thread_instance:
            # If there's no IP and the input thread is running, stop the thread
            input_thread_stop_event.set()
            input_thread_instance.join()
            input_thread_instance = None
            print("Input thread stopped due to loss of IP.")

        time.sleep(1)  # Check every 1 seconds




if __name__ == "__main__":
    monitor_thread = threading.Thread(target=monitor_network_and_control_threads)
    esp32_thread = threading.Thread(target=esp_32_thread)
    pantilt_thread = threading.Thread(target=pantilt_thread)

    monitor_thread.start()
    esp32_thread.start()
    pantilt_thread.start()

    monitor_thread.join()
    esp32_thread.join()
    pantilt_thread.join()