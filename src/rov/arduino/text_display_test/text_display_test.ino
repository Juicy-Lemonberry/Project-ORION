#include <TFT_eSPI.h> // Include the graphics library (this includes the SPI library)

TFT_eSPI tft = TFT_eSPI();  // Create object "tft"

void setup() {
  tft.init();           // Initialize the display
  tft.setRotation(3);   // Set display rotation (0-3). Adjust according to your display setup.
  tft.fillScreen(TFT_BLACK); // Clear the screen to black

  // Set the text color to white, text background to black and the font size to 4
  tft.setTextColor(TFT_WHITE, TFT_BLACK); 
  tft.setTextSize(2);

  // Set the cursor where the text will start
  tft.setCursor(0, 0);

  // Print a text string on the display
  tft.println("Hello, World!");

  // You can change the cursor position and print more text
  tft.setCursor(0, 30); // Move the cursor down and to the left
  tft.println("TFT_eSPI Library");
}

void loop() {
  // The loop is empty because we're just displaying static text
}
