package edu.singaporetech.inf2007.team48.project_orion_sandbox.data.comms

import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import edu.singaporetech.inf2007.team48.project_orion_sandbox.domain.comms.BluetoothDeviceDomain

@SuppressLint("MissingPermission")
fun BluetoothDevice.toBluetoothDeviceDomain(): BluetoothDeviceDomain {
    return BluetoothDeviceDomain(
        name = name,
        address = address
    )
}