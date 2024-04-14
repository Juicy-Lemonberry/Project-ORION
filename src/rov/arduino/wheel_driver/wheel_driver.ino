#include "rov_drive_train.h"

void setup()
{
  Serial.begin(115200); // Start serial communication at 115200 baud
  Serial.println("Waiting 5 seconds for the drive train to initialize...");
  delay(5000);
  driveTrainInit(); // Initialize the drive train system
  delay(1000);
}

void loop()
{
  driveTrainSelfTest(); // Perform the drive train self-test
  delay(5000);          // Wait for 5 seconds before repeating the test
}
