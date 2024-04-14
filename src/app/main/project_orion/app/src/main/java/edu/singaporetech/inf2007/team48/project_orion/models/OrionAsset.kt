package edu.singaporetech.inf2007.team48.project_orion.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "assets")
data class OrionAsset(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "asset_id")
    val assetId: Int = 0,

    @ColumnInfo(name = "asset_name")
    val assetName: String = "",

    @ColumnInfo(name = "asset_type")
    val assetType: Int = 0,

    @ColumnInfo(name = "asset_desc")
    val assetDesc: String = "",

    @ColumnInfo(name = "date_added")
    val dateAdded: Long = 0, // Stores the date time in UNIX time

    @ColumnInfo(name = "date_last_serviced")
    val dateLastServiced : Long? = 0, // Stores the date time in UNIX time

    @ColumnInfo(name = "in_service")
    val inService: Boolean = false,
)

