package edu.singaporetech.inf2007.team48.project_orion.screens

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import edu.singaporetech.inf2007.team48.project_orion.R
import edu.singaporetech.inf2007.team48.project_orion.controllers.OrionViewModel
import edu.singaporetech.inf2007.team48.project_orion.controllers.xbox.XboxInputViewModel
import edu.singaporetech.inf2007.team48.project_orion.controllers.xbox.GamepadInputEvent
import edu.singaporetech.inf2007.team48.project_orion.controllers.welcomeScreen.WelcomeScreenViewModel
import edu.singaporetech.inf2007.team48.project_orion.OrionScreens
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WelcomeScreen(
    orionViewModel: OrionViewModel,
    xboxInputViewModel: XboxInputViewModel,
    navController: NavController,
) {
    val screenViewModel: WelcomeScreenViewModel = viewModel()
    val scope = rememberCoroutineScope()
    LaunchedEffect(true) {
        screenViewModel.reset()
    }

    // Collecting Xbox input events and handling disposal
    DisposableEffect(key1 = true) {
        val job = scope.launch {
            xboxInputViewModel.xboxEvents.collect { event ->
                when (event) {
                    is GamepadInputEvent.ButtonEvent -> {
                        Log.d("XboxInput_ButtonEvent", "Button Event: ${event.btn}, Pressed: ${event.isPressed}")
                    }
                    is GamepadInputEvent.TriggerEvent -> {
                        Log.d("XboxInput_TriggerEvent", "Trigger Event: ${event.trigger}, Value: ${event.value}")
                    }
                    is GamepadInputEvent.JoystickEvent -> {
                        Log.d("XboxInput_JoystickEvent", "Joystick Event: Axis ${event.axis}, Value: ${event.value}")
                    }
                    is GamepadInputEvent.DPadEvent -> {
                        Log.d("XboxInput_DPadEvent", "DPad Event: Axis ${event.axis}, Direction: ${event.direction}")
                    }
                }
            }
        }

        onDispose {
            job.cancel()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painter = painterResource(id = R.drawable.background_04),
                contentScale = ContentScale.FillHeight,
                alpha = 0.5f
            )

    )
    {
// Container for the entire screen
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App name or welcome text
            Text(
                text = "Welcome to ORION",
                style = MaterialTheme.typography.headlineMedium,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )

            Spacer(modifier = Modifier.height(32.dp)) // Space between text and input field

            Column {
                TextField(
                    value = screenViewModel.operatorIdInput.collectAsState().value,
                    onValueChange = { screenViewModel.setOperatorId(it) },
                    label = {
                        Text(
                            text = screenViewModel.operatorIdTextFieldLabel.collectAsState().value,
                            color = if (screenViewModel.operatorIdTextFieldError.collectAsState().value) Color.Red else Color.Unspecified
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    colors = if (screenViewModel.operatorIdTextFieldError.collectAsState().value) {
                        TextFieldDefaults.textFieldColors(
                            focusedIndicatorColor = MaterialTheme.colorScheme.error,
                            unfocusedIndicatorColor = MaterialTheme.colorScheme.error
                        )
                    } else {
                        TextFieldDefaults.textFieldColors()
                    },
                    enabled = !screenViewModel.isBusy.collectAsState().value
                )

                Spacer(modifier = Modifier.height(16.dp)) // Space between input field and button

                // Enter button
                Button(
                    onClick = {
                        screenViewModel.login { operatorName ->
                            orionViewModel.setOperatorName(operatorName)
                            orionViewModel.setOperatorId(screenViewModel.operatorIdInput.value.toInt())
                            navController.navigate(OrionScreens.SystemMenuScreen.route)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    enabled = !screenViewModel.isBusy.collectAsState().value
                ) {
                    Text(
                        text = "Login",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Spacer(modifier = Modifier.height(16.dp)) // Space between input field and button
                // Enter button
                Button(
                    onClick = {
                        navController.navigate(OrionScreens.RegisterAccountScreen.route)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
                ) {
                    Text(
                        text = "Register New Operator",
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                Spacer(modifier = Modifier.height(16.dp)) // Space between input field and button
                Text(
                    text = "Or continue as a guest ➡",
                    fontSize = 18.sp,
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .clickable {
                            screenViewModel.loginAsGuest { operatorName ->
                                orionViewModel.setOperatorName(operatorName)
                                orionViewModel.setOperatorId(0)
                                navController.navigate(OrionScreens.SystemMenuScreen.route)
                            }
                        },
                    style = TextStyle(textDecoration = TextDecoration.Underline)
                )
            }
        }
    }
}