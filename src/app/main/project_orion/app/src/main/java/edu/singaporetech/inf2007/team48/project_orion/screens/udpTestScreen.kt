package edu.singaporetech.inf2007.team48.project_orion.screens

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.navigation.NavController
import edu.singaporetech.inf2007.team48.project_orion.controllers.OrionViewModel
import edu.singaporetech.inf2007.team48.project_orion.controllers.xbox.GamepadInputEvent
import edu.singaporetech.inf2007.team48.project_orion.controllers.xbox.XboxInputViewModel
import edu.singaporetech.inf2007.team48.project_orion.controllers.udp.UdpViewModel
import kotlinx.coroutines.launch

@Composable
fun UdpTestScreen(
    orionViewModel: OrionViewModel,
    xboxInputViewModel: XboxInputViewModel,
    udpViewModel: UdpViewModel,
    navController: NavController
) {
    val scope = rememberCoroutineScope()
    DisposableEffect(key1 = true) {
        udpViewModel.connect("192.168.10.139", 5000, 5000)

        val job = scope.launch {
            xboxInputViewModel.xboxEvents.collect { event ->
                val message = when (event) {
                    is GamepadInputEvent.ButtonEvent -> "B:${event.btn},V:${if (event.isPressed) 1 else 0}"
                    // Filter out the UP/DOWN axis (1) events on the left Joystick as it serves no purpose
                    is GamepadInputEvent.JoystickEvent -> if (event.axis == 1) "" else "J:${event.axis},V:${event.value}"
                    is GamepadInputEvent.TriggerEvent -> "T:${event.trigger},V:${event.value}"
                    is GamepadInputEvent.DPadEvent -> "D:${event.axis},V:${event.direction}"
                    else -> "" // Ignore other event types
                }
                if (message.isNotEmpty()) {
                    Log.d("UDP", "Sending: $message")
                    udpViewModel.sendUDPData(message)
                }
            }
        }


        onDispose {
            udpViewModel.disconnect()
            job.cancel()
        }
    }
}