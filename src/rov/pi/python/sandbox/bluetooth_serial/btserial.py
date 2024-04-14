import bluetooth
import subprocess
import time
import threading

server_uuid = "35b4c90d-a811-4d7f-a988-575e8793b3f2"


def get_wlan_ip(interface='wlan0'):
    """Get the IP address of a specified interface using the `ip` command."""
    try:
        ip_output = subprocess.check_output(['ip', '-4', 'addr', 'show', interface])
        # Decode bytes to string, split by spaces, and get the fourth element which should be the IP
        ip_address = ip_output.decode().split('inet ')[1].split('/')[0]
        return ip_address
    except Exception as e:
        print(f"Failed to get IP address for {interface}. Error: {e}")
        return None

import subprocess

def get_connected_ssid(interface='wlan0'):
    """Get the SSID of the currently connected Wi-Fi network using the `iwgetid` command."""
    try:
        # Attempt to get the SSID using the iwgetid command
        ssid_output = subprocess.check_output(['iwgetid', '-r', interface])
        ssid = ssid_output.strip().decode()  # strip to remove newlines and decode bytes to string
        return ssid if ssid else None
    except subprocess.CalledProcessError:
        # iwgetid returns an error if no network is connected
        print(f"No network connected on {interface}.")
    except Exception as e:
        print(f"Error during SSID fetch: {e}")
    return None


def connect_to_wifi(ssid, password, retry_timeout=10):
    print(f"Attempting to connect to {ssid} with password {password}.")
    subprocess.run(['sudo','iwlist','wlan0','scan'], check=False)
    """Attempt to connect to a WiFi network, retrying the connection command if necessary."""
    def attempt_connection():
        add_network_command = ['sudo', 'nmcli', 'device', 'wifi', 'connect', ssid, 'password', password]
        try:
            subprocess.run(add_network_command, check=True)
            print(f"Attempting to connect to {ssid}...")
            return True
        except subprocess.CalledProcessError as e:
            print(f"Failed to issue connection command to {ssid}. Error: {e}")
            return False
    
    def connection_loop():
        print(f"Attempting to connect to {ssid} with password {password}.")
        subprocess.run(['sudo','iwlist','wlan0','scan'], check=False)
        connection_attempt_start = time.time()
        while True:
            if time.time() - connection_attempt_start >= retry_timeout:
                if attempt_connection():
                    break
                connection_attempt_start = time.time()
            if get_connected_ssid('wlan0') == ssid:
                print(f"Successfully connected to {ssid}.")
                break
            print("Connecting to WiFi...")
            time.sleep(1)
    
    # Start the connection process in a new thread
    threading.Thread(target=connection_loop).start()

while True:
    # Create a new server socket using RFCOMM protocol
    server_sock = bluetooth.BluetoothSocket(bluetooth.RFCOMM)
    server_port = bluetooth.PORT_ANY

    # Bind to any address and the specified port
    server_sock.bind(("", server_port))

    # Start listening with a backlog of 1 connection
    server_sock.listen(1)

    # Configure Bluetooth service
    bluetooth.advertise_service(server_sock, "RaspberryPiRFCOMMServer",
                                service_id=server_uuid,
                                service_classes=[server_uuid, bluetooth.SERIAL_PORT_CLASS],
                                profiles=[bluetooth.SERIAL_PORT_PROFILE])

    # Output the server's Bluetooth address and the port it's listening on
    print("Waiting for connection on RFCOMM channel %d" % server_sock.getsockname()[1])

    # Accept a connection; this call is blocking
    client_sock, client_info = server_sock.accept()
    print("Accepted connection from ", client_info)

    try:
        while True:
            # Receive data from the client
            data = client_sock.recv(1024)
            if not data:
                break
            print("Received '%s'" % data.decode("utf-8"))

            incoming_data = data.decode("utf-8")

            if incoming_data.startswith("CONNECT_TO_WIFI:"):
                connect_to_wifi(*incoming_data.split(":")[1:])
            elif incoming_data == "WHAT_IS_UR_IP":
                ip_address = get_wlan_ip()
                ssid_name = get_connected_ssid()
                response = f"MY_IP_IS:{ip_address}@{ssid_name}"
                client_sock.send(response.encode("utf-8"))
            else:
                print("Invalid command.")
            
    except OSError as e:
        print("Connection error: ", e)
    finally:
        # Close the client socket
        if client_sock:
            client_sock.close()
        # Close the server socket
        if server_sock:
            server_sock.close()

    print("Disconnected. Restarting...")
