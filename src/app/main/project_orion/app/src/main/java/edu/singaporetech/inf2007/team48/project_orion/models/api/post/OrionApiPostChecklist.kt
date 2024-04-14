package edu.singaporetech.inf2007.team48.project_orion.models.api.post

class OrionApiPostChecklist {
    data class Request(
        val asset_id: Int,
        val checklist_title: String,
        val checklist_desc: String
    )

    data class Response(
        val checklist_id: Int,
        val message: String
    )
}
