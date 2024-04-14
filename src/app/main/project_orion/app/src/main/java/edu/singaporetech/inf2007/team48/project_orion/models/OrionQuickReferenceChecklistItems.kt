package edu.singaporetech.inf2007.team48.project_orion.models

data class OrionQuickReferenceChecklistItems (
    val checklist_id: Int,
    val asset_id: Int,
    val checklist_title: String,
    val checklist_desc: String,
    var checklist_completed: Boolean,
)