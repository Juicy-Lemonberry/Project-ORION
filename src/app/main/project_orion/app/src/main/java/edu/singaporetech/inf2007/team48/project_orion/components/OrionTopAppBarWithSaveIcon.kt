package edu.singaporetech.inf2007.team48.project_orion.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Done
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
// Composable function to create a TopAppBar with a save icon
/**
 * Constructs a top app bar with a title, a back navigation icon. It is designed to
 * potentially include a save action icon, though the related code is commented out.
 *
 * @param title The text title displayed in the top app bar.
 * @param scrollBehavior Defines the behavior of the top app bar when scrolling.
 * @param colours Custom colors for the top app bar appearance.
 * @param onSaveClick Callback function intended for when the save icon is clicked, but currently not in use.
 * @param onBackClick Callback function to be invoked when the back icon is clicked.
 */
@Composable
fun OrionTopAppBarWithSaveIcon(
    title: String,
    // Provides a default pinned scroll behavior if not specified
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
    // Provides default colors for the top app bar if not specified
    colours : TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
    onSaveClick: () -> Unit,
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
        // The actions section is commented out;
        // it was intended for a save icon which never got implemented
//        actions = {
//            IconButton(onClick = onSaveClick) {
//                Icon(
//                    imageVector = Icons.Default.Done,
//                    contentDescription = "Save"
//                )
//            }
//        },
        // Applies the custom colors passed to the function
        colors = colours
    )
}
