package edu.singaporetech.inf2007.team48.project_orion.models.api.post

class OrionApiPostAsset {
    data class Request(
        val asset_id: Int? = null,
        val asset_name: String,
        val type_id: Int,
        val asset_desc: String,
        val date_added: Long,
        val date_last_serviced: Long? = null,
        val is_active: Int
    )

    data class Response(
        val asset_id: Int,
        val message: String
    )
}
