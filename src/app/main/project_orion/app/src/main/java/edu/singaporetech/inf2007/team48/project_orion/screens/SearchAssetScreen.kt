package edu.singaporetech.inf2007.team48.project_orion.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import edu.singaporetech.inf2007.team48.project_orion.R
import edu.singaporetech.inf2007.team48.project_orion.controllers.OrionViewModel
import edu.singaporetech.inf2007.team48.project_orion.services.api.OrionApiServiceGet
import edu.singaporetech.inf2007.team48.project_orion.controllers.xbox.XboxInputViewModel
import edu.singaporetech.inf2007.team48.project_orion.models.OrionAsset
import edu.singaporetech.inf2007.team48.project_orion.OrionScreens
import edu.singaporetech.inf2007.team48.project_orion.components.AssetInfoCard
import edu.singaporetech.inf2007.team48.project_orion.components.OrionTopAppBarWithImageIcon
import kotlinx.coroutines.launch



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchAssetScreen(
    orionApi: OrionApiServiceGet,
    orionViewModel: OrionViewModel,
    xboxInputViewModel: XboxInputViewModel,
    navController: NavController
) {
    val context = LocalContext.current;
    val coroutineScope = rememberCoroutineScope()

    // NOTE: This is the full list of Orions that is fetched from backend API
    val orionAssetList = rememberSaveable { mutableStateOf(listOf<OrionAsset>()) }
    // NOTE: This is the actual list of Orions that are displayed into the lazy column
    val searchedOrionList = rememberSaveable { mutableStateOf(listOf<OrionAsset>()) }

    LaunchedEffect(Unit) {
        // Coroutine to fetch list of Orions from Backend API
        coroutineScope.launch {
            try {
                val fetchedAssets = orionApi.getAssets(assetId = null, assetName = null, typeId = null, isActive = null)
                val result = fetchedAssets.map { apiAsset ->
                    OrionAsset(
                        assetId = apiAsset.asset_id,
                        assetName = apiAsset.asset_name,
                        assetType = apiAsset.type_id,
                        assetDesc = apiAsset.asset_desc ?: "No Description",
                        dateAdded = apiAsset.date_added,
                        dateLastServiced = apiAsset.date_last_serviced,
                        inService = apiAsset.is_active == 1
                    )
                }

                orionAssetList.value = result;
                searchedOrionList.value = result;
            } catch (e: Exception) {
                Log.e("SearchAssetScreen", "Error fetching assets from network: ${e.localizedMessage}", e)
                Toast.makeText(context, "Failed to fetch list of Orions\r\nTry again or contact admin!", Toast.LENGTH_SHORT).show()
            }
        }
    }

//    orionAssetList.value += OrionAsset(
//        assetName = "SNB9538E",
//        assetType = 4,
//        assetDesc = "Hyundai Kona EV",
//        dateAdded = 1682322087000,
//        dateLastServiced = 1683186087000
//    )

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            topBar = {
                OrionTopAppBarWithImageIcon(
                    title = "Search Assets",
                    onProfilePictureClick = {},
                    onBackClick = { navController.popBackStack() }
                )
            },
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center
            ) {
                SearchField(
                    // Update the Orions list to be displayed as the user types in the search field...
                    onNewInput = {
                        searchedOrionList.value = orionAssetList.value.filter { orionAsset ->
                            orionAsset.assetName.contains(it, ignoreCase = true)
                        }
                    }
                )
                Divider()

                OrionCardsList(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    searchedOrionList.value,
                    onOrionSelected = {selectedAsset ->
                        // Set selected asset into ViewModel and pass to next Screen.
                        orionViewModel.setSearchedAssetId(selectedAsset.assetId)
                        navController.navigate(OrionScreens.ViewAssetScreen.createRoute(selectedAsset.assetId.toString()))
                        Log.d("SearchAssetScreen", "Asset clicked: ${selectedAsset.assetId}")
                    }
                )
            }
        }
    }
}

@Composable
private fun SearchField(
    onNewInput: (newInput: String) -> Unit
){
    val searchInput = rememberSaveable { mutableStateOf("") }

    TextField(
        value = searchInput.value,
        onValueChange = {
            searchInput.value = it
            onNewInput(it)
        },
        label = { Text("Search") },
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        singleLine = true,
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search"
            )
        }
    )
}

@Composable
private fun OrionCardsList(
    modifier: Modifier,
    orionList: List<OrionAsset>,
    onOrionSelected: (selected: OrionAsset) -> Unit
)  {
    LazyColumn(
        modifier = modifier
    ) {
        items(orionList.size) { index ->
            val asset = orionList[index]
            AssetInfoCard(
                assetImage = R.drawable.asset_image,
                assetName = asset.assetName,
                assetType = "Electric Vehicle",
                assetDesc = asset.assetDesc,
                dateAdded = asset.dateAdded,
                dateServiced = asset.dateLastServiced,
                onClick = {
                    onOrionSelected(asset)
                }
            )
        }
    }
}