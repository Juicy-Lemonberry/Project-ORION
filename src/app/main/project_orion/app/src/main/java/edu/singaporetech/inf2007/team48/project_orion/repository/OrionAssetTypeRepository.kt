package edu.singaporetech.inf2007.team48.project_orion.repository

import androidx.annotation.WorkerThread
import edu.singaporetech.inf2007.team48.project_orion.dao.OrionAssetTypeDao
import edu.singaporetech.inf2007.team48.project_orion.models.OrionAssetType
import kotlinx.coroutines.flow.Flow

class OrionAssetTypeRepository(
    private val orionAssetTypeDao: OrionAssetTypeDao
)
{
    // Section 2: OrionAssetTypeDao
    @WorkerThread
    suspend fun insertAssetType(orionAssetType: OrionAssetType) {
        orionAssetTypeDao.insertAssetType(orionAssetType)
    }

    @WorkerThread
    fun getAllAssetTypes(): Flow<List<OrionAssetType>> {
        return orionAssetTypeDao.getAllAssetTypes()
    }

    @WorkerThread
    fun getAssetTypeById(typeId: Int): Flow<OrionAssetType> {
        return orionAssetTypeDao.getAssetTypeById(typeId)
    }

    @WorkerThread
    suspend fun updateAssetType(newAssetType: OrionAssetType) {
        orionAssetTypeDao.updateAssetType(
            newAssetType.typeId,
            newAssetType.typeDesc
        )
    }

    @WorkerThread
    suspend fun deleteAssetType(typeId: Int) {
        orionAssetTypeDao.deleteAssetType(typeId)
    }
}