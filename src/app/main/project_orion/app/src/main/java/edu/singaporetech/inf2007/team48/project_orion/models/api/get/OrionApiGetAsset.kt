package edu.singaporetech.inf2007.team48.project_orion.models.api.get

data class OrionApiGetAsset(
    val asset_id: Int,
    val asset_name: String,
    val type_id: Int,
    val asset_desc: String?,
    val date_added: Long,
    val date_last_serviced: Long?,
    val is_active: Int
)

