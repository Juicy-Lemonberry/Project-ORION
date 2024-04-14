package edu.singaporetech.inf2007.team48.project_orion.controllers

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import edu.singaporetech.inf2007.team48.project_orion.consts.OrionRovRequiredPermissions
import edu.singaporetech.inf2007.team48.project_orion.models.api.get.OrionApiGetChecklist
import edu.singaporetech.inf2007.team48.project_orion.models.OrionAsset
import edu.singaporetech.inf2007.team48.project_orion.models.OrionAssetType
import edu.singaporetech.inf2007.team48.project_orion.models.OrionChecklist
import edu.singaporetech.inf2007.team48.project_orion.models.OrionQuickReferenceChecklist
import edu.singaporetech.inf2007.team48.project_orion.models.OrionQuickReferenceChecklistItems
import edu.singaporetech.inf2007.team48.project_orion.models.OrionRecord
import edu.singaporetech.inf2007.team48.project_orion.models.OrionUser
import edu.singaporetech.inf2007.team48.project_orion.repository.OrionAssetRepository
import edu.singaporetech.inf2007.team48.project_orion.repository.OrionAssetTypeRepository
import edu.singaporetech.inf2007.team48.project_orion.repository.OrionChecklistRepository
import edu.singaporetech.inf2007.team48.project_orion.repository.OrionPreferenceRepository
import edu.singaporetech.inf2007.team48.project_orion.repository.OrionRecordRepository
import edu.singaporetech.inf2007.team48.project_orion.repository.OrionUserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


@OptIn(ExperimentalPermissionsApi::class)
class OrionViewModel(
    private val assetRepository: OrionAssetRepository,
    private val assetTypeRepository: OrionAssetTypeRepository,
    private val checklistRepository: OrionChecklistRepository,
    private val recordRepository: OrionRecordRepository,
    private val userRepository: OrionUserRepository,
    private val preferenceRepository: OrionPreferenceRepository
) : ViewModel() {
    private val _allAssets = assetRepository.getAllAssets()
    private val _allAssetTypes = assetTypeRepository.getAllAssetTypes()
    private val _allChecklists = checklistRepository.getAllChecklists()
    private val _allRecords = recordRepository.getAllRecords()
    private val _allUsers = userRepository.getAllUsers()

    val requiredRovPermissions = OrionRovRequiredPermissions.permissions

    var hasNavigatedAwayAfterScanning by mutableStateOf(false)
        private set

    fun onNavigatedAwayAfterScanning() {
        hasNavigatedAwayAfterScanning = true
    }

    fun resetNavigatedAwayAfterScanning() {
        hasNavigatedAwayAfterScanning = false
    }

    private val _appOperatorName = MutableStateFlow("Guest")
    val operatorName: StateFlow<String> = _appOperatorName.asStateFlow()
    fun setOperatorName(name: String) {
        _appOperatorName.value = name
    }

    private val _appOperatorId =
        MutableStateFlow(0) // 0 is the default value for guest, in which the user is not logged in
    val operatorId: StateFlow<Int> = _appOperatorId.asStateFlow()
    fun setOperatorId(id: Int) {
        _appOperatorId.value = id
    }

    private val _appOperatorLoggedIn = MutableStateFlow(false)
    val operatorLoggedIn: StateFlow<Boolean> = _appOperatorLoggedIn.asStateFlow()
    fun setOperatorLoggedIn(loggedIn: Boolean) {
        _appOperatorLoggedIn.value = loggedIn
    }


    private val _camQrCodeScannedString = MutableStateFlow("")
    val camQrCodeScannedString: StateFlow<String> = _camQrCodeScannedString.asStateFlow()
    fun setCamQrCodeScannedString(qrCode: String) {
        _camQrCodeScannedString.value = qrCode
        if (qrCode.isNotEmpty() && qrCode.startsWith("ORION:") && qrCode.length == 23) {
            // Trims away the "ORION:" prefix, and sets the remaining characters as the rovBtMacAddress
            setRovBtMacAddress(qrCode.substring(6))
        }
    }

    private val _rovBtMacAddress = MutableStateFlow("")
    val rovBtMacAddress: StateFlow<String> = _rovBtMacAddress.asStateFlow()
    fun setRovBtMacAddress(macAddress: String) {
        _rovBtMacAddress.value = macAddress
    }

    private val _rovWifiAssignedIp = MutableStateFlow("")
    val rovWifiAssignedIp: StateFlow<String> = _rovWifiAssignedIp.asStateFlow()
    fun setRovWifiAssignedIp(ip: String) {
        _rovWifiAssignedIp.value = ip
    }

    private val _rovWifiConnectedSsid = MutableStateFlow("")
    val rovWifiConnectedSsid: StateFlow<String> = _rovWifiConnectedSsid.asStateFlow()
    fun setRovWifiConnectedSsid(ssid: String) {
        _rovWifiConnectedSsid.value = ssid
    }

    private val _appHotspotSsid = MutableStateFlow("")
    val appHotspotSsid: StateFlow<String> = _appHotspotSsid.asStateFlow()
    fun setAppHotspotSsid(ssid: String) {
        _appHotspotSsid.value = ssid
    }

    private val _appHotspotPassword = MutableStateFlow("")
    val appHotspotPassword: StateFlow<String> = _appHotspotPassword.asStateFlow()
    fun setAppHotspotPassword(password: String) {
        _appHotspotPassword.value = password
    }

    private val _appBluetoothIsConnectingToRov = MutableStateFlow(false)
    val appBluetoothIsConnectingToRov: StateFlow<Boolean> =
        _appBluetoothIsConnectingToRov.asStateFlow()

    fun setAppBluetoothIsConnectingToRov(isConnecting: Boolean) {
        _appBluetoothIsConnectingToRov.value = isConnecting
    }

    private val _appBluetoothIsConnectedToRov = MutableStateFlow(false)
    val appBluetoothIsConnectedToRov: StateFlow<Boolean> =
        _appBluetoothIsConnectedToRov.asStateFlow()

    fun setAppBluetoothIsConnectedToRov(isConnected: Boolean) {
        _appBluetoothIsConnectedToRov.value = isConnected
    }

    private val _rovWifiIsConnectingToPhone = MutableStateFlow(false)
    val rovWifiIsConnectingToPhone: StateFlow<Boolean> = _rovWifiIsConnectingToPhone.asStateFlow()
    fun setRovWifiIsConnectingToPhone(isConnecting: Boolean) {
        _rovWifiIsConnectingToPhone.value = isConnecting
    }

    private val _rovWifiIsConnectedToPhone = MutableStateFlow(false)
    val rovWifiIsConnectedToPhone: StateFlow<Boolean> = _rovWifiIsConnectedToPhone.asStateFlow()
    fun setRovWifiIsConnectedToPhone(isConnected: Boolean) {
        _rovWifiIsConnectedToPhone.value = isConnected
    }

    private val _rovViewportActive = MutableStateFlow(false)
    val rovViewportActive: StateFlow<Boolean> = _rovViewportActive.asStateFlow()
    fun setRovViewportActive(isActive: Boolean) {
        _rovViewportActive.value = isActive
    }

    //saving the current assetid to be implemented on different other screens
    private val _currentSearchedAssetId = MutableStateFlow<Int>(-1)
    val currentSearchedAssetId: StateFlow<Int> = _currentSearchedAssetId

    fun setSearchedAssetId(id: Int) {
        _currentSearchedAssetId.value = id;
        Log.d("OrionViewModel", "Asset ID set to: $id")
    }

    // This here quickReferenceChecklist is used in the RovViewPortScreen to display the checklist
    // During live inspection.
    private val _quickReferenceChecklist =
        MutableStateFlow<OrionQuickReferenceChecklist>(OrionQuickReferenceChecklist())
    val quickReferenceChecklist: StateFlow<OrionQuickReferenceChecklist> =
        _quickReferenceChecklist.asStateFlow()

    fun loadQuickReferenceChecklistFromApi(orionApiGetChecklist: List<OrionApiGetChecklist>, assetName: String, assetType: String) {
        // Clears the current list of quickReferenceChecklist
        _quickReferenceChecklist.value.asset_checklist = emptyList()
        _quickReferenceChecklist.value.asset_id = orionApiGetChecklist.first().asset_id
        _quickReferenceChecklist.value.asset_name = assetName
        _quickReferenceChecklist.value.asset_type = assetType
        // Loops through the list of OrionApiGetChecklist and converts them to OrionChecklist
        orionApiGetChecklist.forEach { apiResult ->
            _quickReferenceChecklist.value.asset_checklist += OrionQuickReferenceChecklistItems(
                checklist_id = apiResult.checklist_id,
                asset_id = apiResult.asset_id,
                checklist_title = apiResult.checklist_title,
                checklist_desc = apiResult.checklist_desc,
                checklist_completed = false
            )
        }
    }

    fun updateQuickReferenceChecklistItem(item: OrionQuickReferenceChecklistItems) {
        val updatedList = _quickReferenceChecklist.value.asset_checklist.toMutableList()
        updatedList.forEach {
            if (it.checklist_id == item.checklist_id) {
                it.checklist_completed = item.checklist_completed
            }
        }
        _quickReferenceChecklist.value.asset_checklist = updatedList
    }

    fun resetQuickReferenceChecklistCheckboxes() {
        val updatedList = _quickReferenceChecklist.value.asset_checklist.toMutableList()
        updatedList.forEach { it.checklist_completed = false }
        _quickReferenceChecklist.value.asset_checklist = updatedList
    }

    /*TODO: Continuously add in additional variables that are needed across the screens*/


    init {
        viewModelScope.launch {
            assetRepository.getAllAssets().collect { asset ->
            }
        }
        viewModelScope.launch {
            assetTypeRepository.getAllAssetTypes().collect { assetType ->
            }
        }
        viewModelScope.launch {
            checklistRepository.getAllChecklists().collect { checklist ->
            }
        }
        viewModelScope.launch {
            recordRepository.getAllRecords().collect { record ->
            }
        }
        viewModelScope.launch {
            userRepository.getAllUsers().collect { user ->
            }
        }
    }

    fun isLoggedIn(): Boolean {
        return _appOperatorId.value != 0
    }


    // Section 1: 'get all' functions
    fun getAllAssets(): Flow<List<OrionAsset>> {
        return _allAssets
    }

    fun getAllAssetTypes(): Flow<List<OrionAssetType>> {
        return _allAssetTypes
    }

    fun getAllChecklists(): Flow<List<OrionChecklist>> {
        return _allChecklists
    }

    fun getAllRecords(): Flow<List<OrionRecord>> {
        return _allRecords
    }

    fun getAllRecordsThatHasServiceDate(): Flow<List<OrionRecord>> {
        return recordRepository.getAllRecordsThatHasServiceDate()
    }

    fun getAllRecordsThatHasNoServiceDate(): Flow<List<OrionRecord>> {
        return recordRepository.getAllRecordsThatHasNoServiceDate()
    }

    fun getAllUsers(): Flow<List<OrionUser>> {
        return _allUsers
    }

    // Section 1.1 get by primary key

    fun getAssetById(assetId: Int): Flow<OrionAsset> {
        return assetRepository.getAssetById(assetId)
    }

    fun getAssetTypeById(typeId: Int): Flow<OrionAssetType> {
        return assetTypeRepository.getAssetTypeById(typeId)
    }

    fun getChecklistById(checklistId: Int): Flow<OrionChecklist> {
        return checklistRepository.getChecklistById(checklistId)
    }

    fun getRecordById(serviceId: Int): Flow<OrionRecord> {
        return recordRepository.getRecordById(serviceId)
    }

    fun getUserById(userId: Int): Flow<OrionUser> {
        return userRepository.getUserById(userId)
    }

    // Section 1.2 get by foreign key
    fun getAllAssetsByType(typeId: Int): Flow<List<OrionAsset>> {
        return assetRepository.getAllAssetsByType(typeId)
    }

    fun getAllAssetsWithServiceStatus(inService: Boolean): Flow<List<OrionAsset>> {
        return assetRepository.getAllAssetsWithServiceStatus(inService)
    }

    fun getAllChecklistsByAssetId(assetId: Int): Flow<List<OrionChecklist>> {
        return checklistRepository.getAllChecklistsByAssetId(assetId)
    }

    fun getAllRecordsByAssetId(assetId: Int): Flow<List<OrionRecord>> {
        return recordRepository.getAllRecordsByAssetId(assetId)
    }

    fun getAllRecordsByUserId(userId: Int): Flow<List<OrionRecord>> {
        return recordRepository.getAllRecordsByUserId(userId)
    }


    // Section 2: 'insert' functions

    fun insertAsset(orionAsset: OrionAsset) {
        viewModelScope.launch {
            assetRepository.insertAsset(orionAsset)
        }
    }

    fun insertAssetType(orionAssetType: OrionAssetType) {
        viewModelScope.launch {
            assetTypeRepository.insertAssetType(orionAssetType)
        }
    }

    fun insertChecklist(orionChecklist: OrionChecklist) {
        viewModelScope.launch {
            checklistRepository.insertChecklist(orionChecklist)
        }
    }

    fun insertRecord(orionRecord: OrionRecord) {
        viewModelScope.launch {
            recordRepository.insertRecord(orionRecord)
        }
    }

    fun insertUser(orionUser: OrionUser) {
        viewModelScope.launch {
            userRepository.insertUser(orionUser)
        }
    }

    // Section 3: 'update' functions

    fun updateAsset(newAsset: OrionAsset) {
        viewModelScope.launch {
            assetRepository.updateAsset(
                newAsset = newAsset
            )
        }
    }

    fun updateAssetType(newAssetType: OrionAssetType) {
        viewModelScope.launch {
            assetTypeRepository.updateAssetType(
                newAssetType = newAssetType
            )
        }
    }

    fun updateChecklist(newChecklist: OrionChecklist) {
        viewModelScope.launch {
            checklistRepository.updateChecklist(
                newChecklist = newChecklist
            )
        }
    }

    fun updateRecord(newRecord: OrionRecord) {
        viewModelScope.launch {
            recordRepository.updateRecord(
                newRecord = newRecord
            )
        }
    }

    fun updateUser(newUser: OrionUser) {
        viewModelScope.launch {
            userRepository.updateUser(
                newUser = newUser
            )
        }
    }

    // Section 4: 'delete' functions

    fun deleteAsset(assetId: Int) {
        viewModelScope.launch {
            assetRepository.deleteAsset(assetId)
        }
    }

    fun deleteAssetType(typeId: Int) {
        viewModelScope.launch {
            assetTypeRepository.deleteAssetType(typeId)
        }
    }

    fun deleteChecklist(checklistId: Int) {
        viewModelScope.launch {
            checklistRepository.deleteChecklist(checklistId)
        }
    }

    fun deleteRecord(serviceId: Int) {
        viewModelScope.launch {
            recordRepository.deleteRecord(serviceId)
        }
    }

    fun deleteUser(userId: Int) {
        viewModelScope.launch {
            userRepository.deleteUser(userId)
        }
    }

}