# Raspberry Pi Code

This folder contains the code that will run on a Raspberry Pi.

## Prerequisites

Before running the code, make sure you have the following:

- Raspberry Pi board
- Operating system installed (e.g., Raspbian)
- Dependencies installed. (Dependencies can be installed by running 
`setup_dependencies_and_system_config.sh` script in both the `main/bluetooth` and `main/wifi` folders.

## Getting Started

To get started with the code, follow these steps:

1. Clone this repository to your Raspberry Pi.
2. Navigate to the `pi/python/main` folder.
3. Run `sudo ./start.sh` in both `main/bluetooth` and `main/wifi` folders to start the code. 
4. Done. Your Raspberry Pi is now running the code, ready to accept bluetooth pairing commands from the mobile app, and ready to forward them to the Arduino via USB.
