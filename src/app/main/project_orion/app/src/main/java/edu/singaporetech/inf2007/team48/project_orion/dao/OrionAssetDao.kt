package edu.singaporetech.inf2007.team48.project_orion.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import edu.singaporetech.inf2007.team48.project_orion.models.OrionAsset
import kotlinx.coroutines.flow.Flow


// Section 1: assets
@Dao
interface OrionAssetDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAsset(orionAsset: OrionAsset)

    @Query("SELECT * FROM assets ORDER BY asset_id ASC")
    fun getAllAssets(): Flow<List<OrionAsset>>

    @Query("SELECT * FROM assets WHERE asset_type = :assetType ORDER BY asset_id ASC")
    fun getAllAssetsByType(assetType: Int): Flow<List<OrionAsset>>

    @Query("SELECT * FROM assets WHERE in_service = :inService ORDER BY asset_id ASC")
    fun getAllAssetsWithServiceStatus(inService: Boolean): Flow<List<OrionAsset>>

    @Query("SELECT * FROM assets WHERE asset_id = :assetId")
    fun getAssetById(assetId: Int): Flow<OrionAsset>

    @Query("UPDATE assets SET asset_name = :assetName, asset_type = :assetType, asset_desc = :assetDesc, date_added = :dateAdded, date_last_serviced = :dateLastServiced, in_service = :inService WHERE asset_id = :assetId")
    suspend fun updateAsset(
        assetId: Int,
        assetName: String,
        assetType: Int,
        assetDesc: String,
        dateAdded: Long,
        dateLastServiced: Long?,
        inService: Boolean
    )

    @Query("DELETE FROM assets WHERE asset_id = :assetId")
    suspend fun deleteAsset(assetId: Int)
}
