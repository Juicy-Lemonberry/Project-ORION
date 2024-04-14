package edu.singaporetech.inf2007.team48.project_orion.activities

import android.os.Bundle
import android.view.InputDevice
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import edu.singaporetech.inf2007.team48.project_orion.OrionApp
import edu.singaporetech.inf2007.team48.project_orion.OrionNavGraph
import edu.singaporetech.inf2007.team48.project_orion.controllers.OrionViewModelFactory
import edu.singaporetech.inf2007.team48.project_orion.controllers.bluetooths.BluetoothViewModelFactory
import edu.singaporetech.inf2007.team48.project_orion.controllers.hotspot.HotspotViewModelFactory
import edu.singaporetech.inf2007.team48.project_orion.controllers.xbox.XboxInputViewModel
import edu.singaporetech.inf2007.team48.project_orion.controllers.udp.UdpViewModel
import edu.singaporetech.inf2007.team48.project_orion.ui.theme.Project_orionTheme

class MainActivity : ComponentActivity() {
    // ViewModel for handling Xbox controller input, initialized lazily.
    private val xboxInputViewModel: XboxInputViewModel by viewModels()

    // ViewModel for handling UDP networking, initialized lazily.
    private val udpViewModel: UdpViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize ViewModel factories for different features like Bluetooth, Hotspot,
        // and app's core functionalities.
        val hotspotViewModelFactory = HotspotViewModelFactory(applicationContext)
        val bluetoothViewModelFactory = BluetoothViewModelFactory(applicationContext)
        val orionViewModelFactory =
            OrionViewModelFactory(
                // Getting repositories from application class for the ViewModelFactory.
                assetRepository = (application as OrionApp).orionAssetRepository,
                assetTypeRepository = (application as OrionApp).orionAssetTypeRepository,
                checklistRepository = (application as OrionApp).orionChecklistRepository,
                recordRepository = (application as OrionApp).orionRecordRepository,
                userRepository = (application as OrionApp).orionUserRepository,
                preferenceRepository = (application as OrionApp).orionPreferenceRepository
            )

        setContent {
            Project_orionTheme {
                // A surface container that uses the 'background' color from the theme,
                // filling the maximum size available.
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Composition that sets up the navigation graph for the application.
                    ProjectOrionApp(
                        orionViewModelFactory,
                        hotspotViewModelFactory,
                        bluetoothViewModelFactory,
                        xboxInputViewModel,
                        udpViewModel
                    )
                }
            }
        }
    }

    // Override to handle generic motion events (e.g., joystick movement) from an input device.
    override fun onGenericMotionEvent(event: MotionEvent): Boolean {
        // Check if the event comes from a joystick and handle it.
        if (event.source and InputDevice.SOURCE_JOYSTICK == InputDevice.SOURCE_JOYSTICK) {
            xboxInputViewModel.handleControllerInput(event)
            return true // Indicate that the event was handled.
        }
        return super.onGenericMotionEvent(event)
    }

    // Override to handle key down events (e.g., button presses) from an input device.
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        // Check if the event comes from a gamepad and handle it.
        if (event.source and InputDevice.SOURCE_GAMEPAD == InputDevice.SOURCE_GAMEPAD) {
            xboxInputViewModel.handleControllerInput(event)
            return true // Indicate that the event was handled.
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}

// Composable function that sets up the navigation graph for the application.
@Composable
fun ProjectOrionApp(
    orionViewModelFactory: OrionViewModelFactory,
    hotspotViewModelFactory: HotspotViewModelFactory,
    bluetoothViewModelFactory: BluetoothViewModelFactory,
    xboxInputViewModel: XboxInputViewModel,
    udpViewModel: UdpViewModel
) {
    // Navigation graph for the application, specifying ViewModel factories and models for different parts of the app.
    OrionNavGraph(
        orionViewModelFactory = orionViewModelFactory,
        hotspotViewModelFactory = hotspotViewModelFactory,
        bluetoothViewModelFactory = bluetoothViewModelFactory,
        xboxInputViewModel = xboxInputViewModel,
        udpViewModel = udpViewModel
    )
}