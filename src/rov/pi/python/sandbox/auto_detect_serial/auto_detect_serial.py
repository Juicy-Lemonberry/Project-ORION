import serial
import serial.tools.list_ports
import random
import time

def find_device():
    ports = serial.tools.list_ports.comports()
    target_device_port = None
    random_number = str(random.randint(1000, 9999))  # Generate a random number
    
    for port in ports:
        try:
            # Attempt to open the serial port
            with serial.Serial(port.device, 115200, timeout=2) as ser:
                print(f"Trying {port.device}...")
                ser.write(random_number.encode())  # Send the random number
                time.sleep(1)  # Wait for the device to respond
                incoming = ser.readline().decode().strip()  # Read the response
                
                # Check if the response matches the sent random number
                if incoming == random_number:
                    print(f"Device found on {port.device}")
                    target_device_port = port.device
                    break
                else:
                    print("Response did not match.")
        except (serial.SerialException, serial.SerialTimeoutException) as e:
            print(f"Could not open serial port {port.device}: {e}")
    
    if target_device_port is None:
        print("Device Not Found")
    else:
        print(f"Ready to use device on {target_device_port}")
        # Further code to use the device can go here
    
    return target_device_port

# Example usage
if __name__ == "__main__":
    device_port = find_device()
    if device_port:
        # Port is ready to be used
        pass
