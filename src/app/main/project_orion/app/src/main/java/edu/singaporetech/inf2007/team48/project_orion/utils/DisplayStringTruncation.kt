package edu.singaporetech.inf2007.team48.project_orion.utils

/**
 * Truncation method used for strings that are to be displayed onto views.
 *
 * If the string is more than the limit, it will truncate to the limit,
 * replacing the last 3 characters with '...'
 */
fun displayStringTruncation(string: String, limit: Int = 20): String {
    return if (string.length > limit) string.substring(0, limit - 3) + "..." else string
}