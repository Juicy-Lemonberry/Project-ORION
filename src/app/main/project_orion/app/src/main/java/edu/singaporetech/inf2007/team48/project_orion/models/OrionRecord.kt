package edu.singaporetech.inf2007.team48.project_orion.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "records")
data class OrionRecord(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "service_id")
    val serviceId: Int = 0,

    @ColumnInfo(name = "asset_id")
    val assetId: Int = 0,

    @ColumnInfo(name = "user_id")
    val userId: Int = 0,

    @ColumnInfo(name = "service_desc")
    val serviceDesc: String = "",

    @ColumnInfo(name = "date_recorded")
    val dateRecorded: Long = 0,

    @ColumnInfo(name = "date_serviced")
    val dateServiced: Long? = 0,
)
