package edu.singaporetech.inf2007.team48.project_orion.utils

import android.annotation.SuppressLint
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage

/**
 * Analyzes images for QR codes and performs an action when a valid QR code is detected.
 *
 * This class is designed to be used with an ImageAnalysis use case in the CameraX library.
 * When a valid QR code that meets specific criteria is found, it invokes a callback function
 * with the QR code's content.
 *
 * @param onValidQrCodeScanned A callback function to be invoked with the scanned QR code as a string
 *                             if the QR code content starts with "ORION:".
 */
class BarcodeAnalyzer(
    private val onValidQrCodeScanned: (String) -> Unit
) : ImageAnalysis.Analyzer {

    // Configure the barcode scanner to recognize all barcode formats.
    private val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(Barcode.FORMAT_ALL_FORMATS)
        .build()

    // Initialize the barcode scanner with the specified options.
    private val scanner = BarcodeScanning.getClient(options)

    // This annotation suppresses warnings for using experimental or unstable APIs.
    @SuppressLint("UnsafeOptInUsageError")
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            // Converts the camera image to an InputImage.
            val inputImage = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
            // Processes the image to find barcodes.
            scanner.process(inputImage)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        val rawValue = barcode.rawValue
                        // Check if the barcode (QR code) contains the expected prefix.
                        if (rawValue != null && rawValue.startsWith("ORION:")) {
                            // Invoke the callback with the QR code's content.
                            onValidQrCodeScanned(rawValue)
                        }
                    }
                }
                .addOnCompleteListener {
                    // Close the image to free up resources.
                    imageProxy.close()
                }
        }
    }
}

