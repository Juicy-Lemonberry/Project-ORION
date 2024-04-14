package edu.singaporetech.inf2007.team48.project_orion.controllers.xbox

import android.util.Log
import android.view.InputEvent
import android.view.KeyEvent
import android.view.MotionEvent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class XboxInputViewModel : ViewModel() {

    // SharedFlow to emit input events to the UI or other parts of the app.
    private val _xboxEvents = MutableSharedFlow<GamepadInputEvent>(extraBufferCapacity = 1)
    val xboxEvents = _xboxEvents.asSharedFlow()

    // Variables to store the last known state for each input.
    // So we don't keep spam emitting the same event for inputs that were not touched.
    // :WayTooDank:
    private val currentMotionValues = mutableMapOf<Int, Float>()
    private val lastMotionValues = mutableMapOf<Int, Float>()


    // Since we want MotionEvents to be polled every fixed interval,
    // while KeyEvents are emitted immediately, we split the handling of these events
    // into two separate functions.

    /**
     * Handles input events from the controller.
     * @param inputEvent The input event to handle.
     * @see InputEvent
     *
     */
    fun handleControllerInput(inputEvent: InputEvent) {
        Log.d("XboxInputViewModel", "Handling input event: $inputEvent")
        when (inputEvent) {
            is MotionEvent -> handleMotionInputEvent(inputEvent)
            is KeyEvent -> handleKeyInputEvent(inputEvent)
        }
    }


    // Coroutine to poll the current state of the controller every 10ms.
    init {
        viewModelScope.launch(Dispatchers.Default) {
            while (isActive) { // Ensure coroutine is still active
                currentMotionValues.forEach { (axis, value) ->
                    val lastInputValue = lastMotionValues[axis]
                    if (lastInputValue == null || lastInputValue != value) {
                        emitEvent(convertToGamepadInputEvent(axis, value))
                        lastMotionValues[axis] = value
                    }
                }
                delay(10) // Check every 10ms
            }
        }
    }

    /**
     * Handles MotionEvent input events.
     * @param inputEvent The MotionEvent to handle.
     * @see MotionEvent
     *
     */

    private fun handleMotionInputEvent(inputEvent: MotionEvent) {
        // Update Left Joystick
        currentMotionValues[MotionEvent.AXIS_X] = inputEvent.getAxisValue(MotionEvent.AXIS_X)
        currentMotionValues[MotionEvent.AXIS_Y] = inputEvent.getAxisValue(MotionEvent.AXIS_Y)

        // Update Right Joystick
        currentMotionValues[MotionEvent.AXIS_Z] = inputEvent.getAxisValue(MotionEvent.AXIS_Z)
        currentMotionValues[MotionEvent.AXIS_RZ] = inputEvent.getAxisValue(MotionEvent.AXIS_RZ)

        // Update Triggers
        currentMotionValues[MotionEvent.AXIS_LTRIGGER] = inputEvent.getAxisValue(MotionEvent.AXIS_LTRIGGER)
        currentMotionValues[MotionEvent.AXIS_RTRIGGER] = inputEvent.getAxisValue(MotionEvent.AXIS_RTRIGGER)

        // Update D-Pad
        currentMotionValues[MotionEvent.AXIS_HAT_X] = inputEvent.getAxisValue(MotionEvent.AXIS_HAT_X)
        currentMotionValues[MotionEvent.AXIS_HAT_Y] = inputEvent.getAxisValue(MotionEvent.AXIS_HAT_Y)
    }

    /**
     * Handles KeyEvent input events.
     * @param inputEvent The KeyEvent to handle.
     * @see KeyEvent
     *
     */
    private fun handleKeyInputEvent(inputEvent: KeyEvent) {
        emitEvent(
            GamepadInputEvent.ButtonEvent(
                inputEvent.keyCode,
                inputEvent.action == KeyEvent.ACTION_DOWN
            )
        )
    }

    /**
     * Converts a MotionEvent axis and value to a GamepadInputEvent.
     * @param axis The MotionEvent axis.
     * @param value The MotionEvent value.
     * @return The corresponding GamepadInputEvent.
     * @throws IllegalArgumentException if the axis is invalid.
     * @see GamepadInputEvent
     * @see MotionEvent
     *
     */
    private fun convertToGamepadInputEvent(axis: Int, value: Float): GamepadInputEvent {
        return when (axis) {
            MotionEvent.AXIS_X, MotionEvent.AXIS_Y, MotionEvent.AXIS_Z, MotionEvent.AXIS_RZ -> {
                GamepadInputEvent.JoystickEvent(axis, value)
            }
            MotionEvent.AXIS_LTRIGGER, MotionEvent.AXIS_RTRIGGER -> {
                GamepadInputEvent.TriggerEvent(axis, value)
            }
            MotionEvent.AXIS_HAT_X, MotionEvent.AXIS_HAT_Y -> {
                GamepadInputEvent.DPadEvent(axis, value.toInt())
            }
            else -> throw IllegalArgumentException("Invalid axis: $axis")
        }
    }

    /**
     * Emits a GamepadInputEvent to the shared flow.
     * @param event The GamepadInputEvent to emit.
     * @see GamepadInputEvent
     *
     *
     */
    private fun emitEvent(event: GamepadInputEvent) {
        viewModelScope.launch(Dispatchers.Default) {
            _xboxEvents.emit(event)
        }
    }
}
