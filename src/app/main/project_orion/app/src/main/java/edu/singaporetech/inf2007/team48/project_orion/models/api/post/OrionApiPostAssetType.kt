package edu.singaporetech.inf2007.team48.project_orion.models.api.post

class OrionApiPostAssetType {
    data class Request(
        val type_id: Int? = null,
        val type_desc: String
    )

    data class Response(
        val message: String,
        val type_id: Int
    )
}
