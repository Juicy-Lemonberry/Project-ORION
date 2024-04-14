package edu.singaporetech.inf2007.team48.project_orion.services.api

import edu.singaporetech.inf2007.team48.project_orion.models.api.delete.OrionApiDelete
import retrofit2.http.DELETE
import retrofit2.http.Path
import retrofit2.Response

interface OrionApiServiceDelete {
    @DELETE("/assets/{asset_id}")
    suspend fun deleteAsset(
        @Path("asset_id") assetId: Int
    ): Response<OrionApiDelete> // Use OrionApiDelete for the response body

    @DELETE("/checklists/{checklist_id}")
    suspend fun deleteChecklist(
        @Path("checklist_id") checklistId: Int
    ): Response<OrionApiDelete>

    @DELETE("/records/{service_id}")
    suspend fun deleteRecord(
        @Path("service_id") serviceId: Int
    ): Response<OrionApiDelete>
}
