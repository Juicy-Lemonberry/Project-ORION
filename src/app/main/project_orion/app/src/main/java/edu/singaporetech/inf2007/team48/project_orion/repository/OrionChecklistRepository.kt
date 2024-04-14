package edu.singaporetech.inf2007.team48.project_orion.repository

import androidx.annotation.WorkerThread
import edu.singaporetech.inf2007.team48.project_orion.dao.OrionChecklistDao
import edu.singaporetech.inf2007.team48.project_orion.models.OrionChecklist
import kotlinx.coroutines.flow.Flow

class OrionChecklistRepository(
    private val orionChecklistDao: OrionChecklistDao
)
{
    // Section 3: OrionChecklistDao
    @WorkerThread
    suspend fun insertChecklist(orionChecklist: OrionChecklist) {
        orionChecklistDao.insertChecklist(orionChecklist)
    }

    @WorkerThread
    fun getAllChecklists(): Flow<List<OrionChecklist>> {
        return orionChecklistDao.getAllChecklists()
    }

    @WorkerThread
    fun getAllChecklistsByAssetId(assetId: Int): Flow<List<OrionChecklist>> {
        return orionChecklistDao.getAllChecklistsByAssetId(assetId)
    }

    @WorkerThread
    fun getChecklistById(checklistId: Int): Flow<OrionChecklist> {
        return orionChecklistDao.getChecklistById(checklistId)
    }

    @WorkerThread
    suspend fun updateChecklist(newChecklist: OrionChecklist) {
        orionChecklistDao.updateChecklist(
            newChecklist.checklistId,
            newChecklist.assetId,
            newChecklist.checklistTitle,
            newChecklist.checklistDesc
        )
    }

    @WorkerThread
    suspend fun deleteChecklist(checklistId: Int) {
        orionChecklistDao.deleteChecklist(checklistId)
    }
}