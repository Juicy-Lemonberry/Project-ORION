#!/bin/bash

# Add Serial Port Profile
sudo sdptool add SP

# Make the device discoverable
sudo hciconfig hci0 piscan

# Run the main script
sudo python3 main.py
