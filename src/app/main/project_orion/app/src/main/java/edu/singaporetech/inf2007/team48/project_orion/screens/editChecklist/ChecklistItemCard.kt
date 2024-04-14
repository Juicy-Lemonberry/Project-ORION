package edu.singaporetech.inf2007.team48.project_orion.screens.editChecklist

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import edu.singaporetech.inf2007.team48.project_orion.utils.displayStringTruncation

/**
 * Composable function to display a card for a checklist item.
 * The card contains a checkbox to mark the item as checked or unchecked.
 * The card also contains the title of the checklist item.
 * @param checklistTitle The title of the checklist item.
 * @param isChecked The state of the checkbox.
 * @param onCheckedChange The callback to change the state of the checkbox.
 * @param paddingValues The padding values for the card.
 */
@Composable
fun ChecklistItemCard(
    checklistTitle: String,
    isChecked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    paddingValues: PaddingValues = PaddingValues(8.dp),
) {


    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(paddingValues),
        verticalAlignment = Alignment.CenterVertically,
    )
    {
        // Checkbox
        Checkbox(
            checked = isChecked,
            onCheckedChange = onCheckedChange
        )

        // Spacer for padding
        Spacer(modifier = Modifier.width(8.dp))

        // Text
        Text(
            text = displayStringTruncation(
                checklistTitle, 30
            ),
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleMedium
        )
    }
}