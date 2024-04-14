package edu.singaporetech.inf2007.team48.project_orion.models.api.get

import com.google.gson.annotations.SerializedName

data class OrionApiGetRecord(
    @SerializedName("service_id")
    val serviceId: Int,
    @SerializedName("asset_id")
    val assetId: Int,
    @SerializedName("user_id")
    val userId: Int,
    @SerializedName("service_desc")
    val serviceDescription: String,
    @SerializedName("date_recorded")
    val dateRecorded: Long,
    @SerializedName("date_serviced")
    val dateServiced: Long?
)

