package edu.singaporetech.inf2007.team48.project_orion.controllers.rovConnection

import androidx.lifecycle.ViewModel
import edu.singaporetech.inf2007.team48.project_orion.controllers.OrionViewModel
import edu.singaporetech.inf2007.team48.project_orion.controllers.bluetooths.BluetoothViewModel
import edu.singaporetech.inf2007.team48.project_orion.controllers.hotspot.HotspotViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// ViewModel class for managing the UI state of the ROV (Remote Operated Vehicle) connecting screen.
class RovConnectingScreenViewModel(
    // Dependencies on other ViewModels for orchestrating connection setup.
    private val orionViewModel: OrionViewModel,
    private val bluetoothViewModel: BluetoothViewModel,
    private val hotspotViewModel: HotspotViewModel
) : ViewModel() {

    // Job for managing a long-running connection task.
    private var connectionJob: Job? = null

    // MutableStateFlow for controlling the enabled state of the abort button.
    private val _abortButtonEnabled = MutableStateFlow(false)

    // Exposes an immutable StateFlow of the abort button's enabled state.
    val abortButtonEnabled: StateFlow<Boolean> = _abortButtonEnabled.asStateFlow()

    // Tracks if the abort button was clicked.
    private val _abortButtonClicked = MutableStateFlow(false)

    // Exposes an immutable StateFlow for abort button clicks.
    val abortButtonClicked: StateFlow<Boolean> = _abortButtonClicked.asStateFlow()

    // Tracks completion of the Bluetooth connection phase.
    private val _bluetoothPhaseCompleted = MutableStateFlow(false)

    // Directly exposes the Bluetooth connection state from the BluetoothViewModel.
    val bluetoothPhaseCompleted: StateFlow<Boolean> = bluetoothViewModel.isConnected

    // Tracks completion of the WiFi setup phase.
    private val _wifiPhaseCompleted = MutableStateFlow(false)

    // Exposes an immutable StateFlow for WiFi phase completion.
    val wifiPhaseCompleted: StateFlow<Boolean> = _wifiPhaseCompleted.asStateFlow()

    // Tracks completion of the final connection phase.
    private val _finalPhaseCompleted = MutableStateFlow(false)

    // Exposes an immutable StateFlow for the final phase completion.
    val finalPhaseCompleted: StateFlow<Boolean> = _finalPhaseCompleted.asStateFlow()

    // Indicates if currently in the Bluetooth connection phase.
    private val _isBluetoothPhase = MutableStateFlow(false)
    val isBluetoothPhase: StateFlow<Boolean> = _isBluetoothPhase.asStateFlow()

    // Indicates if currently in the WiFi setup phase.
    private val _isWifiPhase = MutableStateFlow(false)
    val isWifiPhase: StateFlow<Boolean> = _isWifiPhase.asStateFlow()

    // Exposes the Bluetooth connection progress from the BluetoothViewModel.
    val bluetoothProgress: StateFlow<Float> = bluetoothViewModel.progress

    // MutableStateFlow for tracking the progress of the WiFi connection setup.
    private val _wifiProgress = MutableStateFlow(0.0f)

    // Exposes an immutable StateFlow for WiFi setup progress.
    val wifiProgress: StateFlow<Float> = _wifiProgress.asStateFlow()

    // MutableStateFlows for updating UI text elements.
    private val _titleText = MutableStateFlow("null")
    val titleText: StateFlow<String> = _titleText.asStateFlow()

    private val _subTitleText = MutableStateFlow("null")
    val subTitleText: StateFlow<String> = _subTitleText.asStateFlow()

    // Resets the UI and connection states to their initial values.
    fun reset() {
        _abortButtonEnabled.value = true
        _abortButtonClicked.value = false
        _bluetoothPhaseCompleted.value = false
        _wifiPhaseCompleted.value = false
        _finalPhaseCompleted.value = false
        _isBluetoothPhase.value = false
        _isWifiPhase.value = false
        _wifiProgress.value = 0.0f
        _titleText.value = "Title"
        _subTitleText.value = "Description"
        if (bluetoothViewModel.isConnected()) {
            bluetoothViewModel.disconnect()
        }
        connectionJob?.cancel()
    }

    // Cancels the ongoing connection job and stops the hotspot.
    fun cancelConnectionJob() {
        connectionJob?.cancel()
        hotspotViewModel.stopHotspot()
    }

    // Sets the enabled state of the abort button.
    fun setAbortButtonEnabled(boolean: Boolean) {
        _abortButtonEnabled.value = boolean
    }

    // Marks the abort button as clicked.
    fun onAbortButtonClicked() {
        _abortButtonClicked.value = true
    }

    // Starts or stops the Bluetooth connection phase.
    fun setBluetoothPhase(boolean: Boolean) {
        _isBluetoothPhase.value = boolean
    }

    // Starts or stops the WiFi setup phase.
    fun setWifiPhase(boolean: Boolean) {
        _isWifiPhase.value = boolean
    }

    // Sets the progress of the WiFi setup.
    fun setWifiProgress(progress: Float) {
        _wifiProgress.value = progress
    }

    // Sets the completion state of the Bluetooth connection phase.
    fun setBluetoothPhaseCompleted(boolean: Boolean) {
        _bluetoothPhaseCompleted.value = boolean
    }

    // Sets the completion state of the WiFi setup phase.
    fun setWifiPhaseCompleted(boolean: Boolean) {
        _wifiPhaseCompleted.value = boolean
    }

    // Sets the completion state of the final connection phase.
    fun setFinalPhaseCompleted(boolean: Boolean) {
        _finalPhaseCompleted.value = boolean
    }

    // Sets the title text of the screen.
    fun setTitleText(text: String) {
        _titleText.value = text
    }

    // Sets the subtitle text of the screen.
    fun setSubTitleText(text: String) {
        _subTitleText.value = text
    }

    // Checks if the Bluetooth connection is active.
    fun isBluetoothConnected(): Boolean {
        return bluetoothViewModel.isConnected()
    }

    // Disconnects the Bluetooth connection.
    fun disconnectBluetooth() {
        bluetoothViewModel.disconnect()
    }

    // Initiates the Bluetooth connection process and transitions through the connection phases.
    fun beginBluetoothConnection() {
        _titleText.value = "Connecting to ROV..."
        _isBluetoothPhase.value = true
        connectionJob = CoroutineScope(Dispatchers.Main).launch {
            bluetoothViewModel.connectToDevice(orionViewModel.rovBtMacAddress.value)

            // Polling isConnected to wait for it to become true
            while (!bluetoothViewModel.isConnected()) {
                delay(100) // Check every 100 milliseconds
                // Retry the connection if it failed
                if (bluetoothViewModel.btConnectionAttemptFailed.value) {
                    bluetoothViewModel.connectToDevice(orionViewModel.rovBtMacAddress.value)
                }
            }

            _isBluetoothPhase.value = false

            // Once connected, begin data exchange using RovConnectingScreenViewModel
            withContext(Dispatchers.Main) {
                beginHotspotConnection()
            }
        }
    }

    // Starts the hotspot setup process as part of the WiFi phase.
    private fun beginHotspotConnection() {
        _titleText.value = "Setting up WiFi connection..."
        _subTitleText.value = "Setting up local hotspot..."
        _wifiProgress.value = 0.0f
        _isWifiPhase.value = true
        CoroutineScope(Dispatchers.Main).launch {
            hotspotViewModel.startHotspot { success ->
                if (success) {
                    _subTitleText.value = "Hotspot setup complete!\nAsking ROV to connect..."
                    _wifiProgress.value = 0.25f
                    startHotspotNegotiation()

                } else {
                    _subTitleText.value = "Hotspot setup failed!"
                    _wifiProgress.value = 0.0f
                }
            }
        }
    }

    // Negotiates the hotspot connection details with the ROV.
    private fun startHotspotNegotiation() {
        CoroutineScope(Dispatchers.Main).launch {
            bluetoothViewModel.attachMessageReceivedCallback { message ->
                processReplies(message)
            }
            _wifiProgress.value = 0.5f
            bluetoothViewModel.sendData("CONNECT_TO_WIFI:${hotspotViewModel.ssid.value}:${hotspotViewModel.password.value}")
            // check viewmodel to see if orionViewModel.rovWifiAssignedIp is updated
            val messageId = 0
            while (orionViewModel.rovWifiConnectedSsid.value != hotspotViewModel.ssid.value) {
                delay(3000)
                bluetoothViewModel.sendData("WHAT_IS_UR_IP")
            }
            _wifiProgress.value = 1.0f
            _finalPhaseCompleted.value = true
            delay(1000)
            bluetoothViewModel.detachMessageReceivedCallback { message ->
                processReplies(message)
            }
        }
    }

    // Processes replies from the ROV over Bluetooth.
    private fun processReplies(message: String) {
        if (message.startsWith("ECAM:")) {
            _subTitleText.value = "ROV ECAM: ${message.substring(5)}"
        } else if (message.startsWith("MY_IP_IS:")) {
            // reads format of MY_IP_IS:XXX.XXX.XXX.XXX@SSID
            val ip = message.substring(9, message.indexOf('@'))
            val ssid = message.substring(message.indexOf('@') + 1)
            _subTitleText.value = "ROV connected to WiFi!\nIP: $ip\nSSID: $ssid"
            orionViewModel.setRovWifiAssignedIp(ip)
            orionViewModel.setRovWifiConnectedSsid(ssid)
            orionViewModel.setRovWifiIsConnectedToPhone(true)
        }
    }
}