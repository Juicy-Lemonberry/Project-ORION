package edu.singaporetech.inf2007.team48.project_orion.models.api.post

class OrionApiPostUser {
    data class Request(
        val user_id: Int? = null,
        val user_name: String
    )

    data class Response(
        val message: String,
        val user_id: Int
    )
    //this is the reply from the server
}
