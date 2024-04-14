package edu.singaporetech.inf2007.team48.project_orion.screens.editChecklist

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Composable function to display a checkbox to select all checklist items.
 * The checkbox allows the user to select all checklist items at once.
 * @param selectAllChecked The state of the checkbox.
 * @param onCheckChange The callback to change the state of the checkbox.
 *
 *
 */
@Composable
fun SelectAllCheckbox(
    selectAllChecked: Boolean,
    onCheckChange: (newCheck: Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = selectAllChecked,
            onCheckedChange = { onCheckChange(it) }
        )
        Text(
            text = "Select All",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
    }
}