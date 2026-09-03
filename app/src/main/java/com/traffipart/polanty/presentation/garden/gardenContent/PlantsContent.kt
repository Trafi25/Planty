package com.traffipart.polanty.presentation.garden.gardenContent

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.traffipart.polanty.presentation.garden.GardenUiState

/**
 * Composable that displays the list of plants in the garden.
 * If the list is empty, it displays a message and a button to add the first plant.
 *
 * @param state The current UI state of the garden.
 * @param onAddPlant Callback invoked when the user wants to add a plant.
 * @param onPlantSelected Callback invoked when a plant is selected, providing its ID.
 * @param modifier The modifier to be applied to the layout.
 */
@Composable
fun PlantsContent(
    state: GardenUiState,
    onAddPlant: () -> Unit,
    onPlantSelected: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    if (state.plants.isEmpty()) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text("No plants in your garden yet.")
            Button(onClick = onAddPlant) {
                Text("Add first plant")
            }
        }
        return
    }
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        items(
            items = state.plants,
            key = { plant -> plant.id },
        ) { plant ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                onClick = { onPlantSelected(plant.id) },
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                ) {
                    plant.imageUri?.let { imageUri ->
                        AsyncImage(
                            model = imageUri,
                            contentDescription = plant.displayName,
                            modifier = Modifier.fillMaxWidth().height(180.dp),
                            contentScale = ContentScale.Crop,
                        )
                    }
                    Text(text = plant.displayName)
                }
            }
        }
    }
}
