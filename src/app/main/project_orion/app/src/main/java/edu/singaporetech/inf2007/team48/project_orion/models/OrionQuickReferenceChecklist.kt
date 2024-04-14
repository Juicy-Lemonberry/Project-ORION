package edu.singaporetech.inf2007.team48.project_orion.models

data class OrionQuickReferenceChecklist (
    var asset_id: Int = -1,
    var asset_name: String = "UNDEFINED",
    var asset_type: String = "UNDEFINED",
    var asset_checklist: List<OrionQuickReferenceChecklistItems> = emptyList()
)