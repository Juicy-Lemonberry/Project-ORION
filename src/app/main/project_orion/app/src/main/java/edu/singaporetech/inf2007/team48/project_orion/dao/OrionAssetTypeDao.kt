package edu.singaporetech.inf2007.team48.project_orion.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import edu.singaporetech.inf2007.team48.project_orion.models.OrionAssetType
import kotlinx.coroutines.flow.Flow


// Section 2: asset_types
@Dao
interface OrionAssetTypeDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertAssetType(orionAssetType: OrionAssetType)

    @Query("SELECT * FROM asset_types ORDER BY type_id ASC")
    fun getAllAssetTypes(): Flow<List<OrionAssetType>>

    @Query("SELECT * FROM asset_types WHERE type_id = :typeId")
    fun getAssetTypeById(typeId: Int): Flow<OrionAssetType>

    @Query("UPDATE asset_types SET type_desc = :typeDesc WHERE type_id = :typeId")
    suspend fun updateAssetType(typeId: Int, typeDesc: String)

    @Query("DELETE FROM asset_types WHERE type_id = :typeId")
    suspend fun deleteAssetType(typeId: Int)
}