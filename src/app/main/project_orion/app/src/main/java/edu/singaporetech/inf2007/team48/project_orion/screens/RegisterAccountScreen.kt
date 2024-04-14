package edu.singaporetech.inf2007.team48.project_orion.screens

import android.content.pm.ActivityInfo
import androidx.compose.foundation.isSystemInDarkTheme
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import edu.singaporetech.inf2007.team48.project_orion.R
import edu.singaporetech.inf2007.team48.project_orion.controllers.OrionViewModel
import edu.singaporetech.inf2007.team48.project_orion.controllers.xbox.XboxInputViewModel
import edu.singaporetech.inf2007.team48.project_orion.controllers.register.RegisterAccountScreenViewModel
import edu.singaporetech.inf2007.team48.project_orion.components.LockScreenOrientation
import edu.singaporetech.inf2007.team48.project_orion.components.OrionTopAppBar


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterAccountScreen(
    orionViewModel: OrionViewModel,
    xboxInputViewModel: XboxInputViewModel,
    navController: NavController) {

    val context = LocalContext.current
    val screenViewModel: RegisterAccountScreenViewModel = viewModel()
    LaunchedEffect(true) {
        screenViewModel.reset()
    }
    LockScreenOrientation(orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .paint(
                painter = painterResource(id = R.drawable.background_05),
                contentScale = ContentScale.FillHeight,
                alpha = 0.3f
            )

    ) {
        Scaffold(
            topBar = {
                OrionTopAppBar(
                    title = "Register Account",
                    onBackClick = { navController.popBackStack() },
                    colours = TopAppBarDefaults.topAppBarColors(
                        containerColor = if (isSystemInDarkTheme()) {
                            Color.Black.copy(alpha = 0.3f)
                        } else {
                            Color.White.copy(alpha = 0.3f)
                        }
                    )
                )

            },
            containerColor = Color.Transparent
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            )
            {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = screenViewModel.titleText.collectAsState().value,
                            style = MaterialTheme.typography.headlineMedium,
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            textAlign = TextAlign.Center,
                            color = if (screenViewModel.registrationSuccess.collectAsState().value) {
                                Color.Green
                            } else {
                                MaterialTheme.colorScheme.primary
                            }
                        )
                        Spacer(modifier = Modifier.height(32.dp)) // Space between text and input field
                        Text(
                            text = screenViewModel.subtitleText.collectAsState().value,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.align(Alignment.CenterHorizontally),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp)) // Space between text and input field

                    }
                    Column {
                        TextField(
                            value = screenViewModel.operatorId.collectAsState().value,
                            onValueChange = { screenViewModel.setOperatorId(it) },
                            label = {
                                Text(
                                    text = screenViewModel.operatorIdTextFieldLabel.collectAsState().value,
                                    color = if (screenViewModel.isOperatorIdTextFieldError.collectAsState().value) Color.Red else Color.Unspecified
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            enabled = !screenViewModel.isBusy.collectAsState().value
                                    && !screenViewModel.registrationSuccess.collectAsState().value,
                            colors = if (screenViewModel.isOperatorIdTextFieldError.collectAsState().value) {
                                TextFieldDefaults.textFieldColors(
                                    focusedIndicatorColor = MaterialTheme.colorScheme.error,
                                    unfocusedIndicatorColor = MaterialTheme.colorScheme.error,
                                )
                            } else {
                                TextFieldDefaults.textFieldColors()
                            }
                        )
                        Spacer(modifier = Modifier.padding(8.dp))
                        TextField(
                            value = screenViewModel.operatorName.collectAsState().value,
                            onValueChange = { screenViewModel.setOperatorName(it) },
                            label = {
                                Text(
                                    screenViewModel.operatorNameTextFieldLabel.collectAsState().value,
                                    color = if (screenViewModel.isOperatorNameTextFieldError.collectAsState().value) Color.Red else Color.Unspecified
                                )
                            },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            enabled = !screenViewModel.isBusy.collectAsState().value
                                    && !screenViewModel.registrationSuccess.collectAsState().value,
                            colors = if (screenViewModel.isOperatorNameTextFieldError.collectAsState().value) {
                                TextFieldDefaults.textFieldColors(
                                    focusedIndicatorColor = MaterialTheme.colorScheme.error,
                                    unfocusedIndicatorColor = MaterialTheme.colorScheme.error
                                )
                            } else {
                                TextFieldDefaults.textFieldColors()
                            }
                        )
                        Spacer(modifier = Modifier.padding(8.dp))
                    }
                    Button(
                        onClick = {
                            screenViewModel.registerAccountButtonClicked(
                                onRegisterAccountButtonClickedAfterSuccessfulCreation = {
                                    navController.popBackStack()
                                }
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                        enabled = !screenViewModel.isBusy.collectAsState().value
                    ) {
                        Text(
                            text = screenViewModel.createAccountButtonLabel.collectAsState().value,
                            fontSize = 18.sp,
                            color = if (screenViewModel.registrationSuccess.collectAsState().value) {
                                Color.Green
                            } else {
                                MaterialTheme.colorScheme.primary
                            }
                        )
                    }

                }
            }
        }
    }
}