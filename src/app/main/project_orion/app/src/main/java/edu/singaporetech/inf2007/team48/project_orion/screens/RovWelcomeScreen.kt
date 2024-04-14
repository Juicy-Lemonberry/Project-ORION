package edu.singaporetech.inf2007.team48.project_orion.screens

import android.Manifest
import android.content.pm.ActivityInfo
import androidx.compose.foundation.Image
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import edu.singaporetech.inf2007.team48.project_orion.R
import edu.singaporetech.inf2007.team48.project_orion.consts.OrionRovRequiredPermissions
import edu.singaporetech.inf2007.team48.project_orion.consts.OrionRovRequiredPermissions.postBluetoothPermissions
import edu.singaporetech.inf2007.team48.project_orion.consts.OrionRovRequiredPermissions.postTiramisuPermissions
import edu.singaporetech.inf2007.team48.project_orion.controllers.OrionViewModel
import edu.singaporetech.inf2007.team48.project_orion.controllers.xbox.XboxInputViewModel
import edu.singaporetech.inf2007.team48.project_orion.controllers.rov.RovWelcomeScreenViewModel
import edu.singaporetech.inf2007.team48.project_orion.extensionFunctions.context.showToastMessage
import edu.singaporetech.inf2007.team48.project_orion.OrionScreens
import edu.singaporetech.inf2007.team48.project_orion.components.LockScreenOrientation
import edu.singaporetech.inf2007.team48.project_orion.components.OrionTopAppBar

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RovWelcomeScreen(
    orionViewModel: OrionViewModel,
    xboxInputViewModel: XboxInputViewModel,
    navController: NavController
) {
    LockScreenOrientation(orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
    val permissionsState =
        rememberMultiplePermissionsState(permissions = OrionRovRequiredPermissions.permissions)
    val context = LocalContext.current
    val screenViewModel: RovWelcomeScreenViewModel = viewModel()

    UpdatePermissionsToViewModel(
        permissionsState = permissionsState,
        screenViewModel = screenViewModel
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painter = painterResource(id = R.drawable.background_06),
                contentScale = ContentScale.FillHeight,
                alpha = 0.5f
            )
    ) {
        Scaffold(
            topBar = {
                OrionTopAppBar(
                    title = "ROV Connection Setup",
                    onBackClick = { navController.popBackStack() },
                    colours = TopAppBarDefaults.topAppBarColors(
                        containerColor = if (isSystemInDarkTheme()) {
                            Color.Black.copy(alpha = 0.3f)
                        } else {
                            Color.White.copy(alpha = 0.3f)
                        }
                    )
                )

            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
            {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    Text(
                        text = "Welcome to Project Orion's ROV Connection Setup!",
                        style = MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(32.dp))
                    Text(
                        text = "Before we begin, please ensure the following permissions are granted:",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.align(Alignment.CenterHorizontally),
                        textAlign = TextAlign.Center
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    PermissionsRequester(
                        multiplePermissionsState = permissionsState,
                        screenViewModel = screenViewModel,
                        onPermissionGranted = {
                            context.showToastMessage(
                                "Permissions granted"
                            )
                        },
                        onPermissionDenied = {
                            context.showToastMessage(
                                "some permissions denied"
                            )
                        },
                        navController = navController
                    )
                    // Allow Permissions Button / Continue Button
                    Button(
                        onClick = {
                            if (permissionsState.allPermissionsGranted) {
                                navController.navigate(route = OrionScreens.RovQRCodeScreen.route)
                            } else {
                                permissionsState.launchMultiplePermissionRequest()
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (permissionsState.allPermissionsGranted) {
                                Color.Green
                            } else {
                                MaterialTheme.colorScheme.primary
                            }
                        )
                    ) {
                        Text(if (permissionsState.allPermissionsGranted) "LETS GO! ➡" else "Allow permissions",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun PermissionsRequester(
    multiplePermissionsState: MultiplePermissionsState,
    screenViewModel: RovWelcomeScreenViewModel,
    onPermissionGranted: () -> Unit,
    onPermissionDenied: () -> Unit,
    navController: NavController

) {
    val context = LocalContext.current

    Column {
        // Section for Camera permission
        PermissionStatusCard(
            permissionName = "Camera",
            permissionReason = "To scan the pairing QR code and to establish connection to the ROV.",
            permissionIcon = R.drawable.camera_icon,
            isGranted = screenViewModel.cameraPermissionGranted.collectAsState().value
        )

        // Section for Location permissions
        PermissionStatusCard(
            permissionName = "Location Access",
            permissionReason = "To create a local hotspot for ROV data streaming connectivity.",
            permissionIcon = R.drawable.location_icon,
            isGranted = screenViewModel.locationPermissionGranted.collectAsState().value
        )


        // Section for Bluetooth and Nearby Devices permissions
        PermissionStatusCard(
            permissionName = "Bluetooth",
            permissionReason = "To establish serial communication and setup hotspot to the ROV.",
            permissionIcon = R.drawable.bluetooth_icon,
            isGranted = screenViewModel.bluetoothPermissionGranted.collectAsState().value
        )
    }
}


@Preview(showBackground = true)
@Composable
fun PermissionCardPreview() {
    PermissionStatusCard(
        permissionName = "Camera",
        permissionReason = "To scan the pairing QR codes and to establish connection to the ROV.",
        permissionIcon = R.drawable.bluetooth_icon,
        isGranted = true
    )

}

@Composable
fun PermissionStatusCard(
    permissionName: String,
    permissionReason: String,
    permissionIcon: Int,
    isGranted: Boolean,
    iconModifier: Modifier = Modifier
) {
    Row(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            painter = painterResource(id = permissionIcon),
            contentDescription = null,
            colorFilter = if (!isGranted) ColorFilter.colorMatrix(ColorMatrix().apply {
                setToSaturation(
                    0f
                )
            }) else null,
            modifier = iconModifier.size(75.dp)
        )
        Column(
            modifier = Modifier.padding(start = 16.dp)
        ) {
            Text(
                text = permissionName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = permissionReason,
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                text = if (isGranted) "✔ Permission Granted" else "✖ Permission Denied",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = if (isGranted) Color.Green else Color.Red
            )
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun UpdatePermissionsToViewModel(
    permissionsState: MultiplePermissionsState,
    screenViewModel: RovWelcomeScreenViewModel
) {

    screenViewModel.setCameraPermissionGranted(permissionsState.permissions.firstOrNull {
        it.permission == Manifest.permission.CAMERA
    }?.status?.isGranted == true)

    screenViewModel.setLocationPermissionGranted(permissionsState.permissions.firstOrNull {
        it.permission == Manifest.permission.ACCESS_COARSE_LOCATION ||
                it.permission == Manifest.permission.ACCESS_FINE_LOCATION
    }?.status?.isGranted == true)

    val compatiblePermissions = postBluetoothPermissions + postTiramisuPermissions

    // Filtering only the permissions relevant to this section
    val relevantPermissions = permissionsState.permissions.filter {
        it.permission in compatiblePermissions
    }

    val allGranted = relevantPermissions.all { it.status.isGranted }

    screenViewModel.setBluetoothPermissionGranted(allGranted)
}