package com.traffipart.polanty.presentation.spaceDetails

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.traffipart.polanty.ui.theme.spacing

@Composable
fun SpaceDetailsScreen(
    onBackClick: () -> Unit,
    onPlantSelected: (Long) -> Unit,
    viewModel: SpaceDetailsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(MaterialTheme.spacing.large),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
    ) {
        Button(onClick = onBackClick) {
            Text("Back")
        }
        if (state.isLoading) {
            CircularProgressIndicator()
            return@Column
        }
        val space = state.space
        if (space == null) {
            Text(text = "Space not found", style = MaterialTheme.typography.titleMedium)
            return@Column
        }
        Text(text = space.name, style = MaterialTheme.typography.titleMedium)
        Text(text = space.type.displayName, style = MaterialTheme.typography.bodyMedium)
        Text(
            text =
                if (state.plants.size == 1) {
                    "1 plant"
                } else {
                    "${state.plants.size} plants"
                },
            style = MaterialTheme.typography.bodySmall,
        )
        Text(
            text = "Plants",
            style = MaterialTheme.typography.titleMedium,
        )
        if (state.plants.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.mediumSmall),
                contentPadding = PaddingValues(bottom = MaterialTheme.spacing.large),
            ) {
                items(
                    items = state.plants,
                    key = { plant -> plant.id },
                ) { plant ->
                    Card(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .clickable {
                                    onPlantSelected(plant.id)
                                },
                    ) {
                        Column(
                            modifier = Modifier.padding(MaterialTheme.spacing.medium),
                            verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.extraSmall),
                        ) {
                            Text(text = plant.displayName, style = MaterialTheme.typography.titleMedium)
                            plant.commonName?.let {
                                Text(text = it, style = MaterialTheme.typography.bodySmall)
                            }
                        }
                    }
                }
            }
        } else {
            Text(
                text = "No plants in this space yet.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
