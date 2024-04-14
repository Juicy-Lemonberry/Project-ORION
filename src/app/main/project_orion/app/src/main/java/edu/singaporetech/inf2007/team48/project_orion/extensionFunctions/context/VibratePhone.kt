package edu.singaporetech.inf2007.team48.project_orion.extensionFunctions.context

import android.content.Context
import android.os.VibrationEffect
import android.os.Vibrator

/**
 * Helper extension function to vibrate the phone, if the phone has a vibrator.
 * Does nothing if the phone does not have a vibrator.
 *
 * ### Example usage:
 * ```
 * val context = LocalContext.current
 * context.vibratePhone(500)
 * ```
 */
fun Context.vibratePhone(durationMs: Long = 500) {
    // TODO: Fix deprecation...
    val vibrator = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    if (vibrator.hasVibrator()) {
        val effect = VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE)
        vibrator.vibrate(effect)
    }
}
