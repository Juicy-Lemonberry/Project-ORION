package edu.singaporetech.inf2007.team48.project_orion.controllers

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import edu.singaporetech.inf2007.team48.project_orion.repository.OrionAssetRepository
import edu.singaporetech.inf2007.team48.project_orion.repository.OrionAssetTypeRepository
import edu.singaporetech.inf2007.team48.project_orion.repository.OrionChecklistRepository
import edu.singaporetech.inf2007.team48.project_orion.repository.OrionPreferenceRepository
import edu.singaporetech.inf2007.team48.project_orion.repository.OrionRecordRepository
import edu.singaporetech.inf2007.team48.project_orion.repository.OrionUserRepository

class OrionViewModelFactory(
    private val assetRepository: OrionAssetRepository,
    private val assetTypeRepository: OrionAssetTypeRepository,
    private val checklistRepository: OrionChecklistRepository,
    private val recordRepository: OrionRecordRepository,
    private val userRepository: OrionUserRepository,
    private val preferenceRepository: OrionPreferenceRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OrionViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return OrionViewModel(
                assetRepository = assetRepository,
                assetTypeRepository = assetTypeRepository,
                checklistRepository = checklistRepository,
                recordRepository = recordRepository,
                userRepository = userRepository,
                preferenceRepository = preferenceRepository
            ) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}