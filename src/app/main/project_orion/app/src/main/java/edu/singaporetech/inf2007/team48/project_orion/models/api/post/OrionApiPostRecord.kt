package edu.singaporetech.inf2007.team48.project_orion.models.api.post

import com.google.gson.annotations.SerializedName

class OrionApiPostRecord {
    data class Request(
        @SerializedName("asset_id")
        val assetId: Int,
        @SerializedName("user_id")
        val userId: Int,
        @SerializedName("service_desc")
        val serviceDescription: String,
        @SerializedName("date_recorded")
        val dateRecorded: Long, //mandatory
        @SerializedName("date_serviced")
        val dateServiced: Long? = null,
        @SerializedName("service_id")
        val serviceId: Int? = null
    )

    //pass request
    //get back response, using retrofit __> json to readable data

    data class Response(
        @SerializedName("message")
        val message: String,
        @SerializedName("service_id")
        val serviceId: Int
    )
}
