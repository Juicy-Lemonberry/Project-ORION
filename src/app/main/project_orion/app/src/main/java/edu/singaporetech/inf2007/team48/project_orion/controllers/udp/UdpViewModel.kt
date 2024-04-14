package edu.singaporetech.inf2007.team48.project_orion.controllers.udp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress

// ViewModel for handling UDP connection, sending, and receiving data.
class UdpViewModel : ViewModel() {

    // Stores the timestamp of the last packet sent to implement keepalive functionality.
    private var lastPacketSentTime = System.currentTimeMillis()

    // Coroutine job for sending KEEPALIVE messages periodically.
    private var keepAliveJob: Job? = null // Coroutine for sending KEEPALIVE messages

    // Datagram socket for UDP communication.
    private var socket: DatagramSocket? = null

    // Coroutine job for listening for incoming UDP packets.
    private var listeningJob: kotlinx.coroutines.Job? = null


    // Holds the last piece of data received from the UDP connection.
    private val _receivedData = MutableStateFlow<String?>(null)
    // Publicly exposed StateFlow of received data, allowing observation.
    val receivedData = _receivedData.asStateFlow()
    // List of callbacks to be invoked when data is received.
    private val onDataReceivedCallbacks = mutableListOf<(String) -> Unit>()

    // Indicates whether the UDP connection is established.
    private val _isConnected = MutableStateFlow<Boolean>(false)
    // Publicly exposed StateFlow of the connection status.
    val isConnected : StateFlow<Boolean> = _isConnected.asStateFlow()

    // Stores remote IP address for the UDP connection.
    private val _remoteIp = MutableStateFlow<String>("")
    // Stores remote port number for the UDP connection.
    private var _remotePort = MutableStateFlow<Int>(0)

    // Initiates a coroutine that sends keepalive messages periodically.
    private fun startKeepAliveThread() {
        keepAliveJob?.cancel()
        keepAliveJob = viewModelScope.launch(Dispatchers.IO) {
            while (isActive) {
                if (System.currentTimeMillis() - lastPacketSentTime > 1000) {
                    sendUDPData("keepalive")
                    lastPacketSentTime = System.currentTimeMillis()
                }
                delay(1000)
            }
        }
    }

    /**
     * Initiates a UDP connection to the specified remote IP and port.
     *
     * @param remoteIp The remote IP address to connect to.
     * @param remotePort The remote port number to connect to.
     * @param localPort The local port number to bind to (optional).
     *
     */

    fun connect(remoteIp: String, remotePort: Int, localPort: Int? = null) {
        _remoteIp.value = remoteIp
        _remotePort.value = remotePort
        viewModelScope.launch(Dispatchers.IO) {
            try {
                // Optional: specify a local port, or let the system choose one
                socket = if (localPort != null) DatagramSocket(localPort) else DatagramSocket()
                startListening() // Start listening for incoming data
                startKeepAliveThread()
                _isConnected.value = true
                Log.d("UdpViewModel", "Connected to $remoteIp:$remotePort")
            } catch (e: Exception) {
                Log.e("UdpViewModel", "Error creating connection: ${e.message}")
                _isConnected.value = false
            }
        }
    }

    /**
     * Sends a UDP message to the remote IP and port.
     * @param data The data to send.
     * @param remoteIp The remote IP address to send to.
     * @param remotePort The remote port number to send to.
     *
     */
    fun sendUDPData(data: String, remoteIp: String = _remoteIp.value, remotePort: Int = _remotePort.value) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val address = InetAddress.getByName(remoteIp)
                val buffer = data.toByteArray()
                val packet = DatagramPacket(buffer, buffer.size, address, remotePort)
                socket?.send(packet)
            } catch (e: Exception) {
                Log.e("UdpViewModel", "Error sending UDP data: ${e.message}")

            }
        }
    }

    /**
     * Registers a callback to be invoked when data is received.
     *
     * @param callback The callback function to be invoked when data is received.
     *
     */

    fun onDataReceive(callback: (String) -> Unit) {
        onDataReceivedCallbacks.add(callback)
    }

    /**
     * Removes a previously registered data receive callback.
     *
     * @param callback The callback function to be removed.
     *
     */
    fun removeDataReceiveCallback(callback: (String) -> Unit) {
        onDataReceivedCallbacks.remove(callback)
    }

    // Starts a coroutine that listens for incoming UDP packets and processes them.
    private fun startListening() {
        listeningJob = viewModelScope.launch(Dispatchers.IO) {
            try {
                while (isActive) {
                    val buffer = ByteArray(1024) // Adjust based on expected data size
                    val packet = DatagramPacket(buffer, buffer.size)
                    socket?.receive(packet) // This call is blocking
                    val receivedString = String(packet.data, 0, packet.length)

                    _receivedData.value = receivedString // Emit received data

                    // Invoke all registered callbacks with the new data
                    onDataReceivedCallbacks.forEach { callback ->
                        callback(receivedString)
                    }
                }
            } catch (e: Exception) {
                Log.e("UdpViewModel", "Error receiving UDP data: ${e.message}")
            }
        }
    }

    // Closes the UDP connection and cleans up resources.
    fun disconnect() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                keepAliveJob?.cancelAndJoin() // Stop sending keepAlive messages
                listeningJob?.cancelAndJoin() // Stop listening
                socket?.close() // Close the socket
                _isConnected.value = false
            } catch (e: Exception) {
                Log.e("UdpViewModel", "Error closing connection: ${e.message}")
            } finally {
                socket = null
                _isConnected.value = false // Just to be sure
            }
        }
    }


    // Ensures that resources are cleaned up when the ViewModel is cleared.
    override fun onCleared() {
        super.onCleared()
        disconnect() // Ensure resources are cleaned up
    }
}