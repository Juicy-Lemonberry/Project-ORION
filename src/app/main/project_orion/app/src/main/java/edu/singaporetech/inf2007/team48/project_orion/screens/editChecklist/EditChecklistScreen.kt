package edu.singaporetech.inf2007.team48.project_orion.screens.editChecklist

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import edu.singaporetech.inf2007.team48.project_orion.controllers.OrionViewModel
import edu.singaporetech.inf2007.team48.project_orion.services.api.OrionApiServiceDelete
import edu.singaporetech.inf2007.team48.project_orion.controllers.xbox.XboxInputViewModel
import edu.singaporetech.inf2007.team48.project_orion.models.OrionChecklist
import edu.singaporetech.inf2007.team48.project_orion.models.EditChecklistItem
import edu.singaporetech.inf2007.team48.project_orion.OrionScreens
import edu.singaporetech.inf2007.team48.project_orion.components.OrionTopAppBarWithSaveIcon
import edu.singaporetech.inf2007.team48.project_orion.services.api.OrionApiServiceGet
import edu.singaporetech.inf2007.team48.project_orion.services.api.OrionApiServicePost
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import kotlinx.coroutines.launch

/**
 * Composable function to display the screen for editing a checklist.
 * This screen displays the checklist items fetched from the API.
 * It allows the user to select and delete checklist items.
 * @param orionViewModel The view model for the Orion app.
 * @param xboxInputViewModel The view model for the Xbox input.
 * @param navController The navigation controller for the app.
 * @param orionApi The service for making GET requests to the Orion API.
 * @param orionApiPostService The service for making POST requests to the Orion API.
 * @param orionApiServiceDelete The service for making DELETE requests to the Orion API.
 * @see EditChecklistItem
 * @see OrionApiServiceGet
 * @see OrionApiServicePost
 * @see OrionApiServiceDelete
 * @see OrionViewModel
 * @see XboxInputViewModel
 * @see NavController
 *
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditChecklistScreen(
    orionViewModel: OrionViewModel,
    xboxInputViewModel: XboxInputViewModel,
    navController: NavController,
    orionApi: OrionApiServiceGet,
    orionApiPostService: OrionApiServicePost,
    orionApiServiceDelete: OrionApiServiceDelete
) {

    // Use a mutable state to hold the checklist items fetched from the API
    var checklistItems by rememberSaveable { mutableStateOf<List<EditChecklistItem>?>(null) }
    var isLoading by rememberSaveable { mutableStateOf(true) }
    // Keep track of the "Select All" checkbox state
    var selectAllChecked by rememberSaveable { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val currentAssetId by orionViewModel.currentSearchedAssetId.collectAsState()


    // Function to refetch or locally update checklist items
    val refreshChecklistItems: suspend() -> Unit = {
        coroutineScope.launch {
            val updatedItems = orionApi.getChecklists(assetId = currentAssetId, checklistId = null)
            checklistItems = updatedItems.map { apiItem ->
                createNewChecklistObject(
                    checklistId = apiItem.checklist_id,
                    assetId = apiItem.asset_id,
                    checklistTitle = apiItem.checklist_title ?: "Title not available",
                    checklistDesc = apiItem.checklist_desc ?: "Description not available"
                )
            }
        }
    }



    // Fetch the checklist items when the screen is composed
    LaunchedEffect(currentAssetId) {
        isLoading = true
        try {
            // Assume assetId = 1 is hardcoded for now, replace with dynamic fetching if needed
            val fetchedItems = orionApi.getChecklists(assetId = currentAssetId, checklistId = null)
            checklistItems = fetchedItems.map { apiItem ->
                createNewChecklistObject(
                    checklistId = apiItem.checklist_id,
                    assetId = apiItem.asset_id,
                    checklistTitle = apiItem.checklist_title ?: "Title not available",
                    checklistDesc = apiItem.checklist_desc ?: "Description not available"
                )
            }
            isLoading = false
        } catch (e: Exception) {
            isLoading = false
        }
    }




    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            topBar = {
                OrionTopAppBarWithSaveIcon(
                    // there's no SaveIcon because i removed it in the OrionTopAppBarWithSaveIcon.kt file
                    title = "Edit Checklist",
                    onSaveClick = { /*TODO*/ }, //useless atm since i removed the icon
                    onBackClick = {
                        // Attempt to pop back to the ViewAssetScreen, if not found in back stack, do not pop
                        val popped = navController.popBackStack(route = OrionScreens.ViewAssetScreen.route, inclusive = false)
                        if (!popped) {
                            // If ViewAssetScreen was not in the back stack, explicitly navigate to it
                            navController.navigate(OrionScreens.ViewAssetScreen.route) {
                                // This clears everything up to the ViewAssetScreen from the back stack, making ViewAssetScreen the top
                                popUpTo(OrionScreens.ViewAssetScreen.route) { inclusive = true }
                                // Avoid multiple copies of the same screen
                                launchSingleTop = true
                            }
                        }
                    }
                )
            },
            bottomBar = {
                BottomActionButtons(
                    navController = navController,
                    checklistItems = checklistItems,
                    orionApiServiceDelete = orionApiServiceDelete,
                    coroutineScope = coroutineScope,
                    refreshChecklistItems = { coroutineScope.launch { refreshChecklistItems() } } // Pass the function here
                )
            }




        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {

                val onCheckAllChange = { checked: Boolean ->
                    selectAllChecked = checked
                    checklistItems = checklistItems?.map { it.copy(isSelected = checked) }
                }

                SelectAllCheckbox(
                    selectAllChecked,
                    onCheckAllChange
                )

                Divider()

                LazyColumn {
                    checklistItems?.let { items ->
                        items(items, key = { it.orionChecklistItem.checklistId }) { item -> // Using checklistId as a key for better performance and correctness
//                            var isChecked by remember { mutableStateOf(item.isSelected) }
                            ChecklistItemCard(
                                checklistTitle = item.orionChecklistItem.checklistTitle,
                                isChecked = item.isSelected,
                                onCheckedChange = { isSelected ->
                                    // Create a new list with the updated item
                                    checklistItems = checklistItems?.map {
                                        if (it.orionChecklistItem.checklistId == item.orionChecklistItem.checklistId) {
                                            it.copy(isSelected = isSelected)
                                        } else it
                                    }
                                    // After an individual item is updated, re-evaluate the state of selectAllChecked
                                    selectAllChecked = checklistItems?.all { it.isSelected } ?: false
                                }
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun createNewChecklistObject(
    checklistId: Int,
    assetId: Int,
    checklistTitle: String,
    checklistDesc: String
): EditChecklistItem {
    return EditChecklistItem(
        OrionChecklist(
            checklistId = checklistId,
            assetId = assetId,
            checklistTitle = checklistTitle,
            checklistDesc = checklistDesc
        ),
        isSelected = false,
        isMarkedForDeletion = false,
        isDeleted = false
    )
}