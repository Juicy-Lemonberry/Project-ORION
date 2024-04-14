package edu.singaporetech.inf2007.team48.project_orion_sandbox.domain.comms

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
interface BluetoothController {
    val isConnected: StateFlow<Boolean>
    val scannedDevices: StateFlow<List<BluetoothDevice>>
    val pairedDevices: StateFlow<List<BluetoothDevice>>
    val errors: SharedFlow<String>

    fun startDiscovery()
    fun stopDiscovery()
    //Bluetooth Server is a blocking action
    fun startBluetoothServer(): Flow<ConnectionResult>
    fun connectBluetoothToDevice(device: BluetoothDevice): Flow<ConnectionResult>
    suspend fun trySendMessage(message: String): BluetoothMessage?
    fun closeBluetoothConnection()

    fun release()
}