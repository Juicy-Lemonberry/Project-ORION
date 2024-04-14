package edu.singaporetech.inf2007.team48.project_orion.services.api

import edu.singaporetech.inf2007.team48.project_orion.models.api.post.OrionApiPostAsset
import edu.singaporetech.inf2007.team48.project_orion.models.api.post.OrionApiPostAssetType
import edu.singaporetech.inf2007.team48.project_orion.models.api.post.OrionApiPostChecklist
import edu.singaporetech.inf2007.team48.project_orion.models.api.post.OrionApiPostRecord
import edu.singaporetech.inf2007.team48.project_orion.models.api.post.OrionApiPostUser
import retrofit2.http.Body
import retrofit2.http.POST

interface OrionApiServicePost {
    //have both request and response data classes
    @POST("/assets")
    suspend fun addNewAsset(
        @Body newAssetRequest: OrionApiPostAsset.Request
    ): OrionApiPostAsset.Response

    @POST("/asset_types")
    suspend fun addNewAssetType(
        @Body newAssetTypeRequest: OrionApiPostAssetType.Request
    ): OrionApiPostAssetType.Response

    @POST("/checklists")
    suspend fun addNewChecklist(
        @Body newChecklistRequest: OrionApiPostChecklist.Request
    ): OrionApiPostChecklist.Response

    @POST("/records")
    suspend fun addNewRecord(
        @Body newRecordRequest: OrionApiPostRecord.Request
    ): OrionApiPostRecord.Response

    @POST("/users")
    suspend fun addNewUser(
        @Body newUserRequest: OrionApiPostUser.Request
    ): OrionApiPostUser.Response
}
