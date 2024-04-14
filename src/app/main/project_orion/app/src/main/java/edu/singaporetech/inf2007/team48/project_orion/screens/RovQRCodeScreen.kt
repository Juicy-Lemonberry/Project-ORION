package edu.singaporetech.inf2007.team48.project_orion.screens

import androidx.compose.material3.ExperimentalMaterial3Api
import android.content.pm.ActivityInfo
import android.util.Log
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FlashlightOff
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import edu.singaporetech.inf2007.team48.project_orion.controllers.OrionViewModel
import edu.singaporetech.inf2007.team48.project_orion.controllers.xbox.XboxInputViewModel
import edu.singaporetech.inf2007.team48.project_orion.utils.BarcodeAnalyzer
import edu.singaporetech.inf2007.team48.project_orion.extensionFunctions.context.vibratePhone
import edu.singaporetech.inf2007.team48.project_orion.OrionScreens
import edu.singaporetech.inf2007.team48.project_orion.components.LockScreenOrientation
import edu.singaporetech.inf2007.team48.project_orion.components.OrionTopAppBar


// Source code referenced from
// https://medium.com/@alexander13oster/compose-barcode-scanner-is-simple-866f20dba6d8
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RovQRCodeScreen(
    orionViewModel: OrionViewModel,
    xboxInputViewModel: XboxInputViewModel,
    navController: NavController
) {
    LockScreenOrientation(orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

    val localContext = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val cameraProviderFuture = remember { ProcessCameraProvider.getInstance(localContext) }
    val isTorchOn = remember { mutableStateOf(false) }
    val camera = remember { mutableStateOf<Camera?>(null) } // Remember the Camera instance

    Scaffold(
        topBar = {
            OrionTopAppBar(
                title = "Scan ROV QR Code",
                onBackClick = { navController.popBackStack() },
                colours = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (isSystemInDarkTheme()) Color.Black.copy(alpha = 0.3f) else Color.White.copy(
                        alpha = 0.3f
                    ),
                    titleContentColor = Color.White,
                )
            )
        },
        containerColor = Color.Transparent
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize()) {
            // Camera preview
            AndroidView(
                modifier = Modifier.fillMaxSize(),
                factory = { context ->
                    PreviewView(context).apply {
                        val preview = Preview.Builder().build()
                        preview.setSurfaceProvider(surfaceProvider)

                        runCatching {
                            val cameraProvider = cameraProviderFuture.get()
                            cameraProvider.unbindAll()
                            camera.value = cameraProvider.bindToLifecycle(
                                lifecycleOwner,
                                CameraSelector.DEFAULT_BACK_CAMERA,
                                Preview.Builder().build().also { it.setSurfaceProvider(surfaceProvider) },
                                ImageAnalysis.Builder().build().also { analysis ->
                                    analysis.setAnalyzer(ContextCompat.getMainExecutor(context), BarcodeAnalyzer{ qrCode ->
                                        // Analyzer Debouncing logic to prevent multiple navigation calls
                                        if (orionViewModel.hasNavigatedAwayAfterScanning) return@BarcodeAnalyzer
                                        orionViewModel.onNavigatedAwayAfterScanning()
                                        // Assuming the QR Code is already validated to start with "ORION:"
                                        // And that the return string is the MAC address of the ROV
                                        Log.d("QR_CODE", "Valid QR Code Scanned: $qrCode")
                                        context.vibratePhone(250L)
                                        orionViewModel.setCamQrCodeScannedString(qrCode)
                                        navController.navigate(OrionScreens.RovConnectingScreen.route)
                                    })
                                }
                            )
                            // Initial torch state setup
                            camera.value?.cameraControl?.enableTorch(isTorchOn.value)
                        }.onFailure {
                            Log.e("CAMERA", "Camera initialization error: ${it.localizedMessage}", it)
                        }
                    }
                }
            )

            // QR Code aiming box and flashlight toggle button here
            // Overlay UI elements on the camera preview
            // Example: QR Code aiming box at the center
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .size(250.dp) // Adjust size accordingly
                    .border(2.dp, Color.Green) // A simple green box as aiming guide
                    .padding(paddingValues)
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter)
                    .background(Color.Black.copy(alpha = 0.5f)),
                horizontalAlignment = Alignment.CenterHorizontally,
            ){
                Text(text = "Point your camera towards the QR code shown on the ROV",
                    style = MaterialTheme.typography.titleLarge,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(16.dp,32.dp),
                    color = Color.White
                )
                // Flashlight toggle button
                FloatingActionButton(
                    onClick = {
                        // Toggle the state
                        isTorchOn.value = !isTorchOn.value
                    },
                    // Modifier and background color...
                ) {
                    Icon(
                        // Change the icon based on the torch state
                        imageVector = if (isTorchOn.value) Icons.Default.FlashlightOff else Icons.Default.FlashlightOn,
                        contentDescription = "Toggle Flashlight"
                    )
                }
                Spacer(modifier = Modifier.size(32.dp))

            }
        }
    }
    // Observe torch state and update when changed
    LaunchedEffect(isTorchOn.value) {
        runCatching {
            camera.value?.cameraControl?.enableTorch(isTorchOn.value)
        }.onFailure {
            Log.e("CAMERA", "Failed to toggle flashlight: ${it.localizedMessage}", it)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            runCatching {
                val cameraProvider = cameraProviderFuture.get()
                cameraProvider.unbindAll() // Ensure to unbind the camera when the composable disposes
            }
        }
    }
}
