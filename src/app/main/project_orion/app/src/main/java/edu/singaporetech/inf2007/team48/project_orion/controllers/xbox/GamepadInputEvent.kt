package edu.singaporetech.inf2007.team48.project_orion.controllers.xbox

sealed class GamepadInputEvent {
    data class ButtonEvent(val btn: Int, val isPressed: Boolean) : GamepadInputEvent()
    data class TriggerEvent(val trigger: Int, val value: Float) : GamepadInputEvent()
    data class JoystickEvent(val axis: Int, val value: Float) : GamepadInputEvent()
    data class DPadEvent(val axis: Int, val direction: Int) : GamepadInputEvent()
}

