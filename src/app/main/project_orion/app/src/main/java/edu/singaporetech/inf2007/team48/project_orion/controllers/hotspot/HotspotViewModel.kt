package edu.singaporetech.inf2007.team48.project_orion.controllers.hotspot

import android.annotation.SuppressLint
import android.content.Context
import android.net.wifi.WifiManager
import android.os.Build
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

// SuppressLint annotation to ignore the "MissingPermission" lint warning
@SuppressLint("MissingPermission")
// ViewModel class for managing WiFi hotspot functionality
class HotspotViewModel(context: Context) : ViewModel() {
    // Retrieves the WifiManager for managing WiFi operations
    private val wifiManager: WifiManager =
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    // Reservation object for the current Local Only Hotspot
    private var hotspotReservation: WifiManager.LocalOnlyHotspotReservation? = null

    // StateFlow variables to publish hotspot status, SSID, and password
    private val _isReady = MutableStateFlow(false) // Tracks if the hotspot is active and ready
    val isReady = _isReady.asStateFlow()

    private val _ssid = MutableStateFlow<String?>(null) // SSID of the hotspot
    val ssid = _ssid.asStateFlow()

    private val _password = MutableStateFlow<String?>(null) // Password of the hotspot
    val password = _password.asStateFlow()

    // Function to start the WiFi hotspot
    /**
     * Starts the WiFi hotspot and updates the StateFlow properties based on the operation success.
     * The function is designed to handle different Android versions with appropriate logic.
     *
     * @param callback A lambda function to indicate success (true) or failure (false) of hotspot start operation.
     */
    fun startHotspot(callback: (Boolean) -> Unit) {
        stopHotspot() // Ensures any existing hotspot is stopped before starting a new one
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            // For Android 10 and above, uses the LocalOnlyHotspotCallback to manage hotspot
            wifiManager.startLocalOnlyHotspot(object : WifiManager.LocalOnlyHotspotCallback() {
                override fun onStarted(reservation: WifiManager.LocalOnlyHotspotReservation) {
                    super.onStarted(reservation)
                    hotspotReservation = reservation
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        // For Android 12 (API level 31) and above, access SSID and password directly from SoftApConfiguration
                        val softApConfig = reservation.softApConfiguration
                        _ssid.value = softApConfig.ssid
                        _password.value = softApConfig.passphrase
                    } else {
                        // For Android 10 and 11, use WifiConfiguration (now deprecated) to get SSID and password
                        val wifiConfig = reservation.wifiConfiguration
                        _ssid.value = wifiConfig?.SSID
                        _password.value = wifiConfig?.preSharedKey
                    }
                    _isReady.value = true // Update readiness state
                    callback(true)
                }

                override fun onStopped() {
                    super.onStopped()
                    _isReady.value = false // Update readiness state on stop
                    callback(false)
                }

                override fun onFailed(reason: Int) {
                    super.onFailed(reason)
                    _isReady.value = false // Update readiness state on failure
                    callback(false)
                }
            }, null)
        } else {
            // For Android versions below Android 10, currently not handling the hotspot functionality
            _isReady.value = false
            callback(false)
        }
    }

    // Function to stop the WiFi hotspot
    /**
     * Stops the WiFi hotspot if it's currently active. This function primarily affects Android 8.0 (API level 26) and above.
     */
    fun stopHotspot() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            hotspotReservation?.close() // Close the reservation to stop the hotspot
            _isReady.value = false // Update readiness state
            // Reset SSID and password
            _ssid.value = null
            _password.value = null
        }
        // For Android versions below 8.0, no direct API to stop the hotspot is provided
    }

    // Cleans up resources when ViewModel is cleared
    override fun onCleared() {
        super.onCleared()
        stopHotspot() // Ensure the hotspot is stopped to clean up resources
    }
}
