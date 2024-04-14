package edu.singaporetech.inf2007.team48.project_orion.screens.addNewItem

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import edu.singaporetech.inf2007.team48.project_orion.controllers.OrionViewModel
import edu.singaporetech.inf2007.team48.project_orion.controllers.xbox.XboxInputViewModel
import edu.singaporetech.inf2007.team48.project_orion.services.api.OrionApiServicePost
import edu.singaporetech.inf2007.team48.project_orion.models.api.post.OrionApiPostChecklist
import kotlinx.coroutines.launch

/**
 * Composable function to display the screen for adding a new item to the checklist.
 * This screen contains the inputs for the checklist title and description.
 * It also contains the buttons to add the new item to the checklist or to cancel the request.
 * It displays a success message if the item is successfully added.
 * @param orionViewModel The view model for the Orion app.
 * @param xboxInputViewModel The view model for the Xbox input.
 * @param navController The navigation controller for the app.
 * @param orionApiPostService The service for making POST requests to the Orion API.
 * @see AddItemInputs
 * @see OrionApiPostChecklist
 * @see OrionApiServicePost
 * @see OrionViewModel
 * @see XboxInputViewModel
 * @see NavController
 *
 */
@Composable
fun AddNewItemScreen(
    orionViewModel: OrionViewModel,
    xboxInputViewModel: XboxInputViewModel,
    navController: NavController,
    orionApiPostService: OrionApiServicePost
) {

    var successMessage by rememberSaveable { mutableStateOf("") }
    val currentAssetId by orionViewModel.currentSearchedAssetId.collectAsState()
    val assetIdString = currentAssetId.toString()
    val coroutineScope = rememberCoroutineScope()


    Surface(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            AddItemInputs(
                assetIdString = assetIdString,
                onCancelRequest = {
                    navController.popBackStack()
                },
                onAddRequest = { checklistTitle, checklistDesc ->
                    coroutineScope.launch {
                        val response = orionApiPostService.addNewChecklist(
                            OrionApiPostChecklist.Request(
                                asset_id = assetIdString.toInt(),
                                checklist_title = checklistTitle,
                                checklist_desc = checklistDesc
                            )
                        )
                        successMessage = "Item successfully added with ID ${response.checklist_id}. ${response.message}"
                    }
                }
            )

            if (successMessage.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = successMessage, style = MaterialTheme.typography.bodyLarge)
            }
        }

    }
}