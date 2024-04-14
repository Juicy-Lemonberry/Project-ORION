package edu.singaporetech.inf2007.team48.project_orion.services

import android.util.Log
import edu.singaporetech.inf2007.team48.project_orion.services.api.OrionApiServiceGet
import edu.singaporetech.inf2007.team48.project_orion.models.api.get.OrionApiGetUser

/**
 * Service function to find a singular user by ID
 */
/**
 * Finds a user by their ID using the Orion GET API service.
 *
 * This suspend function queries the API for users with a specified ID. Since user IDs are
 * expected to be unique, it logs a warning if more than one user is found with the same ID.
 * It returns the first matching user, or null if no users match.
 *
 * @param orionGetAPI The GET service of the Orion API used to fetch user data.
 * @param userID The unique identifier of the user to find.
 * @return The first user matching the given ID, or null if no such user exists.
 */
suspend fun findUserByID(orionGetAPI: OrionApiServiceGet, userID: Int): OrionApiGetUser? {
    // Calls the API to get users by ID, ignoring the userName parameter (set to null).
    val matchingUsers = orionGetAPI.getUsers(
        userId = userID, // Specifies the user ID to search for.
        userName = null // UserName is not used in this search, hence set to null.
    )

    // Checks if more than one user was found with the same ID, which should not happen for unique IDs.
    if (matchingUsers.size > 1) {
        Log.w("Services.findUserByID", "User ID $userID showed up more than once!")
    }

    // Returns the first matching user, or null if no match was found.
    return matchingUsers.firstOrNull()
}
