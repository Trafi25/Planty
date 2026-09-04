package com.traffipart.polanty.presentation.garden.gardenContent

import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.traffipart.polanty.domain.model.PlantSpace
import com.traffipart.polanty.presentation.garden.GardenUiState
import com.traffipart.polanty.ui.theme.spacing

/**
 * Composable that displays the list of plant spaces in the garden.
 * It shows the name of each space and the number of plants assigned to it.
 *
 * @param state The current UI state of the garden.
 * @param onAddSpace Callback invoked when the user wants to add a new space.
 * @param onSpaceLongClicked Callback invoked when a space is long-clicked, typically for deletion.
 * @param modifier The modifier to be applied to the layout.
 */
@Composable
fun SpacesContent(
    state: GardenUiState,
    onAddSpace: () -> Unit,
    onSpaceSelected: (Long) -> Unit,
    onSpaceLongClicked: (PlantSpace) -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier =
            modifier
                .fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.mediumSmall),
        contentPadding = PaddingValues(bottom = MaterialTheme.spacing.large),
    ) {
        if (state.spaces.isEmpty()) {
            item {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(vertical = MaterialTheme.spacing.extraLarge),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.mediumSmall),
                ) {
                    Text(
                        text = "No spaces in your garden yet",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = "Add a space to organize your plants.",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
        items(
            items = state.spaces,
            key = { space -> space.id },
        ) { space ->
            val plantCount = state.plants.count { plant -> plant.spaceId == space.id }
            Card(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .combinedClickable(
                            onClick = { onSpaceSelected(space.id) },
                            onLongClick = {
                                onSpaceLongClicked(
                                    space,
                                )
                            },
                        ),
            ) {
                Row(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(MaterialTheme.spacing.medium),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = space.name,
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            text = if (plantCount == 1) "1 plant" else "$plantCount plants",
                            style = MaterialTheme.typography.bodySmall,
                        )
                    }
                    Text(text = ">")
                }
            }
        }
        item {
            Button(
                onClick = onAddSpace,
            ) { Text("+ Add space") }
        }
    }
}
