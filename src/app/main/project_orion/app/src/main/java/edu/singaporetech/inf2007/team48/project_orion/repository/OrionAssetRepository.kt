package edu.singaporetech.inf2007.team48.project_orion.repository

import androidx.annotation.WorkerThread
import edu.singaporetech.inf2007.team48.project_orion.dao.OrionAssetDao
import edu.singaporetech.inf2007.team48.project_orion.models.OrionAsset
import kotlinx.coroutines.flow.Flow

class OrionAssetRepository (
    private val orionAssetDao: OrionAssetDao
){
    // Section 1: OrionAssetDao
    @WorkerThread
    suspend fun insertAsset(orionAsset: OrionAsset) {
        orionAssetDao.insertAsset(orionAsset)
    }

    @WorkerThread
    fun getAllAssets(): Flow<List<OrionAsset>> {
        return orionAssetDao.getAllAssets()
    }

    @WorkerThread
    fun getAllAssetsByType(assetType: Int): Flow<List<OrionAsset>> {
        return orionAssetDao.getAllAssetsByType(assetType)
    }

    @WorkerThread
    fun getAllAssetsWithServiceStatus(inService: Boolean): Flow<List<OrionAsset>> {
        return orionAssetDao.getAllAssetsWithServiceStatus(inService)
    }

    @WorkerThread
    fun getAssetById(assetId: Int): Flow<OrionAsset> {
        return orionAssetDao.getAssetById(assetId)
    }

    @WorkerThread
    suspend fun updateAsset(newAsset: OrionAsset) {
        orionAssetDao.updateAsset(
            newAsset.assetId,
            newAsset.assetName,
            newAsset.assetType,
            newAsset.assetDesc,
            newAsset.dateAdded,
            newAsset.dateLastServiced,
            newAsset.inService
        )
    }

    @WorkerThread
    suspend fun deleteAsset(assetId: Int) {
        orionAssetDao.deleteAsset(assetId)
    }
}