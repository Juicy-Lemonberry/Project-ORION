package edu.singaporetech.inf2007.team48.project_orion.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarColors
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import edu.singaporetech.inf2007.team48.project_orion.R


// Opt-in annotation for using experimental Material3 API features
@OptIn(ExperimentalMaterial3Api::class)
// Composable function to create a Center Aligned TopAppBar with a title and a profile picture icon
/**
 * Constructs a center-aligned top app bar with a title and a profile picture icon.
 *
 * @param title The text title displayed in the center of the top app bar.
 * @param scrollBehavior Defines the behavior of the top app bar when scrolling.
 * @param colours Custom colors for the top app bar appearance.
 * @param onProfilePictureClick Callback function to be invoked when the profile picture icon is clicked.
 */
@Composable
fun OrionTopAppBarWithTitle(
    title: String,
    // Provides a default pinned scroll behavior if not specified
    scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior(),
    // Provides default colors for the top app bar if not specified
    colours: TopAppBarColors = TopAppBarDefaults.topAppBarColors(),
    onProfilePictureClick: () -> Unit,
) {
    // Constructs a CenterAlignedTopAppBar composable
    CenterAlignedTopAppBar(
        // Sets the title of the CenterAlignedTopAppBar, centered by default
        title = { Text(text = title) },
        // Defines the actions to be included in the TopAppBar (profile picture)
        actions = {
            IconButton(onClick = onProfilePictureClick) {
                // Display a profile picture as an action icon with specific modifications
                Image(
                    painter = painterResource(id = R.drawable.profile_picture),
                    contentDescription = "User Avatar",
                    modifier = Modifier
                        .size(100.dp) // Sets the size of the image
                        .clip(CircleShape), // Clips the image to a circle shape
                    contentScale = ContentScale.Crop // Crops the image to fit the modifier size
                )
            }
        },
        // Applies the custom colors passed to the function
        colors = colours
    )
}