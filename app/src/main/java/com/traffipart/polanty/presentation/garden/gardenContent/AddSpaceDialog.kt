package com.traffipart.polanty.presentation.garden.gardenContent

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable

@Composable
fun AddSpaceDialog(
    name: String,
    onNameChanged: (String) -> Unit,
    errorMessage: String?,
    isLoading: Boolean,
    onAdd: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = {
            if (!isLoading) {
                onDismiss()
            }
        },
        title = {
            Text("Add space")
        },
        text = {
            Column {
                OutlinedTextField(value = name, onValueChange = onNameChanged, label = { Text("Space name") }, singleLine = true)
                errorMessage?.let {
                    Text(text = it)
                }
            }
        },
        confirmButton = {
            TextButton(
                enabled = !isLoading && name.isNotBlank(),
                onClick = onAdd,
            ) {
                Text(if (isLoading) "Adding..." else "Add")
            }
        },
        dismissButton = {
            TextButton(enabled = !isLoading, onClick = onDismiss) {
                Text("Cancel")
            }
        },
    )
}
