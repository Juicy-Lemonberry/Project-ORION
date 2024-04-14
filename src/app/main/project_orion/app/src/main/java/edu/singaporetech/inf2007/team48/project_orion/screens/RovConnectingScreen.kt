package edu.singaporetech.inf2007.team48.project_orion.screens

import edu.singaporetech.inf2007.team48.project_orion.controllers.bluetooths.BluetoothViewModel
import android.content.pm.ActivityInfo
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import edu.singaporetech.inf2007.team48.project_orion.R
import edu.singaporetech.inf2007.team48.project_orion.controllers.OrionViewModel
import edu.singaporetech.inf2007.team48.project_orion.controllers.hotspot.HotspotViewModel
import edu.singaporetech.inf2007.team48.project_orion.controllers.xbox.XboxInputViewModel
import edu.singaporetech.inf2007.team48.project_orion.controllers.rovConnection.RovConnectingScreenViewModel
import edu.singaporetech.inf2007.team48.project_orion.controllers.rovConnection.RovConnectingScreenViewModelFactory
import edu.singaporetech.inf2007.team48.project_orion.OrionScreens
import edu.singaporetech.inf2007.team48.project_orion.components.LockScreenOrientation
import edu.singaporetech.inf2007.team48.project_orion.components.OrionTopAppBar
import kotlinx.coroutines.delay

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RovConnectingScreen(
    navController: NavController,
    xboxInputViewModel: XboxInputViewModel,
    orionViewModel: OrionViewModel,
    bluetoothViewModel: BluetoothViewModel,
    hotspotViewModel: HotspotViewModel
) {
    LockScreenOrientation(orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
    val screenViewModel: RovConnectingScreenViewModel = viewModel(
        factory = RovConnectingScreenViewModelFactory(
            orionViewModel = orionViewModel,
            bluetoothViewModel = bluetoothViewModel,
            hotspotViewModel = hotspotViewModel
        )
    )
    val context = LocalContext.current
    val loadingProgressBackgroundColor = MaterialTheme.colorScheme.secondaryContainer
    val loadingProgressColor = MaterialTheme.colorScheme.primary


    // Reset the navigated away after scanning flag after 1 second
    LaunchedEffect(Unit) {
        screenViewModel.reset()
        screenViewModel.beginBluetoothConnection()
        delay(1000) // Wait for 1 second
        orionViewModel.resetNavigatedAwayAfterScanning()
    }
    // or if the user navigates away from the screen
    DisposableEffect(Unit) {
        onDispose {
            orionViewModel.resetNavigatedAwayAfterScanning()
            screenViewModel.cancelConnectionJob()
        }
    }
    BackHandler {
        screenViewModel.reset()
        navController.popBackStack()
    }

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
                    title = "Connect to Rov",
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
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally

                ) {
                    // This row contains the contents of the 3 icons.
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp, 64.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                        ) {
                            Column {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(32.dp, 0.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    LinearProgressIndicator(
                                        progress = screenViewModel.bluetoothProgress.collectAsState().value,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(32.dp, 0.dp)
                                            .weight(0.5f)
                                    )
                                    LinearProgressIndicator(
                                        progress = screenViewModel.wifiProgress.collectAsState().value,
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(32.dp, 0.dp)
                                            .weight(0.5f),
                                    )
                                }

                            }

                            // This row contains the 3 icons
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(64.dp) // Set the size of the Box, which indirectly sets the icon size
                                        .background(
                                            color =
                                            if (screenViewModel.isBluetoothPhase.collectAsState().value ||
                                                screenViewModel.bluetoothPhaseCompleted.collectAsState().value
                                            ) {
                                                loadingProgressColor
                                            } else {
                                                loadingProgressBackgroundColor
                                            }, // Set the background color and shape,
                                            shape = CircleShape
                                        ) // Set the background color and shape
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.bluetooth_icon),
                                        contentDescription = "Bluetooth Icon",
                                        modifier = Modifier.size(48.dp), // You might want to adjust the size of the Image to be smaller than the Box to create a padding effect
                                        colorFilter = if (!screenViewModel.bluetoothPhaseCompleted.collectAsState().value) ColorFilter.colorMatrix(
                                            ColorMatrix().apply {
                                                setToSaturation(
                                                    0f
                                                )
                                            }) else null
                                    )
                                }

                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(64.dp) // Set the size of the Box, which indirectly sets the icon size
                                        .background(
                                            color =
                                            if (screenViewModel.isWifiPhase.collectAsState().value ||
                                                screenViewModel.wifiPhaseCompleted.collectAsState().value
                                            ) {
                                                loadingProgressColor
                                            } else {
                                                loadingProgressBackgroundColor
                                            }, // Set the background color and shape,
                                            shape = CircleShape
                                        )
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.wifi_icon),
                                        contentDescription = "Wifi Icon",
                                        modifier = Modifier.size(48.dp),
                                        colorFilter = if (!screenViewModel.isWifiPhase.collectAsState().value) ColorFilter.colorMatrix(
                                            ColorMatrix().apply {
                                                setToSaturation(
                                                    0f
                                                )
                                            }) else null
                                    )
                                }

                                Box(
                                    contentAlignment = Alignment.Center,
                                    modifier = Modifier
                                        .size(64.dp) // Set the size of the Box, which indirectly sets the icon size
                                        .background(
                                            color =
                                            if (screenViewModel.finalPhaseCompleted.collectAsState().value) {
                                                loadingProgressColor
                                            } else {
                                                loadingProgressBackgroundColor
                                            }, // Set the background color and shape,
                                            shape = CircleShape
                                        )
                                ) {
                                    Image(
                                        painter = painterResource(id = R.drawable.checkmark_icon),
                                        contentDescription = "ROV Icon",
                                        modifier = Modifier.size(48.dp),
                                        colorFilter = if (!screenViewModel.finalPhaseCompleted.collectAsState().value) ColorFilter.colorMatrix(
                                            ColorMatrix().apply {
                                                setToSaturation(
                                                    0f
                                                )
                                            }) else null
                                    )
                                }
                            }
                        }
                    }
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp, 16.dp)

                    ) {
                        Text(
                            text = screenViewModel.titleText.collectAsState().value,
                            textAlign = TextAlign.Left,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Spacer(modifier = Modifier.size(16.dp))
                        Text(
                            text = if (screenViewModel.isBluetoothPhase.collectAsState().value) {
                                bluetoothViewModel.statusMessage.collectAsState().value
                            } else {
                                screenViewModel.subTitleText.collectAsState().value
                            },
                            textAlign = TextAlign.Left,
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White
                        )

                    }
                    Button(
                        onClick = {
                            navController.navigate(OrionScreens.RovLoadChecklistScreen.route)
                        },
                        enabled = screenViewModel.finalPhaseCompleted.collectAsState().value,
                    )
                    {
                        Text(
                            "NEXT ➡",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }
            }
        }
    }
}