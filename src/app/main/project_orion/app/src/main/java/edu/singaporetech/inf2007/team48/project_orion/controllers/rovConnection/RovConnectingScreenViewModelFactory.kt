package edu.singaporetech.inf2007.team48.project_orion.controllers.rovConnection

import edu.singaporetech.inf2007.team48.project_orion.controllers.bluetooths.BluetoothViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import edu.singaporetech.inf2007.team48.project_orion.controllers.OrionViewModel
import edu.singaporetech.inf2007.team48.project_orion.controllers.hotspot.HotspotViewModel

class RovConnectingScreenViewModelFactory(
    private val orionViewModel: OrionViewModel,
    private val bluetoothViewModel: BluetoothViewModel,
    private val hotspotViewModel: HotspotViewModel
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(RovConnectingScreenViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return RovConnectingScreenViewModel(orionViewModel, bluetoothViewModel, hotspotViewModel) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}