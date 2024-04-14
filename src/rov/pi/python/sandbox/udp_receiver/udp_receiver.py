import socket

# The IP address '' means to listen on all available interfaces
HOST = '0.0.0.0'  # Symbolic name meaning all available interfaces
PORT = 5000 # Arbitrary non-privileged port

# Create a socket for UDP
# socket.AF_INET indicates that we want an IPv4 socket
# socket.SOCK_DGRAM indicates that this will be a UDP socket
sock = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)

# Bind the socket to the host and port
sock.bind((HOST, PORT))

print(f"Listening for UDP packets on port {PORT}...")

# Loop forever, listening for packets
try:
    while True:
        # Wait for a packet
        data, addr = sock.recvfrom(1024) # buffer size is 1024 bytes

        # Print the received data and the address of the sender
        print(f"Received message: {data.decode()} from {addr}")

except KeyboardInterrupt:
    print("\nServer is stopping.")

finally:
    sock.close()
