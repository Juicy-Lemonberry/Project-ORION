package edu.singaporetech.inf2007.team48.project_orion_sandbox.presentation

import edu.singaporetech.inf2007.team48.project_orion_sandbox.domain.comms.BluetoothDevice
import edu.singaporetech.inf2007.team48.project_orion_sandbox.domain.comms.BluetoothMessage

data class BluetoothUiState(
    val scannedDevices: List<BluetoothDevice> = emptyList(),
    val pairedDevices: List<BluetoothDevice> = emptyList(),
    val isConnected: Boolean = false,
    val isConnecting: Boolean = false,
    val errorMessage: String? = null,
    val messages: List<BluetoothMessage> = emptyList()

)