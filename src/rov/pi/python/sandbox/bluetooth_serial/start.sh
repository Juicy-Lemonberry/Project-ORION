#!/bin/bash

# Add Serial Port Profile
sudo sdptool add SP

# Make the device discoverable
sudo hciconfig hci0 piscan

# Run the Bluetooth server script
sudo python3 btserial.py
