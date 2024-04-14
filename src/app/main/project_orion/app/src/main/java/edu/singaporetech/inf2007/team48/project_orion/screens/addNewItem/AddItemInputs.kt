package edu.singaporetech.inf2007.team48.project_orion.screens.addNewItem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

/**
 * Composable function to display the inputs for adding a new item to the checklist.
 *
 * @param assetIdString The asset ID of the checklist.
 * @param onCancelRequest The callback to cancel the add item request.
 * @param onAddRequest The callback to add the new item to the checklist.
 */
@Composable
fun AddItemInputs(
    assetIdString: String,
    onCancelRequest: () -> Unit,
    onAddRequest: (title: String, description: String) -> Unit
) {
    var checklistTitle by rememberSaveable { mutableStateOf("") }
    var checklistDesc by rememberSaveable { mutableStateOf("") }

    val isInputFilled = checklistDesc.isNotBlank() && checklistDesc.isNotBlank()

    OutlinedTextField(
        value = assetIdString,
        onValueChange = {},
        label = { Text("Asset ID") },
        enabled = false
    )
    OutlinedTextField(
        value = checklistTitle,
        onValueChange = { checklistTitle = it },
        label = { Text("Checklist Title") }
    )
    OutlinedTextField(
        value = checklistDesc,
        onValueChange = { checklistDesc = it },
        label = { Text("Checklist Description") }
    )

    Spacer(modifier = Modifier.height(16.dp))

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = { onCancelRequest() },
            modifier = Modifier.weight(1f)
        ) {
            Text(text = "Cancel")
        }
        Spacer(modifier = Modifier.width(16.dp))
        Button(
            onClick = {
                onAddRequest(checklistTitle, checklistDesc)
                checklistTitle = ""
                checklistDesc = ""
            },
            modifier = Modifier.weight(1f),
            enabled = isInputFilled
        ) {
            Text(text = "Add new item")
        }
    }
}