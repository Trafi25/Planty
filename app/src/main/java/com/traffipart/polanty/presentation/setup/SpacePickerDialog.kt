package com.traffipart.polanty.presentation.setup

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.traffipart.polanty.domain.model.PlantSpace

/**
 * A dialog for picking a space from a list of available spaces.
 *
 * @param spaces The list of [PlantSpace]s to choose from.
 * @param selectedSpaceId The ID of the currently selected space, if any.
 * @param onSpaceSelected Callback invoked when a space is selected.
 * @param onDismiss Callback invoked when the user cancels or dismisses the dialog.
 */
@Composable
fun SpacePickerDialog(
    spaces: List<PlantSpace>,
    selectedSpaceId: Long?,
    onSpaceSelected: (Long) -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Select a space") },
        text = {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                spaces.forEach { space ->
                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onSpaceSelected(space.id)
                                }.padding(vertical = 12.dp, horizontal = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            modifier = Modifier.weight(1f),
                            text = space.name,
                        )
                        if (space.id == selectedSpaceId) {
                            Text("✓")
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}
