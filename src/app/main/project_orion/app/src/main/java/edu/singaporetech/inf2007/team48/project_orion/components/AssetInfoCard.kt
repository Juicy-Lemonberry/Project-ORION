package edu.singaporetech.inf2007.team48.project_orion.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import edu.singaporetech.inf2007.team48.project_orion.extensionFunctions.long.toUnixFormattedDate

/**
 * Composable function that displays a card with information about an asset.
 *
 * @param assetImage Resource ID of the asset's image.
 * @param assetName Name or ID of the asset.
 * @param assetType Type/category of the asset.
 * @param assetDesc Description of the asset.
 * @param dateAdded Timestamp when the asset was added.
 * @param dateServiced Optional timestamp of the asset's last service date. `null` indicates no service date.
 * @param onClick Function to call when the card is clicked. This allows the card to be interactive.
 * @param paddingValues External padding for the card. Default is set to 16.dp to ensure consistent spacing around the card.
 */
@Composable
fun AssetInfoCard(
    assetImage: Int,
    assetName: String,
    assetType: String,
    assetDesc: String,
    dateAdded: Long,
    dateServiced: Long?,
    onClick: () -> Unit,
    paddingValues: PaddingValues = PaddingValues(16.dp)
) {
    // Horizontal layout with clickable behavior that triggers `onClick`.
    Row(
        modifier = Modifier
            .fillMaxWidth() // Make the row fill the maximum width available.
            .padding(paddingValues) // Apply external padding.
            .clickable { onClick() }, // Set the click action.
        verticalAlignment = Alignment.CenterVertically // Align children vertically at the center.
    ) {
        // Display the asset's image.
        Image(
            painter = painterResource(id = assetImage), // Load the image from resources.
            contentDescription = "Asset Image", // Accessibility description of the image.
            modifier = Modifier
                .size(110.dp) // Set the image size.
                .clip(RectangleShape), // Clip the image to a rectangle shape.
            contentScale = ContentScale.Crop // Crop the content to fit the size.
        )
        Spacer(modifier = Modifier.size(16.dp))
        Column {
            // Display the asset ID with dynamic color based on the system's theme (dark/light).
            Text(
                text = "ID: $assetName",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = if (isSystemInDarkTheme()) Color.Cyan else Color.Blue
            )
            // Use a helper composable for displaying asset type, description, and dates.
            AssetInfoDescText(titleText = "Asset Type", descText = assetType)
            AssetInfoDescText(titleText = "Asset Desc", descText = assetDesc)
            // Format the date added timestamp to a readable date string.
            AssetInfoDescText(
                titleText = "Date Added", descText = (dateAdded * 1000).toUnixFormattedDate()
            )
            // Format the date serviced timestamp if it's not null; otherwise, display "--".
            AssetInfoDescText(
                titleText = "Date Serviced",
                descText = if (dateServiced != null) (dateServiced * 1000).toUnixFormattedDate() else "--"
            )
        }
    }
}