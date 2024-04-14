package edu.singaporetech.inf2007.team48.project_orion.models

data class EditChecklistItem (
    val orionChecklistItem : OrionChecklist,
    var isSelected : Boolean = false,
    var isMarkedForDeletion : Boolean = false,
    var isDeleted : Boolean = false,
)