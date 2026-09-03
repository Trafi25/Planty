package com.traffipart.polanty.presentation.garden.gardenContent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.traffipart.polanty.domain.model.PlantSpaceType

@Composable
fun AddSpaceDialog(
    selectedType: PlantSpaceType,
    name: String,
    onTypeChanged: (PlantSpaceType) -> Unit,
    onNameChanged: (String) -> Unit,
    errorMessage: String?,
    isLoading: Boolean,
    onAdd: () -> Unit,
    onDismiss: () -> Unit,
) {
    var showTypeMenu by rememberSaveable { mutableStateOf(false) }

    val isCustom = selectedType == PlantSpaceType.Custom

    val canAdd = !isLoading && (!isCustom || name.trim().length >= 2)

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
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text("Type")

                Column {
                    OutlinedButton(
                        modifier = Modifier.fillMaxWidth(),
                        enabled = !isLoading,
                        onClick = { showTypeMenu = true },
                    ) {
                        Text(
                            modifier = Modifier.weight(1f),
                            text = selectedType.displayName,
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = "Choose space type",
                        )
                    }

                    DropdownMenu(
                        expanded = showTypeMenu,
                        onDismissRequest = { showTypeMenu = false },
                    ) {
                        PlantSpaceType.entries.forEach { type ->
                            DropdownMenuItem(
                                text = { Text(type.displayName) },
                                onClick = {
                                    onTypeChanged(type)
                                    showTypeMenu = false
                                },
                            )
                        }
                    }
                }

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = name,
                    enabled = !isLoading,
                    onValueChange = onNameChanged,
                    label = { Text("Space name") },
                    singleLine = true,
                    isError = errorMessage != null,
                    supportingText = {
                        Text(
                            text =
                                errorMessage ?: if (isCustom) {
                                    "Required - at least 2 characters"
                                } else {
                                    "Optional - leave blank to use ${selectedType.displayName}"
                                },
                        )
                    },
                )
            }
        },
        confirmButton = {
            TextButton(
                enabled = canAdd,
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
