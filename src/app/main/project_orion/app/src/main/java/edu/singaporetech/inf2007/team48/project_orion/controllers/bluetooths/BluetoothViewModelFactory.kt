package edu.singaporetech.inf2007.team48.project_orion.controllers.bluetooths

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

// Factory class for creating instances of BluetoothViewModel with a specific constructor parameter (context)
class BluetoothViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    // Creates instances of the specified ViewModel class
    /**
     * Creates a ViewModel instance, ensuring it is of the BluetoothViewModel type and passing context to its constructor.
     *
     * @param modelClass The Class object of the ViewModel to be instantiated.
     * @return A new instance of T, which is a subclass of ViewModel.
     * @throws IllegalArgumentException if the ViewModel class is not assignable from BluetoothViewModel.
     */
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BluetoothViewModel::class.java)) {
            // Suppresses "UNCHECKED_CAST" warning because the cast is logically checked
            @Suppress("UNCHECKED_CAST")
            return BluetoothViewModel(context) as T
        }
        // Throws an exception if the modelClass is not assignable to BluetoothViewModel
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
