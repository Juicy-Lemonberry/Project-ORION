package edu.singaporetech.inf2007.team48.project_orion.components

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight

/**
 * Composable function designed to display service report details in a structured format.
 *
 * @param titleText The title or label for the report description. This serves to categorize
 *                  or headline the content that follows, making it clear what aspect of the
 *                  service report is being described.
 * @param reportDesc The content of the report description. This is the actual information
 *                   or data related to the titleText, providing detailed insight into the
 *                   specific service aspect mentioned.
 */
@Composable
fun AssetServiceDescText(
    titleText: String,
    reportDesc: String,
) {
    Column {
        // Displays the title text in bold, using a larger body typography style for emphasis.
        Text(text = "$titleText:", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
        // Conditionally truncates the report description text to 39 characters followed by "..." if it exceeds 42 characters,
        // otherwise, it displays the full report description. This ensures the text fits within a predefined space.
        Text(text = if (reportDesc.length > 42) reportDesc.substring(0, 39) + "..." else reportDesc, style = MaterialTheme.typography.bodyLarge)
    }
}
