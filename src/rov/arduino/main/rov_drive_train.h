#include <Arduino.h>
#include <Wire.h>
#include <cmath>
#ifndef ROV_DRIVE_TRAIN_H
#define ROV_DRIVE_TRAIN_H
// Schematics of pins goes as follows:
// L_* are the left wheels
// R_* are the right wheels
// _M1 are the front wheels (1 as in "first")
// _M2 are the back wheels (2 as in "second")
// _A are the forward pins (A for "Advance")
// _B are the backward pins (B for "Backward")

// Following the schematic above, here below are the pins as wired between the MakerDriver and the ESP32
#define L_M1A 26
#define L_M1B 25
#define L_M2A 33
#define L_M2B 32

#define R_M1A 12
#define R_M1B 13
#define R_M2A 15
#define R_M2B 2

// on-board relay switch that turns on power from the batteries to the motor drivers and motors
#define driveTrainPowerPin 27

// Additional Aliases for development ease
#define frontLeftForward L_M1A
#define frontLeftBackward L_M1B
#define frontRightForward R_M1A
#define frontRightBackward R_M1B

#define rearLeftForward L_M2A
#define rearLeftBackward L_M2B
#define rearRightForward R_M2A
#define rearRightBackward R_M2B

// Total number of driver pins.
#define TOTAL_DRIVETRAIN_PINS 8

// Struct to store the pins for each wheel
struct WheelPinsPair
{
    const int forwardPin;
    const int backwardPin;
    const int forwardPinChannel;
    const int backwardPinChannel;

    // Define equality comparison operator for WheelPinsPair
    bool operator==(const WheelPinsPair &other) const
    {
        return forwardPin == other.forwardPin && backwardPin == other.backwardPin;
    }
};

// Struct to store the pins for each wheel
struct WheelSide
{
    const int left = 0;
    const int right = 1;
    // Define equality comparison operator for WheelSide
    bool operator==(const WheelSide &other) const
    {
        return left == other.left && right == other.right;
    }

    // Define inequality comparison operator for WheelSide
    bool operator!=(const WheelSide &other) const
    {
        return left != other.left || right != other.right;
    }
};

// Class to hold the pins for each wheel
class WheelPinsPairSet
{
public:
    const WheelPinsPair frontLeft = {frontLeftForward, frontLeftBackward, 0, 1};
    const WheelPinsPair frontRight = {frontRightForward, frontRightBackward, 2, 3};
    const WheelPinsPair rearLeft = {rearLeftForward, rearLeftBackward, 4, 5};
    const WheelPinsPair rearRight = {rearRightForward, rearRightBackward, 6, 7};
};

// Class to hold HIGH LOW configurations for each wheel direction
class WheelDirection
{
public:
    const WheelPinsPair N = {LOW, LOW, -1, -1};
    const WheelPinsPair D = {HIGH, LOW, -1, -1};
    const WheelPinsPair R = {LOW, HIGH, -1, -1};
};

// Class to hold power configuration for the drive train power relay
class DriveTrainPowerMode
{
public:
    const int On = LOW;
    const int Off = HIGH;
};

extern WheelPinsPairSet wheelPin;
extern WheelDirection wheelDirection;
extern DriveTrainPowerMode driveTrainPowerMode;
extern WheelSide wheelSide;

// Initializes the drive train system, setting up PWM for motor control pins and the power pin
void driveTrainInit();

// Sets the drive train power mode to either on or off
void setDriveTrainPowerMode(int driveTrainPowerMode);

// Sets the speed and direction for a specific wheel
bool setPerWheel(WheelPinsPair wheel, WheelPinsPair dir, int speed);

// Sets the speed for a specific wheel; direction is determined by the sign of the speed
bool setPerWheel(WheelPinsPair wheel, int speed);

// Sets the speed and direction for all wheels on a specified side of the vehicle
bool setPerSide(int wheelSide, WheelPinsPair dir, int speed);

// Sets the speed for all wheels on a specified side of the vehicle; direction is determined by the sign of the speed
bool setPerSide(int wheelSide, int speed);

// Sets the speed and direction for all wheels on the vehicle
bool setAll(WheelPinsPair dir, int speed);

// Sets the speed for all wheels on the vehicle; direction is determined by the sign of the speed
bool setAll(int speed);

// Sets the speed and direction for each wheels using raw duty cycle values
bool setAll(int frontLeftSpeed, int frontRightSpeed, int rearLeftSpeed, int rearRightSpeed);

// Sets the speed and direction for each wheels using percentage values
bool setAllPercentage(int frontLeftSpeed, int frontRightSpeed, int rearLeftSpeed, int rearRightSpeed);

// Stops all motion by setting the speed of all wheels to zero
bool stopAll();

// Converts a percentage value to a PWM duty cycle value appropriate for the current PWM resolution
int getDutyCycle(int percentage);

// Performs a self-test of the drive train by cycling through a range of speeds in both forward and reverse directions
void driveTrainSelfTest();

#endif
