#include "rov_drive_train.h"
#include <TFT_eSPI.h> // Include the graphics library

TFT_eSPI tft = TFT_eSPI(); // Create object "tft"

// Declare global volatile variables for motor speeds
volatile int FRONT_LEFT_MOTOR_SP = 0;
volatile int FRONT_RIGHT_MOTOR_SP = 0;
volatile int REAR_LEFT_MOTOR_SP = 0;
volatile int REAR_RIGHT_MOTOR_SP = 0;
volatile unsigned long lastMessageTime = 0;
volatile int SCRIPT_ACTIVE_MESSAGE_SENT = 0;
void setup()
{
  Serial.begin(115200);      // Initialize serial communication at 115200 baud
  driveTrainInit();          // Initialize the drive train
  tft.init();                // Initialize the display
  tft.setRotation(3);        // Set display rotation (0-3). Adjust according to your display setup.
  tft.fillScreen(TFT_BLACK); // Clear the screen to black
  tft.setTextColor(TFT_WHITE, TFT_BLACK);
  tft.setTextSize(2);

  displaySSIDAndIPs("N/A", "N/A", "N/A");
  displayEcamMessage("SCRIPT INOP", TFT_RED, TFT_BLACK);

  // Create two tasks for Thread 1 and Thread 2
  xTaskCreatePinnedToCore(
      TaskSerialRead,   /* Task function */
      "TaskSerialRead", /* Name of the task */
      10000,            /* Stack size of task */
      NULL,             /* Parameter of the task */
      1,                /* Priority of the task */
      NULL,             /* Task handle to keep track of created task */
      0);               /* Core where the task should run */

  xTaskCreatePinnedToCore(
      TaskMotorControl,
      "TaskMotorControl",
      10000,
      NULL,
      1,
      NULL,
      1);
}

void loop()
{
  // Empty. Everything is handled by FreeRTOS tasks.
}

// Task 1: Read serial input and assign values to global variables
void TaskSerialRead(void *pvParameters)
{
  (void)pvParameters;
  char inputBuffer[256]; // Buffer for serial input
  while (1)
  {
    if (Serial.available() > 0)
    {
      size_t length = Serial.readBytesUntil('\n', inputBuffer, sizeof(inputBuffer) - 1);
      inputBuffer[length] = '\0'; // Null-terminate the string

      // Update the lastMessageTime every time a new message is received
      lastMessageTime = millis();

      // Check if a comma is present within the first 4 characters
      bool commaFound = false;
      for (int i = 0; i < 4 && i < length; ++i)
      {
        if (inputBuffer[i] == ',')
        {
          commaFound = true;
          break;
        }
      }
      if(SCRIPT_ACTIVE_MESSAGE_SENT == 0)
      {        
          displayEcamMessage("SCRIPT ACTIVE", TFT_GREEN, TFT_BLACK);
          SCRIPT_ACTIVE_MESSAGE_SENT = 1;
      }
      if (!commaFound)
      {
        // No comma found within the first 4 characters,
        String inputStr(inputBuffer); // Convert char array to String for easy manipulation
        if (inputStr.startsWith("SSID:"))
        {
          // Serial.println("IP Address and SSID received");
          // Handle SSID and IP address parsing
          String inputStr(inputBuffer);
          int ssidEndIndex = inputStr.indexOf(",IP:");
          String ssid = inputStr.substring(5, ssidEndIndex);
          inputStr.remove(0, ssidEndIndex + 4); // Remove the SSID part and ",IP:"
          int commaIndex = inputStr.indexOf(',');
          String wlan0_ip = inputStr.substring(0, commaIndex);
          String eth0_ip = inputStr.substring(commaIndex + 1);

          // Assuming you have a function to display SSID along with IPs
          displaySSIDAndIPs(ssid, wlan0_ip, eth0_ip);
        }
        else if (inputStr.startsWith("ECAM:"))
        {
          // Extract everything after "ECAM:"
          String ecamMessage = inputStr.substring(5); // Remove "ECAM:" part

          // Call displayEcamMessage with the extracted message
          displayEcamMessage(ecamMessage, TFT_CYAN, TFT_BLACK);
        }
        // perform a console readback.

        Serial.println(inputBuffer);
      }
      else
      {
        // Attempt to parse the input buffer into integers
        int parsedItems = sscanf(inputBuffer, "%d,%d,%d,%d", &FRONT_LEFT_MOTOR_SP, &FRONT_RIGHT_MOTOR_SP, &REAR_LEFT_MOTOR_SP, &REAR_RIGHT_MOTOR_SP);
        if (parsedItems < 4)
        {
          // If less than 4 items were successfully parsed, assume it's a readback request.
          Serial.println(inputBuffer);
        }
      }
    }
    vTaskDelay(10 / portTICK_PERIOD_MS); // Short delay to prevent task from hogging CPU
  }
}

// Task 2: Continuously update motor speeds based on global variables
void TaskMotorControl(void *pvParameters)
{
  (void)pvParameters;
  while (1)
  {
    // Check if more than 1 second has passed since the last message
    if ((millis() - lastMessageTime) > 1000)
    {
      // Reset motor speeds if no message has been received for more than 1 second
      setAllPercentage(0, 0, 0, 0);
      if (SCRIPT_ACTIVE_MESSAGE_SENT == 1){
          displayEcamMessage("SCRIPT INOP", TFT_RED, TFT_BLACK);
          SCRIPT_ACTIVE_MESSAGE_SENT = 0;
          }
    }
    else
    {
      // Otherwise, continue updating the motor speeds as per the last command
      setAllPercentage(FRONT_LEFT_MOTOR_SP, FRONT_RIGHT_MOTOR_SP, REAR_LEFT_MOTOR_SP, REAR_RIGHT_MOTOR_SP);
    }
    vTaskDelay(100 / portTICK_PERIOD_MS); // Adjust motor speeds at a fixed rate
  }
}

void displaySSIDAndIPs(const String &ssid, const String &wlan0_ip, const String &eth0_ip)
{
  tft.setTextColor(TFT_WHITE, TFT_BLACK);
  tft.fillScreen(TFT_BLACK); // Clear the screen
  tft.setCursor(0, 0);       // Reset cursor to top-left

  // Display the SSID
  tft.println("SSID: " + ssid);

  // Display the wlan0 IP address
  tft.println("wlan0:");
  tft.println(wlan0_ip);

  // Move to the next line for eth0 IP display
  tft.println("eth0:");
  tft.println(eth0_ip);
}

void displayEcamMessage(const String &ecam, uint16_t fgcolor, uint16_t bgcolor)
{
  tft.setCursor(0, 0);       // Reset cursor to top-left
  tft.println();
  tft.println();
  tft.println();
  tft.println();
  tft.println();
  tft.println("                     "); // Clear any previously lingering messages
  tft.println("                     ");  
  
  tft.setCursor(0, 0);       // Reset cursor to top-left
  tft.println();
  tft.println();
  tft.println();
  tft.println();
  tft.println();
  // Prints any messages from script
  tft.setTextColor(fgcolor, bgcolor);
  tft.println("ECAM:");
  tft.println(ecam);
}
