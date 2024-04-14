package edu.singaporetech.inf2007.team48.project_orion.extensionFunctions.long

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Treats this value as a Unix Format, and convert it to a human-readable Formatted date in String.
 *
 * @return Human-readable, formatted date in represented in `dd MMM yyyy`
 */
fun Long.toUnixFormattedDate(): String {
    // Create a Date object from the Unix time (which is in milliseconds)
    val date = Date(this)

    // Create a SimpleDateFormat instance with your desired format
    val format = SimpleDateFormat("dd MMM yyyy", Locale.ENGLISH)

    // Use the SimpleDateFormat instance to format the Date object
    return format.format(date)
}