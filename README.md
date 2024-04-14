# Project ORION (Onsite Remote InspectiON)

## Overview

Project ORION is a culmination of our team's effort to bridge the gap between theoretical knowledge and practical application in the mechanical engineering curriculum. It is a Remotely Operated Vehicle (ROV) system designed to aid tertiary education by allowing students to safely conduct under-vehicle inspections. The project integrates a mobile application with the ROV to provide live footage, enhancing both safety and learning efficiency.

## Project Components

- **Mobile Application**: A custom-developed application for controlling the ROV and viewing live footage of under-vehicle inspections.
- **Remotely Operated Vehicle (ROV)**: A Raspberry Pi-based vehicle equipped with a camera for live streaming under-vehicle inspections.
- **Backend Server**: Supports the mobile application by handling data storage, retrieval, and user authentication.

## Features

- **Live Streaming**: Provides real-time footage from the ROV to the mobile application, enabling detailed inspections.
- **QR Code Connection**: Simplifies the process of linking the mobile application to the ROV.
- **Xbox Controller Compatibility**: Allows users to intuitively control the ROV using a familiar game controller.
- **Maintenance Checklist Integration**: Enables students to interact with and complete maintenance checklists through the app.
- **Documentation and Reporting**: Facilitates the documentation of inspections.

## Installation and Setup

Given the project's educational purpose, installation and setup procedures are tailored for demonstration and evaluation within a controlled environment, such as a classroom or lab.

### Prerequisites

- A compatible smartphone or tablet for the mobile application.
- Access to a Wi-Fi network for communication between the mobile app and ROV.
- An Xbox controller for navigating the ROV.

### Steps

1. **Clone the Project Repository**: Start by cloning the project repository to access the code for the mobile application, ROV firmware, and server.
2. **Server Setup**:

   - Navigate to `/main/src/server` in the cloned repository to find the backend server code.
   - Follow the README or setup instructions located within this directory to get the server up and running.

3. **ROV Firmware Setup**:

   - The ROV code is split between the Raspberry Pi and the ESP32 motor controller.
   - For the Raspberry Pi, go to `/main/src/rov/pi`. Follow the instructions for setting up the Raspberry Pi to work with the ROV.
   - For the ESP32 motor controller, access `/main/src/rov/arduino`. There, you will find the necessary code and setup instructions to configure the ESP32 for controlling the ROV's motors.

4. **Mobile Application Setup**:

   - The mobile application code is located at `/main/src/app/main/project_orion`.
   - This is an Android Studio project. Open Android Studio, navigate to 'File > Open...', and select the `project_orion` project directory.
   - Follow the build and run instructions within the project to install the application on a compatible Android device.

5. **Network Configuration**:

   - Ensure all devices (the server, the ROV, and the mobile application device) are connected to the same Wi-Fi network.
   - This is crucial for the successful communication between the mobile application, the ROV, and the backend server.

6. **Establishing Connections**:
   - With the server running, the ROV set up, and the mobile application installed, you should now be able to establish a connection between the mobile application and the ROV through the server.
   - Follow any additional instructions provided in the project documentation to ensure proper connectivity and functionality.

By following these steps, you will have successfully set up the server, ROV, and mobile application components of Project ORION. For detailed operation and troubleshooting instructions, refer to the specific README files within each directory.

## Demonstration and Usage

For demonstration purposes, we suggest outlining a series of tasks that showcase the project's capabilities, such as:

1. Establishing a connection between the mobile app and the ROV using QR code scanning.
2. Navigating the ROV to a specific location using the Xbox controller.
3. Conducting a mock inspection by viewing live footage.
4. Completing an item on the maintenance checklist based on the inspection findings.

## Development Process

Our development followed agile methodologies, focusing on iterative progress through sprints. Each sprint targeted specific features and integrations, beginning with infrastructure setup, continuing through core development and hardware integration, and culminating in refinement and testing.

![Team Workflow](/docs/img/TeamFlow.png)

In addition to our sprint workflow, we developed a robust System Architecture to ensure seamless integration across all components of Project ORION. The architecture includes a Backend Server, a User/Operator interface through the Mobile Application, and the Robot Car subsystem. The Backend Server utilizes a Cloud Server with MySQL for storage and a BaaS Python Server for backend processing. The Mobile Application connects with the Robot Car via mobile Wi-Fi and is also responsible for user input through a USB/Bluetooth-connected controller. The Robot Car itself is composed of a Raspberry Pi as the main controller, interfaced with a Pi Camera Module and a Camera Mount System, all coordinated through a Robot Car Drive System.

![System Architecture Diagram](/docs/img/SystemArchitectureDiagram.png)

## Team Contribution

**Team Members**:

- [@NekoCoaster](https://github.com/NekoCoaster)
- [@wqyeo](https://github.com/wqyeo)
- [@Zhwee](https://github.com/Zhwee)
- [@minvee](https://github.com/minvee)
- [@vianiecetan](https://github.com/vianiecetan)

**Acknowledgments**: Special thanks to our professors and advisors for their guidance, as well as to all team members for their dedication and hard work.

## Conclusion

Project ORION exemplifies the practical application of mechanical engineering principles in tertiary education, enhancing learning experiences by incorporating technology into traditional curricula.

## Video Demonstration

[![Watch the video](https://img.youtube.com/vi/8pS9xiDiqYE/maxresdefault.jpg)](https://youtu.be/8pS9xiDiqYE)