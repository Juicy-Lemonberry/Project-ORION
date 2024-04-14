package edu.singaporetech.inf2007.team48.project_orion.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.paint
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import edu.singaporetech.inf2007.team48.project_orion.R
import edu.singaporetech.inf2007.team48.project_orion.controllers.OrionViewModel
import edu.singaporetech.inf2007.team48.project_orion.controllers.xbox.XboxInputViewModel
import edu.singaporetech.inf2007.team48.project_orion.OrionScreens
import edu.singaporetech.inf2007.team48.project_orion.components.OrionTopAppBarWithTitle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SystemMenuScreen(
    orionViewModel: OrionViewModel,
    xboxInputViewModel: XboxInputViewModel,
    navController: NavController
) {
    val context = LocalContext.current
    Box(
        Modifier
            .fillMaxSize()
            .paint(
                painter = painterResource(id = R.drawable.asset_image),
                contentScale = ContentScale.FillHeight,
                alpha = 0.5f
            )
    )
    {
        Scaffold(
            topBar = {
                OrionTopAppBarWithTitle(
                    title = "Welcome ${orionViewModel.operatorName.collectAsState().value}!",
                    onProfilePictureClick = {}
                )
            },
        ) { paddingValues ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Select a system option to continue",
                    style = MaterialTheme.typography.headlineLarge,
                    textAlign = TextAlign.Center
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    SystemOptionButton(
                        icon = R.drawable.menu_icon_rov_system,
                        text = "Connected to ROV",
                        onClick = { navController.navigate(OrionScreens.RovWelcomeScreen.route) }
                    )
                    SystemOptionButton(
                        icon = R.drawable.menu_icon_checklist_system,
                        iconModifier = Modifier.padding(8.dp, 0.dp, 0.dp, 0.dp),
                        text = "Asset Checklists",
                        isDisabled = !orionViewModel.isLoggedIn(),
                        onClick = {
                            if (orionViewModel.isLoggedIn()) {
                                navController.navigate(OrionScreens.SearchAssetScreen.route)
                            } else {
                                Toast.makeText(
                                    context,
                                    "Please login to access checklist page.",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    )
                }
                Spacer(modifier = Modifier.size(64.dp))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun SystemOptionButtonPreview() {
    SystemOptionButton(
        icon = R.drawable.menu_icon_rov_system,
        text = "Connected to ROV",
        onClick = {}
    )
}

@Composable
fun SystemOptionButton(
    icon: Int,
    iconModifier: Modifier = Modifier,
    text: String,
    isDisabled: Boolean = false,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp)) // Adds rounded corners to the button
            .clickable(onClick = onClick)
            .background(MaterialTheme.colorScheme.surfaceVariant) // Use a different background color for the button
            .border(
                BorderStroke(
                    1.dp,
                    if (isDisabled) Color.Gray else MaterialTheme.colorScheme.primary
                ),
                RoundedCornerShape(12.dp)
            ) // Apply the border with rounded corners
            .padding(14.dp)
            .size(150.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = icon),
            contentDescription = null,
            colorFilter = if (isDisabled) ColorFilter.colorMatrix(ColorMatrix().apply {
                setToSaturation(
                    0f
                )
            }) else null,
            modifier = iconModifier.size(100.dp)
        )
        Spacer(modifier = Modifier.size(16.dp))
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Bold,
            color = if (isDisabled) Color.Gray else MaterialTheme.colorScheme.primary
        )
    }
}
