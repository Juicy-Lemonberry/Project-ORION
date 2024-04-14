package edu.singaporetech.inf2007.team48.project_orion.controllers.rov

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
// ViewModel class for the welcome screen of the Rov (Remote Operated Vehicle) application.
class RovWelcomeScreenViewModel : ViewModel() {

    // MutableStateFlow holding the title text, initially set to a welcome message.
    private val _title = MutableStateFlow("Welcome to Project Orion")
    // Publicly exposed immutable version of the title StateFlow.
    val title: StateFlow<String> = _title.asStateFlow()

    // MutableStateFlow holding the description text, instructing the user to enter their operator ID.
    private val _description = MutableStateFlow("Please enter your operator ID to continue")
    // Publicly exposed immutable version of the description StateFlow.
    val description: StateFlow<String> = _description.asStateFlow()

    // MutableStateFlow for tracking the camera permission status, initially false.
    private val _cameraPermissionGranted = MutableStateFlow(false)
    // Publicly exposed immutable version of the camera permission StateFlow.
    val cameraPermissionGranted: StateFlow<Boolean> = _cameraPermissionGranted.asStateFlow()

    // MutableStateFlow for tracking the location permission status, initially false.
    private val _locationPermissionGranted = MutableStateFlow(false)
    // Publicly exposed immutable version of the location permission StateFlow.
    val locationPermissionGranted: StateFlow<Boolean> = _locationPermissionGranted.asStateFlow()

    // MutableStateFlow for tracking the Bluetooth permission status, initially false.
    private val _bluetoothPermissionGranted = MutableStateFlow(false)
    // Publicly exposed immutable version of the Bluetooth permission StateFlow.
    val bluetoothPermissionGranted: StateFlow<Boolean> = _bluetoothPermissionGranted.asStateFlow()

    // MutableStateFlow holding the text for the continuation button.
    private val _buttonText = MutableStateFlow("Continue")
    // Publicly exposed immutable version of the buttonText StateFlow.
    val buttonText: StateFlow<String> = _buttonText.asStateFlow()

    // Sets the camera permission status.
    fun setCameraPermissionGranted(granted: Boolean) {
        _cameraPermissionGranted.value = granted
    }

    // Sets the location permission status.
    fun setLocationPermissionGranted(granted: Boolean) {
        _locationPermissionGranted.value = granted
    }

    // Sets the Bluetooth permission status.
    fun setBluetoothPermissionGranted(granted: Boolean) {
        _bluetoothPermissionGranted.value = granted
    }

    /**
     * Invoked when the 'Continue' button is clicked.
     *
     * @param onAllPermissionsGranted Callback to be invoked if all permissions are granted.
     * @param onSomePermissionsNotGranted Callback to be invoked if some permissions are not granted.
     */
    fun onContinueClicked(
        onAllPermissionsGranted: () -> Unit,
        onSomePermissionsNotGranted: () -> Unit
    ) {
        if (_cameraPermissionGranted.value && _locationPermissionGranted.value && _bluetoothPermissionGranted.value) {
            onAllPermissionsGranted()
        } else {
            onSomePermissionsNotGranted()
        }
    }

}
