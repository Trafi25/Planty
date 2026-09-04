package com.traffipart.polanty.presentation.garden.gardenContent

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.traffipart.polanty.domain.model.PlantSpace

/**
 * A dialog that asks for confirmation before deleting a plant space.
 *
 * @param space The [PlantSpace] to be deleted.
 * @param plantCount The number of plants currently assigned to this space.
 * @param isLoading Whether the deletion process is in progress.
 * @param errorMessage An optional error message to display if deletion fails.
 * @param onDelete Callback triggered when the user confirms deletion.
 * @param onDismiss Callback triggered when the dialog is dismissed or cancelled.
 */
@Composable
fun DeleteSpaceDialog(
    space: PlantSpace,
    plantCount: Int,
    isLoading: Boolean,
    errorMessage: String?,
    onDelete: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = {
            if (!isLoading) onDismiss()
        },
        title = { Text(text = "Delete ${space.name}") },
        text = {
            Text(
                text =
                    errorMessage ?: if (plantCount == 0) {
                        "This space is empty"
                    } else {
                        "$plantCount ${if (plantCount == 1) "plant" else "plants"} will remain in your garden and become unassigned."
                    },
            )
        },
        confirmButton = {
            TextButton(enabled = !isLoading, onClick = onDelete) {
                Text(if (isLoading) "Deleting..." else "Delete")
            }
        },
        dismissButton = {
            TextButton(enabled = !isLoading, onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}
