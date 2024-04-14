package edu.singaporetech.inf2007.team48.project_orion.repository

import androidx.annotation.WorkerThread
import edu.singaporetech.inf2007.team48.project_orion.dao.OrionRecordDao
import edu.singaporetech.inf2007.team48.project_orion.models.OrionRecord
import kotlinx.coroutines.flow.Flow

class OrionRecordRepository(
    private val orionRecordDao: OrionRecordDao
)
{
    // Section 4: OrionRecordDao
    @WorkerThread
    suspend fun insertRecord(orionRecord: OrionRecord) {
        orionRecordDao.insertRecord(orionRecord)
    }

    @WorkerThread
    fun getAllRecords(): Flow<List<OrionRecord>> {
        return orionRecordDao.getAllRecords()
    }

    @WorkerThread
    fun getAllRecordsByAssetId(assetId: Int): Flow<List<OrionRecord>> {
        return orionRecordDao.getAllRecordsByAssetId(assetId)
    }

    @WorkerThread
    fun getAllRecordsByUserId(userId: Int): Flow<List<OrionRecord>> {
        return orionRecordDao.getAllRecordsByUserId(userId)
    }

    @WorkerThread
    fun getAllRecordsThatHasServiceDate(): Flow<List<OrionRecord>> {
        return orionRecordDao.getAllRecordsThatHasServiceDate()
    }

    @WorkerThread
    fun getAllRecordsThatHasNoServiceDate(): Flow<List<OrionRecord>> {
        return orionRecordDao.getAllRecordsThatHasNoServiceDate()
    }

    @WorkerThread
    fun getRecordById(recordId: Int): Flow<OrionRecord> {
        return orionRecordDao.getRecordById(recordId)
    }

    @WorkerThread
    suspend fun updateRecord(newRecord: OrionRecord) {
        orionRecordDao.updateRecord(
            newRecord.serviceId,
            newRecord.assetId,
            newRecord.userId,
            newRecord.serviceDesc,
            newRecord.dateRecorded,
            newRecord.dateServiced
        )
    }

    @WorkerThread
    suspend fun deleteRecord(serviceId: Int) {
        orionRecordDao.deleteRecord(serviceId)
    }
}