#!/bin/bash

# Update and install dependencies
sudo apt-get update
sudo apt-get install -y python3-pip libbluetooth-dev

# Install PyBluez
pip install git+https://github.com/pybluez/pybluez.git#egg=pybluez

# Stop Bluetooth service
sudo systemctl stop bluetooth

# Modify the bluetooth.service to enable SP profile support
sudo sed -i 's|^ExecStart=/usr/lib/bluetooth/bluetoothd.*|ExecStart=/usr/lib/bluetooth/bluetoothd -C --noplugin=sap|' /lib/systemd/system/bluetooth.service

# Reload system daemon and start Bluetooth service
sudo systemctl daemon-reload
sudo systemctl start bluetooth

echo "Setup completed. Please manually check /lib/systemd/system/bluetooth.service to ensure the modifications are correct."
