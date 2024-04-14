package edu.singaporetech.inf2007.team48.project_orion.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "checklists")
data class OrionChecklist(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "checklist_id")
    val checklistId: Int = 0,

    @ColumnInfo(name = "asset_id")
    val assetId: Int = 0,

    @ColumnInfo(name = "checklist_title")
    val checklistTitle: String = "",

    @ColumnInfo(name = "checklist_desc")
    val checklistDesc: String = "",
)