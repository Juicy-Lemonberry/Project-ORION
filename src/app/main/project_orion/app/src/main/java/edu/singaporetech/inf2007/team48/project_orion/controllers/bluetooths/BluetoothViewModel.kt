package edu.singaporetech.inf2007.team48.project_orion.controllers.bluetooths

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.util.Log
import androidx.lifecycle.ViewModel
import edu.singaporetech.inf2007.team48.project_orion.consts.OrionUUID
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.IOException
import java.io.InputStream
import java.io.OutputStream
import java.util.UUID
// SuppressLint annotation to ignore the "MissingPermission" lint warning
@SuppressLint("MissingPermission")
// BluetoothViewModel class to manage Bluetooth connections, data transmission, and UI updates
class BluetoothViewModel(context: Context) : ViewModel() {
    // BluetoothAdapter for handling Bluetooth operations
    private val bluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()
    private var connectThread: ConnectThread? = null // Thread for managing connection attempts
    private var connectedThread: ConnectedThread? = null // Thread for managing data transmission once connected
    private var connectionAttemptFailed = MutableStateFlow(false) // Tracks if connection attempt was unsuccessful

    // Publicly exposed StateFlow to track connection attempt failures
    var btConnectionAttemptFailed: StateFlow<Boolean> = connectionAttemptFailed.asStateFlow()

    // StateFlow for storing and emitting received messages
    private val _receivedMessage = MutableStateFlow<String?>(null)
    val receivedMessage: StateFlow<String?> = _receivedMessage.asStateFlow()

    // List to hold callbacks that should be notified when a message is received
    private val messageReceivedCallbacks: MutableList<(String) -> Unit> = mutableListOf()

    // StateFlows for UI to observe various states
    private val _isError = MutableStateFlow(false) // Tracks if there's an error
    val isError: StateFlow<Boolean> = _isError.asStateFlow()

    private val _progress = MutableStateFlow(0.0f) // Tracks connection progress
    val progress: StateFlow<Float> = _progress.asStateFlow()

    private val _isConnected = MutableStateFlow(false) // Tracks connection status
    val isConnected: StateFlow<Boolean> = _isConnected.asStateFlow()

    private val _statusMessage = MutableStateFlow("") // Tracks status messages for UI display
    val statusMessage: StateFlow<String> = _statusMessage.asStateFlow()

    // Function to initiate connection to a Bluetooth device
    suspend fun connectToDevice(deviceAddress: String) {
        // Reset state and log status
        connectionAttemptFailed.value = false
        _statusMessage.value = "Clearing any existing connections..."
        Log.d(TAG, "Clearing any existing connections...")
        disconnect() // Disconnect existing connections if any
        delay(1000) // Wait a bit before attempting to connect
        _statusMessage.value = "Connecting to device $deviceAddress"
        Log.d(TAG, "Connecting to device $deviceAddress")
        // Attempt to get the device and connect
        val device: BluetoothDevice? = bluetoothAdapter?.getRemoteDevice(deviceAddress)
        device?.let {
            connectThread = ConnectThread(it)
            connectThread?.start()
            _progress.value = 0.1f
        }
    }

    // Function to send data to the connected device
    fun sendData(data: String) {
        if (connectedThread?.isConnected() == true) {
            connectedThread?.write(data.toByteArray())
        } else {
            Log.e(TAG, "Not connected to a device.")
        }
    }

    // Inner class for handling the connection process in a separate thread
    private inner class ConnectThread(device: BluetoothDevice) : Thread() {
        private val socket: BluetoothSocket? by lazy(LazyThreadSafetyMode.NONE) {
            device.createRfcommSocketToServiceRecord(MY_UUID)
        }

        override fun run() {
            // Update UI with status and cancel discovery as it's heavy on Bluetooth adapter
            _statusMessage.value = "ConnectThread Started! Cancelling discovery..."
            _progress.value = 0.25f
            bluetoothAdapter?.cancelDiscovery()
            _statusMessage.value = "Attempting to connect to ROV..."

            socket?.let {
                try {
                    // Attempt to connect to the Bluetooth device
                    Log.d(TAG, "Trying to connect to the device...")
                    it.connect()
                    Log.d(TAG, "Connection established successfully.")
                    _statusMessage.value = "Connection successful!, handing over to ConnectedThread..."
                    manageConnectedSocket(it) // Hand over to ConnectedThread if connection is successful
                } catch (connectException: IOException) {
                    // Handle connection failure
                    Log.e(TAG, "Connection failed", connectException)
                    _isError.value = true
                    try {
                        it.close()
                    } catch (closeException: IOException) {
                        Log.e(TAG, "Could not close the client socket", closeException)
                    }
                    finally {
                        connectionAttemptFailed.value = true
                        _statusMessage.value = "Connection failed!, retrying..."
                    }
                    return
                }
            }
        }

        // Closes the socket and terminates the thread
        fun cancel() {
            try {
                socket?.close()
            } catch (e: IOException) {
                Log.e(TAG, "Could not close the client socket", e)
            }
        }
    }

    // Handles successful connection, creating and starting a ConnectedThread
    private fun manageConnectedSocket(socket: BluetoothSocket) {
        _progress.value = 0.75f
        connectedThread = ConnectedThread(socket)
        connectedThread?.start()
    }

    // Functions to manage message received callbacks
    fun attachMessageReceivedCallback(callback: (String) -> Unit) {
        messageReceivedCallbacks.add(callback)
    }
    fun detachMessageReceivedCallback(callback: (String) -> Unit) {
        messageReceivedCallbacks.remove(callback)
    }

    // Inner class for handling data transmission after a successful connection
    private inner class ConnectedThread(private val mmSocket: BluetoothSocket) : Thread() {
        private val mmInStream: InputStream = mmSocket.inputStream
        private val mmOutStream: OutputStream = mmSocket.outputStream

        override fun run() {
            // Set initial state and buffer for data
            _statusMessage.value = "Connected to device!"
            _progress.value = 1.0f
            _isConnected.value = true
            val buffer = ByteArray(1024)
            var numBytes: Int

            while (true) {
                numBytes = try {
                    mmInStream.read(buffer) // Read from the InputStream
                } catch (e: IOException) {
                    Log.d(TAG, "Input stream was disconnected", e)
                    _isConnected.value = false
                    break
                }
                if (numBytes > 0) {
                    val readMsg = String(buffer, 0, numBytes)
                    Log.d(TAG, "Received: $readMsg")
                    _receivedMessage.value = readMsg
                    messageReceivedCallbacks.forEach { callback ->
                        callback(readMsg)
                    }
                }
            }
        }

        // Send data to the remote device
        fun write(bytes: ByteArray) {
            try {
                mmOutStream.write(bytes)
            } catch (e: IOException) {
                Log.e(TAG, "Error occurred when sending data", e)
            }
        }

        // Check if this thread is currently connected to a device
        fun isConnected(): Boolean {
            return _isConnected.value
        }

        // Close the socket and terminate the thread
        fun cancel() {
            try {
                mmSocket.close()
            } catch (e: IOException) {
                Log.e(TAG, "Could not close the connect socket", e)
            }
        }
    }

    // Public function to check connection status
    fun isConnected(): Boolean {
        return connectedThread?.isConnected() ?: false
    }

    // Function to disconnect from the device and reset the state
    fun disconnect() {
        _statusMessage.value = "Disconnecting..."
        connectThread?.apply {
            cancel()
            interrupt()
        }
        connectedThread?.apply {
            cancel()
            interrupt()
        }
        connectThread = null
        connectedThread = null
        _progress.value = 0.0f
        _isError.value = false
        _isConnected.value = false
        _statusMessage.value = ""
    }

    // Clean up connections when the ViewModel is cleared
    override fun onCleared() {
        super.onCleared()
        disconnect()
    }

    companion object {
        private val MY_UUID: UUID = UUID.fromString(OrionUUID.SERVICE_UUID) // UUID for the Bluetooth connection
        private const val TAG = "BluetoothConnectorVM" // Tag for logging
    }
}
