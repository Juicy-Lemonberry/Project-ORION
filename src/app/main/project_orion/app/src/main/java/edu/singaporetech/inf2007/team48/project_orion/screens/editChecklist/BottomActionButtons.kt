package edu.singaporetech.inf2007.team48.project_orion.screens.editChecklist

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import edu.singaporetech.inf2007.team48.project_orion.OrionScreens
import edu.singaporetech.inf2007.team48.project_orion.models.EditChecklistItem
import edu.singaporetech.inf2007.team48.project_orion.services.api.OrionApiServiceDelete
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

/**
 * To be placed at the bottom bar in a Scaffold...
 * This composable function displays the buttons for deleting selected checklist items and adding a new item to the checklist.
 * The delete button deletes the selected checklist items from the server.
 * The add button navigates to the screen for adding a new item to the checklist.
 * @param navController The navigation controller for the app.
 * @param checklistItems The list of checklist items to be displayed.
 * @param orionApiServiceDelete The service for making DELETE requests to the Orion API.
 * @param coroutineScope The coroutine scope for the app.
 * @param refreshChecklistItems The callback to refresh the checklist items.
 * @see EditChecklistItem
 * @see OrionApiServiceDelete
 *
 */
@Composable
fun BottomActionButtons(
    navController: NavController,
    checklistItems: List<EditChecklistItem>?,
    orionApiServiceDelete: OrionApiServiceDelete,
    coroutineScope: CoroutineScope,
    refreshChecklistItems: suspend() -> Unit
) {
    // Ensuring the bottom row has a specific height
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp) // Specify the height for the bottom row
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Button(
            onClick = { /* delete from server */
                coroutineScope.launch {
                    // Track IDs of successfully deleted checklist items
                    val successfullyDeletedIds = mutableListOf<Int>()
                    checklistItems?.filter { it.isSelected }?.forEach { item ->
                        val response = orionApiServiceDelete.deleteChecklist(item.orionChecklistItem.checklistId)
                        if (response.isSuccessful) {
                            // Handle successful deletion here, e.g., show a confirmation message
                            Log.d("DeleteChecklist", "Checklist item deleted: ${item.orionChecklistItem.checklistId}")
                        } else {
                            // Handle deletion failure here, e.g., show an error message
                            Log.e("DeleteChecklist", "Failed to delete checklist item: ${item.orionChecklistItem.checklistId}")
                        }
                    }
                    // Update the checklist items to exclude the successfully deleted items
                    if (checklistItems != null) {
                        refreshChecklistItems()
                    }

                }

            },
            modifier = Modifier.weight(1f)
        ) {
            Icon(Icons.Default.Delete, contentDescription = "Delete")
            Spacer(Modifier.width(8.dp))
            Text("Delete Selected")
        }
        Spacer(Modifier.width(16.dp))
        Button(
            onClick = { navController.navigate(OrionScreens.AddNewItemScreen.route) },
            modifier = Modifier.weight(1f)
        ) {
            Icon(Icons.Default.Add, contentDescription = "Add")
            Spacer(Modifier.width(8.dp))
            Text("Add New Item")
        }
    }
}