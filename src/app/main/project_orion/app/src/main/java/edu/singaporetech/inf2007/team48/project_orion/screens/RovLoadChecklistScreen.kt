package edu.singaporetech.inf2007.team48.project_orion.screens

import android.content.pm.ActivityInfo
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import edu.singaporetech.inf2007.team48.project_orion.R
import edu.singaporetech.inf2007.team48.project_orion.controllers.OrionViewModel
import edu.singaporetech.inf2007.team48.project_orion.controllers.xbox.XboxInputViewModel
import edu.singaporetech.inf2007.team48.project_orion.services.api.OrionApiClient.orionApi
import edu.singaporetech.inf2007.team48.project_orion.OrionScreens
import edu.singaporetech.inf2007.team48.project_orion.components.LockScreenOrientation
import edu.singaporetech.inf2007.team48.project_orion.components.OrionTopAppBar
import kotlinx.coroutines.launch

@OptIn(ExperimentalPermissionsApi::class, ExperimentalMaterial3Api::class)
@Composable
fun RovLoadChecklistScreen(
    navController: NavController,
    xboxInputViewModel: XboxInputViewModel,
    orionViewModel: OrionViewModel,
) {

    LockScreenOrientation(orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val loadingProgressBackgroundColor = MaterialTheme.colorScheme.secondaryContainer
    val loadingProgressColor = MaterialTheme.colorScheme.primary
    val assetSearchText = remember { mutableStateOf("") }
    val assetResultText = remember { mutableStateOf("") }
    val searchQueryExecuted = remember { mutableStateOf(false) }
    val assetSearchTextInvalid = remember { mutableStateOf(false) }


    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painter = painterResource(id = R.drawable.background_06),
                contentScale = ContentScale.FillHeight,
                alpha = 0.5f
            )
    ) {
        Scaffold(
            topBar = {
                OrionTopAppBar(
                    title = "Load asset checklist",
                    onBackClick = { navController.popBackStack() },
                    colours = TopAppBarDefaults.topAppBarColors(
                        containerColor = if (isSystemInDarkTheme()) {
                            Color.Black.copy(alpha = 0.3f)
                        } else {
                            Color.White.copy(alpha = 0.3f)
                        }
                    )
                )
            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally,


                    ) {
                    Spacer(modifier = Modifier.height(0.dp)) // Dummy spacer to push content down
                    Column(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.SpaceEvenly,
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(
                            text = "Enter an asset ID to load it's checklist as a quick reference.",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center,
                        )

                        TextField(
                            value = assetSearchText.value,
                            onValueChange = { assetSearchText.value = it },
                            label = { Text("Search for asset ID") },
                            modifier = Modifier.fillMaxWidth(),
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,

                                ),
                            singleLine = true,


                            )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                scope.launch {
                                    if (assetSearchText.value.trim().isEmpty() || assetSearchText.value.trim().toIntOrNull() == null){
                                        assetSearchTextInvalid.value = true
                                        return@launch
                                    }
                                    val checklist = orionApi.getService.getChecklists(
                                        checklistId = null,
                                        assetId = assetSearchText.value.trim().toInt()
                                    )
                                    if (checklist.isEmpty()) {
                                        assetSearchTextInvalid.value = true
                                        searchQueryExecuted.value = true
                                        assetResultText.value = assetSearchText.value
                                        return@launch
                                    }
                                    val asset = orionApi.getService.getAssets(
                                        assetId = assetSearchText.value.trim().toInt(),
                                        assetName = null,
                                        typeId = null,
                                        isActive = null
                                    ).firstOrNull()
                                    val assetType = orionApi.getService.getAssetTypes(
                                        typeId = asset?.type_id ?: -1,
                                        typeDesc = null
                                    ).firstOrNull()

                                    orionViewModel.loadQuickReferenceChecklistFromApi(
                                        orionApiGetChecklist = checklist,
                                        assetName = asset?.asset_name ?: "NOT IN DATABASE",
                                        assetType = assetType?.type_desc ?: "NOT IN DATABASE",
                                    )
                                    searchQueryExecuted.value = true
                                    assetResultText.value = assetSearchText.value
                                    assetSearchTextInvalid.value = false
                                }
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            Text(
                                text = "Load checklist",
                                fontWeight = FontWeight.Bold,
                                style = MaterialTheme.typography.bodyLarge,
                            )
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text =
                            if (!searchQueryExecuted.value) "Or, press skip to jump straight into the ROV view port."
                            else if (assetSearchTextInvalid.value) "Invalid asset ID. Please enter a valid numerical asset ID."
                            else if (orionViewModel.quickReferenceChecklist.collectAsState().value.asset_checklist.isEmpty()) "No checklist found for asset ID ${assetResultText.value}."
                            else "Checklist loaded for asset ID ${assetResultText.value}.\n" +
                                    "Loaded ${orionViewModel.quickReferenceChecklist.collectAsState().value.asset_checklist.size} checklists for ${orionViewModel.quickReferenceChecklist.collectAsState().value.asset_name}.",
                            style = MaterialTheme.typography.bodyLarge,
                            color =
                            if (!searchQueryExecuted.value) MaterialTheme.colorScheme.onSurface
                            else if (orionViewModel.quickReferenceChecklist.collectAsState().value.asset_checklist.isEmpty() || assetSearchTextInvalid.value) Color.Red
                            else Color.Green,
                            textAlign = TextAlign.Center,
                        )
                    }

                    Button(
                        onClick = { navController.navigate(OrionScreens.RovViewPortScreen.route) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor =
                            if (orionViewModel.quickReferenceChecklist.collectAsState().value.asset_checklist.isNotEmpty()) {
                                Color.Green
                            } else {
                                MaterialTheme.colorScheme.secondary
                            }
                        )
                    ) {
                        Text(
                            text =
                            if (orionViewModel.quickReferenceChecklist.collectAsState().value.asset_checklist.isNotEmpty()) "LETS GO! ➡"
                            else "Skip Loading ➡",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }
            }
        }
    }
}