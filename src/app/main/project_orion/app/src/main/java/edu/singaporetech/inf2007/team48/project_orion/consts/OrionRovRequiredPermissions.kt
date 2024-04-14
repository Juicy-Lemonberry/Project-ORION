package edu.singaporetech.inf2007.team48.project_orion.consts
import android.Manifest
import android.os.Build

/**
 * Helper consts for the list of requirement permissions for the mobile application...
 */
object OrionRovRequiredPermissions {

    /**
     * Core permission set, requiring internet and network.
     * See definition for up-to-date list.
     */
    val basePermissions = listOf(
        Manifest.permission.CAMERA,
        Manifest.permission.INTERNET,
        Manifest.permission.ACCESS_NETWORK_STATE,
        Manifest.permission.ACCESS_WIFI_STATE,
        Manifest.permission.CHANGE_WIFI_STATE,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    /**
     * Permission set for bluetooth.
     *
     * #### Note
     * The list will differ based on the SDK version, the actual check done is as follows:
     * ```
     * Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
     * ```
     */
    val postBluetoothPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        listOf(
            Manifest.permission.BLUETOOTH_SCAN,
            Manifest.permission.BLUETOOTH_CONNECT
        )
    } else {
        listOf(
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN
        )
    }

    /**
     * Permission set for Wifi and Location access
     *
     * #### Note
     * The list will differ based on the SDK version, the actual check done is as follows:
     * ```
     * Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
     * ```
     */
    val postTiramisuPermissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        listOf(
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.NEARBY_WIFI_DEVICES
        )
    } else {
        listOf(
            // Prior to Android Tiramisu, ACCESS_COARSE_LOCATION is often used alongside FINE_LOCATION for Bluetooth/WiFi scanning
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }

    /**
     * The full permission list for 100% of the application to function.
     *
     * > Implementation, it just concat the lists of permissions sets together.
     */
    val permissions = basePermissions + postBluetoothPermissions + postTiramisuPermissions
}
