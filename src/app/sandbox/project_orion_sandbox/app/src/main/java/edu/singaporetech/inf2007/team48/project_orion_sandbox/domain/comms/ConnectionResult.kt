package edu.singaporetech.inf2007.team48.project_orion_sandbox.domain.comms

sealed interface ConnectionResult {
    object ConnectionEstablished : ConnectionResult
    data class TransferSucceeded(val message: BluetoothMessage): ConnectionResult
    data class Error(val message: String) : ConnectionResult
}