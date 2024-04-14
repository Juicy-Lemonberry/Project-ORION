package edu.singaporetech.inf2007.team48.project_orion.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences

class OrionPreferenceRepository(
    private val dataStore: DataStore<Preferences>
)
{
    //Any other store preferences should be saved here.
}