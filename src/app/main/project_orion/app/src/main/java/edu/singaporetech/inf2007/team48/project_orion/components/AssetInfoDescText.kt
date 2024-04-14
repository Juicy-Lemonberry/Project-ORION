package edu.singaporetech.inf2007.team48.project_orion.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign


/**
 * Composable function designed to display a pair of texts: a title and a description.
 * This function is typically used to display asset information in a labeled format
 * (e.g., "Asset Type: Vehicle"), making it easier to present structured information
 * in a clear and consistent manner.
 *
 * @param titleText The title or label for the description. This is displayed in bold
 *                  to emphasize its role as a label for the subsequent description.
 * @param descText The actual description or value associated with the title. This text
 *                 provides detailed information or the value corresponding to the title.
 */
@Composable
fun AssetInfoDescText(
    titleText: String,
    descText: String,
) {
    // A row layout that fills the maximum available width, aligning its children vertically in the center.
    Row(
        modifier = Modifier
            .fillMaxWidth(), // Expand row to fill the maximum available width.
        verticalAlignment = Alignment.CenterVertically, // Vertically align the contents in the center.
        horizontalArrangement = Arrangement.Start // Horizontally arrange the contents at the start.
    ) {
        Row {
            // Display the title text in bold.
            Text(
                text = titleText + ": ", // Append a colon to the title text for visual separation.
                fontWeight = FontWeight.Bold, // Make the title text bold to distinguish it from the description.
                textAlign = TextAlign.Start // Align the text to the start of the row.
            )
            // Display the description text, aligned to the start.
            Text(text = descText, textAlign = TextAlign.Start)
        }
    }
}





