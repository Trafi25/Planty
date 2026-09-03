package com.traffipart.polanty.presentation.garden.gardenContent

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
import androidx.compose.ui.unit.dp
import com.traffipart.polanty.presentation.garden.GardenUiState

@Composable
fun SpacesContent(
    state: GardenUiState,
    onAddSpace: () -> Unit,
    modifier: Modifier = Modifier,
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 24.dp),
    ) {
        if (state.spaces.isEmpty()) {
            item {
                Column(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = "No spaces in your garden yet",
                        style = MaterialTheme.typography.titleMedium,
                    )
                    Text(
                        text = "Add a space to start adding plants.",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }
        }
        items(
            items = state.spaces,
            key = { space -> space.id },
        ) { space ->
            val plantCount = state.plants.count { plant -> plant.spaceId == plant.id }
            Card(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(16.dp),
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
