package edu.singaporetech.inf2007.team48.project_orion.components

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.platform.LocalContext
/**
 * Locks the screen orientation of the app to a specific orientation (landscape or portrait).
 * This function should be called within the composable functions that require a fixed
 * screen orientation. It uses the current Activity context to apply the orientation lock.
 *
 * @param orientation Use ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE for landscape orientation
 *                    or ActivityInfo.SCREEN_ORIENTATION_PORTRAIT for portrait orientation.
 */
@Composable
fun LockScreenOrientation(orientation: Int) {
    val context = LocalContext.current
    DisposableEffect(orientation) {
        val activity = context.findActivity() ?: return@DisposableEffect onDispose {}
        val originalOrientation = activity.requestedOrientation
        activity.requestedOrientation = orientation
        onDispose {
            // Restore the original orientation when the composable is no longer used
            activity.requestedOrientation = originalOrientation
        }
    }
}

/**
 * Enables full-screen mode for the app, hiding the system UI like the navigation bar
 * and the status bar. This function should be used within Composable functions where
 * full-screen mode is desired. It automatically restores the system UI visibility
 * to its original state when the composable is disposed.
 */
@Composable
fun LockFullScreen() {
    val context = LocalContext.current
    DisposableEffect(Unit) {
        val activity = context.findActivity() ?: return@DisposableEffect onDispose {}
        val originalSystemUiVisibility = activity.window.decorView.systemUiVisibility

        // Configure the system UI for immersive full-screen mode.
        activity.window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        // Ensure content appears under system bars to prevent resizing.
                        or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        // Hide navigation and status bars.
                        or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        or View.SYSTEM_UI_FLAG_FULLSCREEN)

        onDispose {
            // Restore the original system UI visibility when composable is disposed.
            activity.window.decorView.systemUiVisibility = originalSystemUiVisibility
        }
    }
}

/**
 * Extension function on Context to find the Activity context associated with it.
 * Useful for getting an Activity context from a Composable function.
 *
 * @return The Activity context if found, null otherwise.
 */
fun Context.findActivity(): Activity? = when (this) {
    is Activity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}