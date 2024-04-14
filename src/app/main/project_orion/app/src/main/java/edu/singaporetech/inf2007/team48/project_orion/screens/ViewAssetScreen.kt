package edu.singaporetech.inf2007.team48.project_orion.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import edu.singaporetech.inf2007.team48.project_orion.R
import edu.singaporetech.inf2007.team48.project_orion.controllers.OrionViewModel
import edu.singaporetech.inf2007.team48.project_orion.services.api.OrionApiServiceGet
import edu.singaporetech.inf2007.team48.project_orion.services.api.OrionApiServicePost
import edu.singaporetech.inf2007.team48.project_orion.models.api.post.OrionApiPostRecord
import edu.singaporetech.inf2007.team48.project_orion.controllers.xbox.XboxInputViewModel
import edu.singaporetech.inf2007.team48.project_orion.models.OrionAsset
import edu.singaporetech.inf2007.team48.project_orion.models.OrionRecord
import edu.singaporetech.inf2007.team48.project_orion.services.findUserByID
import edu.singaporetech.inf2007.team48.project_orion.OrionScreens
import edu.singaporetech.inf2007.team48.project_orion.components.AssetInfoCard
import edu.singaporetech.inf2007.team48.project_orion.components.AssetInfoDescText
import edu.singaporetech.inf2007.team48.project_orion.components.AssetServiceDescText
import edu.singaporetech.inf2007.team48.project_orion.components.OrionTopAppBarWithEditIcon
import edu.singaporetech.inf2007.team48.project_orion.extensionFunctions.context.showToastMessage
import edu.singaporetech.inf2007.team48.project_orion.extensionFunctions.long.toUnixFormattedDate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.Date

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewAssetScreen(
    assetId: String,
    orionViewModel: OrionViewModel,
    navController: NavController,
    orionGetService: OrionApiServiceGet,
    orionPostService: OrionApiServicePost,
    xboxInputViewModel: XboxInputViewModel
) {
    val context = LocalContext.current;
    val coroutineScope = rememberCoroutineScope()
    val orionAsset by rememberSaveable { mutableStateOf<OrionAsset?>(null) }
    var isLoading by rememberSaveable { mutableStateOf(true) }
    var orionAssetList by rememberSaveable { mutableStateOf(listOf<OrionAsset>()) }

    val underServiceList = rememberSaveable { mutableStateOf(listOf<OrionRecord>()) }
    val completedServiceList = rememberSaveable { mutableStateOf(listOf<OrionRecord>()) }

    val isViewingService = rememberSaveable() { mutableStateOf(false) }

    /**
     * NOTE: ID tied to 'underServiceList'
     */
    val targetViewingServiceID = rememberSaveable() { mutableIntStateOf(-1) }
    val isCreatingService = rememberSaveable { mutableStateOf(false) }

    // Fetch the asset details when the composable enters the composition and whenever the assetId changes
    LaunchedEffect(assetId) {
        coroutineScope.launch {
            try {
                // Assuming assetId is an Int and needs to be parsed
                val parsedAssetId = assetId.toIntOrNull()
                parsedAssetId?.let {
                    val fetchedAssets = orionGetService.getAssets(
                        assetId = it,
                        assetName = null,
                        typeId = null,
                        isActive = null
                    )

                    Log.d("ViewAssetScreen.LaunchedEffect", "Fetched asset: $assetId")
                    orionAssetList = fetchedAssets.map { apiAsset ->
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

                    // Fetch related maintenance records for the asset
                    val fetchedRecords = orionGetService.getRecords(
                        serviceId = null,
                        assetId = it  // Use the parsed asset ID
                    ).map { apiRecord ->
                        OrionRecord(
                            serviceId = apiRecord.serviceId,
                            assetId = apiRecord.assetId,
                            userId = apiRecord.userId,
                            serviceDesc = apiRecord.serviceDescription,
                            dateRecorded = apiRecord.dateRecorded,
                            dateServiced = apiRecord.dateServiced
                        )
                    }

                    underServiceList.value = fetchedRecords.filter {
                        it.dateServiced == null
                    }

                    completedServiceList.value = fetchedRecords.filter {
                        it.dateServiced != null
                    }
                    isLoading = false
                } ?: Log.e("ViewAssetScreen.LaunchedEffect", "Invalid asset ID: $assetId")
            } catch (e: Exception) {
                context.showToastMessage("Failed to fetch Orion Asset!\r\nTry again or contact admin!", Toast.LENGTH_LONG)
                Log.e("ViewAssetScreen.LaunchedEffect", "Error fetching asset details: ${e.localizedMessage}", e)
                isLoading = false
            }
        }
    }

    Surface(
        color = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize()
    ) {
        Scaffold(
            floatingActionButton = {
                FloatingActionButton(onClick = {
                    isCreatingService.value = true
                }) {
                    Icon(Icons.Filled.Add, contentDescription = "Add")
                }
            },
            topBar = {
                OrionTopAppBarWithEditIcon(
                    title = orionAsset?.assetName ?: "",
                    // TODO: Dynamically set the title to the asset name
                    //idk why this part isn't showing up on screen, but the maintenance items appear

                    onEditClick = {},
                    onBackClick = { navController.navigateUp() }
                )
            },
        ) { paddingValues ->
            if (isLoading) {
                Box(modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues), contentAlignment = Alignment.Center) {
                    // TODO: Loading Indications
                }
                return@Scaffold
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Top
            )  {

                //#region Display the Orion Asset
                orionAssetList.forEach { asset ->
                    AssetInfoCard(
                        // TODO: Fetch from backend API and replace with actual Image.
                        assetImage = R.drawable.asset_image,
                        assetName = asset.assetName,
                        // TODO: Map to actual asset type name
                        assetType = "Electric Vehicle",
                        assetDesc = asset.assetDesc,
                        dateAdded = asset.dateAdded,
                        dateServiced = asset.dateLastServiced,
                        onClick = {}
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
                //#endregion

                //#region Edit Checklist Button
                Button(
                    onClick = {
                        navController.navigate(OrionScreens.EditChecklistScreen.route)
                              },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 0.dp)
                        .height(50.dp)
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Text(
                            text = "Edit Checklist",
                            modifier = Modifier.align(Alignment.Center),
                            style = MaterialTheme.typography.titleLarge
                        )
                        Icon(
                            imageVector = Icons.Filled.Edit,
                            contentDescription = "Edit",
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .padding(end = 8.dp)
                        )
                    }
                }
                //#endregion

                //#region Display Pending Maintenance Records
                if (underServiceList.value.isNotEmpty()) {
                    Text(
                        text = "Pending Maintenance Items",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(8.dp, 16.dp, 8.dp, 8.dp)
                    )
                    Divider()
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(underServiceList.value.size) { index ->
                            val currentRecord = underServiceList.value[index]
                            ServiceRecordCard(
                                record = currentRecord,
                                orionGetAPI = orionGetService,
                                coroutineScope = coroutineScope,
                                onSelected = {
                                    targetViewingServiceID.intValue = index
                                    isViewingService.value = true
                                }
                            )
                        }
                    }
                }
                //#endregion

                //#region Display finished servicing Items
                if (completedServiceList.value.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "Completed Maintenance Item",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(8.dp, 16.dp, 8.dp, 8.dp)
                    )
                    Divider()
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    ) {
                        items(completedServiceList.value.size) { index ->
                            val currentRecord = completedServiceList.value[index]
                            ServiceRecordCard(
                                record = currentRecord,
                                orionGetAPI = orionGetService,
                                coroutineScope = coroutineScope,
                                onSelected = {}
                            )
                        }
                    }
                }
                //#endregion
            }
        }
    }

    if (isCreatingService.value) {
        CreateServiceDialog(
            onCancelRequest = {
                isCreatingService.value = false
            },
            onCreateRequest = {description ->
                coroutineScope.launch {
                    try {
                        val dateRecorded = Date().time / 1000

                        val addResponse = orionPostService.addNewRecord(
                            OrionApiPostRecord.Request(
                                assetId = assetId.toInt(),
                                userId = orionViewModel.operatorId.value,
                                serviceDescription = description,
                                dateRecorded = dateRecorded
                            )
                        )

                        val newServiceRecord = OrionRecord(
                            serviceId = addResponse.serviceId,
                            assetId = assetId.toInt(),
                            userId = orionViewModel.operatorId.value,
                            serviceDesc = description,
                            dateRecorded = dateRecorded
                        )

                        // Update the state values on list to properly reflect changes on front-end
                        val newUnderServiceList = underServiceList.value.toMutableList().apply {
                            add(newServiceRecord)
                        }
                        underServiceList.value = newUnderServiceList

                        isCreatingService.value = false
                    } catch (e: Exception) {
                        context.showToastMessage(
                            "Failed to create service record!\r\nTry again or contact admin!",
                            Toast.LENGTH_LONG
                        )

                        isViewingService.value = false
                        targetViewingServiceID.intValue = -1
                    }
                }
            }
        )
    }

    if (isViewingService.value) {
        FlagServiceCompleteDialog(
            onDismissRequest = {
                isViewingService.value = false
                targetViewingServiceID.intValue = -1
            },
            onConfirmRequest = {
                val targetServiceRecord = underServiceList.value[targetViewingServiceID.intValue]

                // API Call to flag this as complete
                coroutineScope.launch {
                    try {
                        val dateServiced = Date().time / 1000

                        Log.d("sending", "$targetServiceRecord")
                        val addResponse = orionPostService.addNewRecord(
                            OrionApiPostRecord.Request(
                                assetId = targetServiceRecord.assetId,
                                userId = targetServiceRecord.userId,
                                serviceDescription = targetServiceRecord.serviceDesc,
                                dateRecorded = targetServiceRecord.dateRecorded,
                                dateServiced = dateServiced,
                                serviceId = targetServiceRecord.serviceId
                            )
                        )

                        // Instance the newly created service record with the completed flag.
                        val newServiceRecord = OrionRecord(
                            serviceId = addResponse.serviceId,
                            assetId = targetServiceRecord.assetId,
                            userId = targetServiceRecord.userId,
                            serviceDesc = targetServiceRecord.serviceDesc,
                            dateRecorded = targetServiceRecord.dateRecorded,
                            dateServiced = dateServiced
                        )

                        // Update the state values on list to properly reflect changes on front-end
                        val newUnderServiceList = underServiceList.value.toMutableList().apply {
                            removeAt(targetViewingServiceID.intValue)
                        }
                        underServiceList.value = newUnderServiceList

                        val newCompletedServiceList = completedServiceList.value.toMutableList().apply {
                            add(newServiceRecord)
                        }
                        completedServiceList.value = newCompletedServiceList

                        isViewingService.value = false
                        targetViewingServiceID.intValue = -1
                    } catch (e: Exception) {
                        context.showToastMessage(
                            "Failed to delete service record!\r\nTry again or contact admin!", Toast.LENGTH_LONG
                        )
                        Log.e("ViewAssetScreen.ConfirmServiceRequest", "Error deleting service (${targetServiceRecord.serviceId}) aa completed: ${e.localizedMessage}", e)

                        isViewingService.value = false
                        targetViewingServiceID.intValue = -1
                    }
                }
            }
        )
    }
}

@Composable
fun ServiceRecordCard(
    record: OrionRecord,
    orionGetAPI: OrionApiServiceGet,
    coroutineScope: CoroutineScope,
    onSelected: (selected: OrionRecord) -> Unit,
) {
    var isLoading by rememberSaveable { mutableStateOf(true) }
    var username by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(record) {
        coroutineScope.launch {
            try {
                //#region API Call to fetch the username based on the record
                val user = findUserByID(orionGetAPI, record.userId)
                if (user == null) {
                    Log.w("ViewAssetScreen.ServiceRecordCard", "Failed to fetch username for User ID ${record.userId}; Service ID ${record.serviceId}")
                }
                username = user?.user_name ?: "Unknown User"
                //#endregion

                isLoading = false
            } catch (e: Exception) {
                Log.e("ViewAssetScreen.ServiceRecordCard", "Error fetching record details: ${e.localizedMessage}", e)
                isLoading = false
            }
        }
    }

    if (isLoading) {
        Box(modifier = Modifier
            .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            // TODO: Loading Indications
        }
        return
    }

    val isServiced = record.dateServiced != null

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onSelected(record) },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Image(
            // TODO: Populate with actual image...
            painter = painterResource(id = R.drawable.service_image),
            contentDescription = "Asset Image",
            modifier = Modifier
                .size(140.dp, 120.dp)
                .clip(RectangleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.size(16.dp))
        Column {
            AssetInfoDescText(titleText = "Reported By", descText = username)
            AssetInfoDescText(
                titleText = "Report On",
                descText = (record.dateRecorded * 1000).toUnixFormattedDate()
            )
            Spacer(modifier = Modifier.height(2.dp))
            AssetServiceDescText(titleText = "Report Description", reportDesc = record.serviceDesc)

            // If under service, add a caption to inform user to tap to update service.
            // Otherwise, indicate service date.
            if (!isServiced) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Tap to update service status...",
                    style = MaterialTheme.typography.labelSmall
                )
            } else {
                AssetServiceDescText(
                    titleText = "Serviced On",
                    reportDesc = (record.dateRecorded * 1000).toUnixFormattedDate()
                )
            }
        }
    }
}

@Composable
fun FlagServiceCompleteDialog(
    onDismissRequest: () -> Unit,
    onConfirmRequest: () -> Unit
) {
    Dialog(onDismissRequest = onDismissRequest) {
        Card(
            modifier = Modifier.wrapContentSize().padding(3.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    // TODO: Accept image input and replace
                    painter = painterResource(id = R.drawable.service_image),
                    contentDescription = "Area of Service Image",
                    modifier = Modifier.size(254.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        onClick = { onDismissRequest() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                    ) {
                        Text(
                            "Close",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        onClick = { onConfirmRequest() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Text(
                            "Flag\nCompleted",
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}


@Composable
fun CreateServiceDialog(
    onCancelRequest: () -> Unit,
    onCreateRequest: (description: String) -> Unit
) {
    val inputDescription = rememberSaveable() {
        mutableStateOf("")
    }

    Dialog(onDismissRequest = onCancelRequest) {
        Card(
            modifier = Modifier
                .wrapContentSize()
                .padding(3.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Create New Service Record",
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    style = MaterialTheme.typography.titleMedium
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                    value = inputDescription.value,
                    onValueChange = { inputDescription.value = it },
                    placeholder = {
                        Text(
                            "Enter Service Description",
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.height(IntrinsicSize.Min)) {
                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        onClick = { onCancelRequest() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
                    ) {
                        Text(
                            "Cancel",
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(),
                        onClick = { onCreateRequest(inputDescription.value) },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4CAF50))
                    ) {
                        Text(
                            "Create",
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}