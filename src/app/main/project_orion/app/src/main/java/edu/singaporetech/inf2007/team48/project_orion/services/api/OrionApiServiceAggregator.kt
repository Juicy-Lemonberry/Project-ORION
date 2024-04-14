package edu.singaporetech.inf2007.team48.project_orion.services.api

/**
 * Aggregates different types of API services for the Orion API client.
 *
 * This class simplifies the usage of different API services by aggregating
 * GET, POST, and DELETE services into a single class. This approach allows
 * for easier management and access to various API operations within the
 * OrionApiClient.
 *
 * @property getService An instance of OrionApiServiceGet to handle GET requests.
 * @property postService An instance of OrionApiServicePost to handle POST requests.
 * @property deleteService An instance of OrionApiServiceDelete to handle DELETE requests.
 */
class OrionApiServiceAggregator(
    val getService: OrionApiServiceGet,
    val postService: OrionApiServicePost,
    val deleteService: OrionApiServiceDelete
)
