package edu.singaporetech.inf2007.team48.project_orion_sandbox.domain.comms

data class BluetoothMessage(
    val message: String,
    val senderName: String,
    val isFromLocalUser: Boolean
)