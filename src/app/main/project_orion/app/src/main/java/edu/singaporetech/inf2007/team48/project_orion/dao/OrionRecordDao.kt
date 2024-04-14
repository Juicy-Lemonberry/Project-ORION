package edu.singaporetech.inf2007.team48.project_orion.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import edu.singaporetech.inf2007.team48.project_orion.models.OrionRecord
import kotlinx.coroutines.flow.Flow


// Section 4: records
@Dao
interface OrionRecordDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertRecord(orionRecord: OrionRecord)

    @Query("SELECT * FROM records ORDER BY service_id ASC")
    fun getAllRecords(): Flow<List<OrionRecord>>

    @Query("SELECT * FROM records WHERE asset_id = :assetId ORDER BY service_id ASC")
    fun getAllRecordsByAssetId(assetId: Int): Flow<List<OrionRecord>>

    @Query("SELECT * FROM records WHERE user_id = :userId ORDER BY service_id ASC")
    fun getAllRecordsByUserId(userId: Int): Flow<List<OrionRecord>>

    @Query("SELECT * FROM records WHERE date_serviced NOT NULL ORDER BY service_id ASC")
    fun getAllRecordsThatHasServiceDate(): Flow<List<OrionRecord>>
    @Query("SELECT * FROM records WHERE date_serviced IS NULL ORDER BY service_id ASC")
    fun getAllRecordsThatHasNoServiceDate(): Flow<List<OrionRecord>>

    @Query("SELECT * FROM records WHERE service_id = :recordId")
    fun getRecordById(recordId: Int): Flow<OrionRecord>

    @Query("UPDATE records SET asset_id = :assetId, user_id = :userId, service_desc = :recordDesc, date_recorded = :dateRecorded, date_serviced = :dateServiced WHERE service_id = :serviceId")
    suspend fun updateRecord(
        serviceId: Int,
        assetId: Int,
        userId: Int,
        recordDesc: String,
        dateRecorded: Long,
        dateServiced: Long?
    )

    @Query("DELETE FROM records WHERE service_id = :serviceId")
    suspend fun deleteRecord(serviceId: Int)
}