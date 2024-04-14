package edu.singaporetech.inf2007.team48.project_orion.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable

// Opt-in annotation for using experimental Material3 API features
@OptIn(ExperimentalMaterial3Api::class)
// Composable function to create a TopAppBar with an edit icon
/**
 * Constructs a top app bar with a title, a back navigation icon, and an edit action icon.
 *
 * @param title The text title displayed in the top app bar.
 * @param scrollBehavior Defines the behavior of the top app bar when scrolling.
 * @param colours Custom colors for the top app bar appearance.
 * @param onEditClick Callback function to be invoked when the edit icon is clicked.
 * @param onBackClick Callback function to be invoked when the back icon is clicked.
 */
@Composable
fun OrionTopAppBarWithEditIcon(
    title: String,
    // Provides a default pinned scroll behavior if not specified
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
    // Provides default colors for the top app bar if not specified
    colours : TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
    onEditClick: () -> Unit,
    onBackClick: () -> Unit
) {
    // Constructs the TopAppBar composable
    TopAppBar(
        // Sets the title of the TopAppBar
        title = { Text(text = title) },
        // Defines the navigation icon (back button) and its action
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back"
                )
            }
        },
        // Defines the actions to be included in the TopAppBar (edit button)
        actions = {
            IconButton(onClick = onEditClick) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit"
                )
            }
        },
        // Applies the custom colors passed to the function
        colors = colours
    )
}
