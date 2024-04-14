package edu.singaporetech.inf2007.team48.project_orion.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import edu.singaporetech.inf2007.team48.project_orion.models.OrionChecklist
import kotlinx.coroutines.flow.Flow


// Section 3: checklists
@Dao
interface OrionChecklistDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChecklist(orionChecklist: OrionChecklist)

    @Query("SELECT * FROM checklists ORDER BY checklist_id ASC")
    fun getAllChecklists(): Flow<List<OrionChecklist>>

    @Query("SELECT * FROM checklists WHERE asset_id = :assetId ORDER BY checklist_id ASC")
    fun getAllChecklistsByAssetId(assetId: Int): Flow<List<OrionChecklist>>

    @Query("SELECT * FROM checklists WHERE checklist_id = :checklistId")
    fun getChecklistById(checklistId: Int): Flow<OrionChecklist>

    @Query("UPDATE checklists SET asset_id = :assetId, checklist_title = :checklistTitle, checklist_desc = :checklistDesc WHERE checklist_id = :checklistId")
    suspend fun updateChecklist(
        checklistId: Int,
        assetId: Int,
        checklistTitle: String,
        checklistDesc: String
    )

    @Query("DELETE FROM checklists WHERE checklist_id = :checklistId")
    suspend fun deleteChecklist(checklistId: Int)
}