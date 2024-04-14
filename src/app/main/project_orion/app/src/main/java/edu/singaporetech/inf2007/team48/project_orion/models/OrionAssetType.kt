package edu.singaporetech.inf2007.team48.project_orion.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "asset_types")
data class OrionAssetType(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "type_id")
    val typeId: Int = 0,

    @ColumnInfo(name = "type_desc")
    val typeDesc: String = "",
)