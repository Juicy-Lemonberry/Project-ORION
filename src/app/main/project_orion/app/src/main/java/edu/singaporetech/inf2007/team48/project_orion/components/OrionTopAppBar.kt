package edu.singaporetech.inf2007.team48.project_orion.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable

/**
 * A composable function that creates a top app bar with a title and a back button.
 * This function is designed to be used across the app for consistent navigation and
 * styling. It uses the `ExperimentalMaterial3Api` to access the latest Material 3 top app bar
 * components and features. The function allows for customization of the scroll behavior
 * and colors, with sensible defaults provided for both.
 *
 * @param title The title to be displayed in the top app bar.
 * @param scrollBehavior Controls how the top app bar responds to scroll events. By default,
 *                       it uses `TopAppBarDefaults.pinnedScrollBehavior()` which means the top app bar
 *                       remains pinned at the top without any scroll effect. This parameter allows
 *                       for customization, such as hiding the top app bar on scroll.
 * @param colours Defines the color palette of the top app bar, including background and content colors.
 *                By default, it uses `TopAppBarDefaults.topAppBarColors()` which provides default
 *                colors based on the overall theme.
 * @param onBackClick A lambda function that is executed when the back button is clicked. This allows
 *                    the caller to specify custom back navigation logic, such as popping the back stack.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrionTopAppBar(
    title: String,
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
    colours: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
    onBackClick: () -> Unit
) {
    TopAppBar(
        title = { Text(text = title) }, // Sets the title of the top app bar.
        navigationIcon = {
            // Provides a back button on the top app bar. Executes `onBackClick` when pressed.
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack, // Uses the default back arrow icon.
                    contentDescription = "Back" // Accessibility description for the icon.
                )
            }
        },
        colors = colours // Applies the provided color palette to the top app bar.
    )
}