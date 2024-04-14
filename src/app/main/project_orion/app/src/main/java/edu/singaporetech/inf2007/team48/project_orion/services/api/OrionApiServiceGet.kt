package edu.singaporetech.inf2007.team48.project_orion.services.api

import android.util.Log
import edu.singaporetech.inf2007.team48.project_orion.models.api.get.OrionApiGetAsset
import edu.singaporetech.inf2007.team48.project_orion.models.api.get.OrionApiGetAssetType
import edu.singaporetech.inf2007.team48.project_orion.models.api.get.OrionApiGetChecklist
import edu.singaporetech.inf2007.team48.project_orion.models.api.get.OrionApiGetRecord
import edu.singaporetech.inf2007.team48.project_orion.models.api.get.OrionApiGetUser
import retrofit2.http.GET
import retrofit2.http.Query

interface OrionApiServiceGet {
    //only have response data class
    //specify null (in code) for optional
    @GET("/assets")
    suspend fun getAssets(
        @Query("asset_id") assetId: Int?, //nullable/optional all optional => ?
        @Query("asset_name") assetName: String?,
        @Query("type_id") typeId: Int?,
        @Query("is_active") isActive: Int?
    ): List<OrionApiGetAsset>

    @GET("/asset_types")
    suspend fun getAssetTypes(
        @Query("type_id") typeId: Int?,
        @Query("type_desc") typeDesc: String?
    ): List<OrionApiGetAssetType>

    @GET("/checklists")
    suspend fun getChecklists(
        @Query("checklist_id") checklistId: Int?,
        @Query("asset_id") assetId: Int?
    ): List<OrionApiGetChecklist>

    @GET("/records")
    suspend fun getRecords(
        @Query("service_id") serviceId: Int?,
        @Query("asset_id") assetId: Int?
    ): List<OrionApiGetRecord>

    @GET("/users")
    suspend fun getUsers(
        @Query("user_id") userId: Int?,
        @Query("user_name") userName: String?
    ): List<OrionApiGetUser>
}