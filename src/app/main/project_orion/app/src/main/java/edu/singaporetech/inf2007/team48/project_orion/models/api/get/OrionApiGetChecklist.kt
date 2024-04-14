package edu.singaporetech.inf2007.team48.project_orion.models.api.get

data class OrionApiGetChecklist(
    val checklist_id: Int,
    val asset_id: Int,
    val checklist_title: String,
    val checklist_desc: String
)

//pass this into _?_ a way that the checklist can read this
//2 integer 2 string
//retrofit


