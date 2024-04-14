package edu.singaporetech.inf2007.team48.project_orion.extensionFunctions.context

import android.content.Context
import android.widget.Toast

/**
 * Helper extension function to make and show a toast.
 *
 * Same effect as calling:
 * ```
 * Toast.makeText(context, message, toastLength).show()
 * ```
 */
fun Context.showToastMessage(
    message: String,
    toastLength: Int = Toast.LENGTH_SHORT
) {
    Toast.makeText(this, message, toastLength).show()
}