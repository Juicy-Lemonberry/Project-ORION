package edu.singaporetech.inf2007.team48.project_orion.services.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


// Defines a singleton object named OrionApiClient.
object OrionApiClient {
    // Base URL of the Orion API server. Note: This URL is only for demonstration purposes.
    private val BASE_URL = "http://inf2007-team48.hopto.org:48200"

    // Initializes the Retrofit client with the base URL and a Gson converter for JSON serialization/deserialization.
    private val retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL) // Sets the base URL for the HTTP requests.
        .addConverterFactory(GsonConverterFactory.create()) // Adds a converter factory for serialization and deserialization of objects.
        .build() // Creates the Retrofit instance.

    // Aggregates different API services (GET, POST, DELETE) into a single API service aggregator for easier access.
    val orionApi = OrionApiServiceAggregator(
        getService = retrofit.create(OrionApiServiceGet::class.java), // Creates an implementation of the GET API service.
        postService = retrofit.create(OrionApiServicePost::class.java), // Creates an implementation of the POST API service.
        deleteService = retrofit.create(OrionApiServiceDelete::class.java) // Creates an implementation of the DELETE API service.
    )
}
