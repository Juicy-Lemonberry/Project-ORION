package edu.singaporetech.inf2007.team48.project_orion.screens

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import edu.singaporetech.inf2007.team48.project_orion.controllers.OrionViewModel

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
//import androidx.navigation.NavController
//import edu.singaporetech.inf2007.team48.project_orion.controllers.OrionViewModel
import edu.singaporetech.inf2007.team48.project_orion.services.api.OrionApiServiceGet
import edu.singaporetech.inf2007.team48.project_orion.models.api.get.OrionApiGetChecklist
import kotlinx.coroutines.launch
//import java.lang.reflect.Modifier
import androidx.compose.foundation.layout.padding
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.singaporetech.inf2007.team48.project_orion.controllers.xbox.XboxInputViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun testingpulldata(
    orionViewModel: OrionViewModel,
    xboxInputViewModel: XboxInputViewModel,
    navController: NavController,
    orionApi: OrionApiServiceGet
) {
    // State to hold your list of checklists
    var checklists by remember { mutableStateOf<List<OrionApiGetChecklist>?>(null) }
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        launch {
            try {
                val fetchedChecklists = orionApi.getChecklists(assetId = 1, checklistId = null)
                checklists = fetchedChecklists
                isLoading = false
            } catch (e: Exception) {
                isLoading = false // Handle error appropriately
            }
        }
    }

    Column(modifier = Modifier.padding(16.dp)) {
        if (isLoading) {
            Text(text = "Loading...", style = MaterialTheme.typography.bodyLarge)
        } else {
            checklists?.let { fetchedChecklists ->
                LazyColumn {
                    items(fetchedChecklists) { checklist ->
                        ChecklistItem(checklist = checklist)
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
            } ?: Text(text = "No checklists found", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@Composable
fun ChecklistItem(checklist: OrionApiGetChecklist) {
    Column {
        Text(text = "Checklist ID: ${checklist.checklist_id}", style = MaterialTheme.typography.bodyLarge)
        Text(text = "Asset ID: ${checklist.asset_id}", style = MaterialTheme.typography.bodyMedium)
        Text(text = "Title: ${checklist.checklist_title}", style = MaterialTheme.typography.bodyMedium)
        Text(text = " ${checklist.checklist_desc}", style = MaterialTheme.typography.bodyMedium)
    }



//    val coroutineScope = rememberCoroutineScope()
//    var assets by remember { mutableStateOf<List<OrionAsset>>(emptyList()) }
//    var isLoading by remember { mutableStateOf(true) }  // Optional: Manage loading state
//
//    // Fetch assets when the composable enters the Composition
//    LaunchedEffect(Unit) {
//        coroutineScope.launch {
//            try {
//                isLoading = true
//                val fetchedAssets = orionApi.getAssets(null, null, null, null)
//                assets = fetchedAssets.map { apiAsset ->
//                    OrionAsset(
//                        assetName = apiAsset.asset_name,
//                        assetType = apiAsset.type_id,
//                        assetDesc = apiAsset.asset_desc ?: "No Description",
//                        dateAdded = apiAsset.date_added,
//                        dateLastServiced = apiAsset.date_last_serviced,
//                        inService = apiAsset.is_active == 1 // Assuming 'is_active' is an Int where 1 represents true
//                    )
//                }
//            } catch (e: Exception) {
//                // Handle the error appropriately
//                e.printStackTrace()
//            } finally {
//                isLoading = false
//            }
//        }
//    }
//
//    // UI
//    Column {
//        if (isLoading) {
//            Text("Loading...")
//        } else {
//            LazyColumn {
//                items(assets) { asset ->
//                    AssetItem(asset = asset)
//                }
//            }
//        }
//    }
//}
//
//@Composable
//fun AssetItem(asset: OrionAsset) {
//    Column {
//        Text("Name: ${asset.assetName}")
//        Text("Type: ${asset.assetType}")
//        Text("Description: ${asset.assetDesc}")
//        Text("Added: ${asset.dateAdded}")
//        Text("Last Serviced: ${asset.dateLastServiced ?: "N/A"}")
//    }
}
