#include "rov_drive_train.h"
#include <Arduino.h>
#include <Wire.h>
#include <cmath>

#define PWM_FREQ 20000
#define PWM_RESOLUTION 8 // 4-bit resolution (0 - 255)

WheelPinsPairSet wheelPin;
WheelDirection wheelDirection;
DriveTrainPowerMode driveTrainPowerMode;
WheelSide wheelSide;

static int allDriveTrainPins[] = {
    frontLeftForward, frontLeftBackward,
    frontRightForward, frontRightBackward,
    rearLeftForward, rearLeftBackward,
    rearRightForward, rearRightBackward};

int PWM_MAX_DUTY = int(pow(2, PWM_RESOLUTION) - 1);
/* Some house keeping:
 * The drive train has an independent power switch defined by the driveTrainPowerPin.
 * The drive train has 4 wheels, each with a forward and backward pin.
 * The 'lower-level' way to control the drive train is to specify the target wheel, the direction, and the magnitude of the speed.
 * The 'Mid-level' way is to control the wheels is to specify targeting either the left or right side of the vehicle, the direction, and the magnitude of the speed.
 * The 'High-level' way is to control the entire vehicle by specifying the direction and the magnitude of the speed.
 */

// Sets up the PWM functionality for the specified wheel
void setupPwm(WheelPinsPair wheelInfo)
{
    ledcSetup(wheelInfo.forwardPinChannel, PWM_FREQ, PWM_RESOLUTION);
    ledcAttachPin(wheelInfo.forwardPin, wheelInfo.forwardPinChannel);
    ledcSetup(wheelInfo.backwardPinChannel, PWM_FREQ, PWM_RESOLUTION);
    ledcAttachPin(wheelInfo.backwardPin, wheelInfo.backwardPinChannel);
}

// Sets up all the required pins for the drive train to operate
void driveTrainInit()
{
    // setup the power pin
    pinMode(driveTrainPowerPin, OUTPUT);
    // setup the drive train pins and Keep trying to initialize the drive train until it is successful
    bool attached = false;
    while (!attached) //
    {
        Serial.println("Initializing drive train...");

        setupPwm(wheelPin.frontLeft);
        setupPwm(wheelPin.frontRight);
        setupPwm(wheelPin.rearLeft);
        setupPwm(wheelPin.rearRight);

        attached = true;
    }
    Serial.println("Drive train initialized successfully.");
    setDriveTrainPowerMode(driveTrainPowerMode.On);
}

void setDriveTrainPowerMode(int driveTrainPowerMode)
{
    digitalWrite(driveTrainPowerPin, driveTrainPowerMode);
}

// return true if the speed is set successfully, false otherwise
bool setPerWheel(WheelPinsPair wheel, WheelPinsPair dir, int speed)
{
    if (speed < 0 || speed > PWM_MAX_DUTY)
    {
        Serial.println("Invalid speed value: " + String(speed) + ". Speed must be between 0 and " + String(PWM_MAX_DUTY) + ".");
        return false;
    }

    if (dir == wheelDirection.N)
    {
        ledcWrite(wheel.forwardPinChannel, 0);
        ledcWrite(wheel.backwardPinChannel, 0);
    }
    else if (dir == wheelDirection.D)
    {
        ledcWrite(wheel.forwardPinChannel, speed);
        ledcWrite(wheel.backwardPinChannel, 0);
    }
    else if (dir == wheelDirection.R)
    {
        ledcWrite(wheel.forwardPinChannel, 0);
        ledcWrite(wheel.backwardPinChannel, speed);
    }
    return true;
}

// return true if the speed is set successfully, false otherwise
bool setPerWheel(WheelPinsPair wheel, int speed)
{
    if (speed < PWM_MAX_DUTY * -1 || speed > PWM_MAX_DUTY)
    {
        Serial.println("Invalid speed value: " + String(speed) + ". Speed must be between 0 and " + String(PWM_MAX_DUTY) + ".");
        return false;
    }
    if (speed > 0)
    {
        return setPerWheel(wheel, wheelDirection.D, abs(speed));
    }
    else if (speed < 0)
    {
        return setPerWheel(wheel, wheelDirection.R, abs(speed));
    }
    else
    {
        return setPerWheel(wheel, wheelDirection.N, 0);
    }
}

// return true if the speed is set successfully, false otherwise
bool setPerSide(int wheelside, WheelPinsPair dir, int speed)
{
    if (wheelside == wheelSide.left)
    {
        return setPerWheel(wheelPin.frontLeft, dir, speed) &&
               setPerWheel(wheelPin.rearLeft, dir, speed);
    }
    else if (wheelside == wheelSide.right)
    {
        return setPerWheel(wheelPin.frontRight, dir, speed) &&
               setPerWheel(wheelPin.rearRight, dir, speed);
    }
    else
    {
        Serial.println("Invalid side value: " + String(wheelside) + ". Side must be either 0 for left or 1 for right.");
        return false;
    }
}
// return true if the speed is set successfully, false otherwise
bool setPerSide(int wheelside, int speed)
{
    if (wheelside == wheelSide.left)
    {
        return setPerWheel(wheelPin.frontLeft, speed) &&
               setPerWheel(wheelPin.rearLeft, speed);
    }
    else if (wheelside == wheelSide.right)
    {
        return setPerWheel(wheelPin.frontRight, speed) &&
               setPerWheel(wheelPin.rearRight, speed);
    }
    else
    {
        Serial.println("Invalid side value: " + String(wheelside) + ". Side must be either 0 for left or 1 for right.");
        return false;
    }
}

// return true if the speed is set successfully, false otherwise
bool setAll(WheelPinsPair dir, int speed)
{
    return setPerWheel(wheelPin.frontLeft, dir, speed) &&
           setPerWheel(wheelPin.frontRight, dir, speed) &&
           setPerWheel(wheelPin.rearLeft, dir, speed) &&
           setPerWheel(wheelPin.rearRight, dir, speed);
}

// return true if the speed is set successfully, false otherwise
bool setAll(int speed)
{
    return setPerWheel(wheelPin.frontLeft, speed) &&
           setPerWheel(wheelPin.frontRight, speed) &&
           setPerWheel(wheelPin.rearLeft, speed) &&
           setPerWheel(wheelPin.rearRight, speed);
}

// return true if the speed is set successfully, false otherwise
bool setAll(int frontLeftSpeed, int frontRightSpeed, int rearLeftSpeed, int rearRightSpeed)
{
    return setPerWheel(wheelPin.frontLeft, frontLeftSpeed) &&
           setPerWheel(wheelPin.frontRight, frontRightSpeed) &&
           setPerWheel(wheelPin.rearLeft, rearLeftSpeed) &&
           setPerWheel(wheelPin.rearRight, rearRightSpeed);
}

// return true if the speed is set successfully, false otherwise
bool setAllPercentage(int frontLeftSpeed, int frontRightSpeed, int rearLeftSpeed, int rearRightSpeed)
{
    return setAll(
        getDutyCycle(frontLeftSpeed),
        getDutyCycle(frontRightSpeed),
        getDutyCycle(rearLeftSpeed),
        getDutyCycle(rearRightSpeed));
}

// return true if the speed is set successfully, false otherwise
bool stopAll()
{
    return setAll(wheelDirection.N, 0);
}

// returns the pwm duty cycle based on percentage
// For some reason, 100% of 255 outputs 200, rather than 255, but I'll consider this as an over-current protection feature, rather than a bug.
// Think of it as a 100% speed limiter. (Or as I would like to call it, MCT Mode.)
// For full 255 or -255, user should input either anything that's greater than 100 or less than -100. (Or as I would like to call it, TOGA Mode.)
int getDutyCycle(int input)
{
    bool isNegative = input < 0;
    // Serial.println("Input: " + String(input));
    //   skip the calculation if value exceeds the input range
    if (input > 100)
    {
        // Serial.println("Output: " + String(PWM_MAX_DUTY) + "[TOGA]. Negative = " + String(isNegative));
        return PWM_MAX_DUTY;
    }
    else if (input < -100)
    {
        // Serial.println("Output: " + String(PWM_MAX_DUTY * -1) + "[TOGA]. Negative = " + String(isNegative));
        return PWM_MAX_DUTY * -1;
    }
    // Fk this shit, My Pepega Brain is too dumb for this math. I'm just doing monke style
    int input_start = 0;
    int input_end = 100;
    int output_start = 0;
    int output_end = PWM_MAX_DUTY;
    input = abs(input);
    int output = output_start + ((output_end - output_start) / (input_end - input_start)) * (input - input_start);
    // Serial.println("Output: " + String(isNegative ? output * -1 : output) + ". Negative = " + String(isNegative));
    return isNegative ? output * -1 : output;
}

// Perform a self-test on the drive train in steps of 25% of the maximum duty cycle.
// Set all wheels to 0, 25%, 50%, 75%, and 100% fowards, then 0, 25%, 50%, 75%, and 100% backwards.
// Each step is held for 3 second.
void driveTrainSelfTest()
{
    Serial.println("Starting drive train self-test...");
    setAll(getDutyCycle(0));
    delay(3000);
    setAll(getDutyCycle(25));
    delay(3000);
    setAll(getDutyCycle(50));
    delay(3000);
    setAll(getDutyCycle(75));
    delay(3000);
    setAll(getDutyCycle(100));
    delay(3000);
    setAll(getDutyCycle(101));
    delay(3000);
    setAll(getDutyCycle(100));
    delay(3000);
    setAll(getDutyCycle(75));
    delay(3000);
    setAll(getDutyCycle(50));
    delay(3000);
    setAll(getDutyCycle(25));
    delay(3000);
    setAll(getDutyCycle(0));
    delay(3000);
    setAll(getDutyCycle(-25));
    delay(3000);
    setAll(getDutyCycle(-50));
    delay(3000);
    setAll(getDutyCycle(-75));
    delay(3000);
    setAll(getDutyCycle(-100));
    delay(3000);
    setAll(getDutyCycle(-101));
    delay(3000);
    setAll(getDutyCycle(-100));
    delay(3000);
    setAll(getDutyCycle(-75));
    delay(3000);
    setAll(getDutyCycle(-50));
    delay(3000);
    setAll(getDutyCycle(-25));
    delay(3000);
    setAll(getDutyCycle(0));
    Serial.println("Drive train self-test completed.");
}