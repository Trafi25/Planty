package com.traffipart.polanty.presentation.spaceDetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.traffipart.polanty.domain.model.plant.Plant
import com.traffipart.polanty.domain.model.space.PlantSpace
import com.traffipart.polanty.domain.model.space.PlantSpaceType
import com.traffipart.polanty.presentation.garden.gardenContent.PlantCard
import com.traffipart.polanty.presentation.theme.PolantyTheme
import com.traffipart.polanty.presentation.theme.spacing

/**
 * Stateful wrapper for the Space Details screen.
 */
@Composable
fun SpaceDetailsScreen(
    onBackClick: () -> Unit,
    onPlantSelected: (Long) -> Unit,
    viewModel: SpaceDetailsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    SpaceDetailsContent(
        state = state,
        onBackClick = onBackClick,
        onPlantSelected = onPlantSelected,
    )
}

/**
 * Stateless Space Details content composable.
 */
@Composable
fun SpaceDetailsContent(
    state: SpaceDetailsUiState,
    onBackClick: () -> Unit,
    onPlantSelected: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
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
        Text(text = space.name, style = MaterialTheme.typography.headlineSmall)
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
                    PlantCard(
                        plant = plant,
                        onClick = { onPlantSelected(plant.id) },
                    )
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

@Preview(showBackground = true)
@Composable
private fun SpaceDetailsContentPreview() {
    PolantyTheme {
        SpaceDetailsContent(
            state =
                SpaceDetailsUiState(
                    space = PlantSpace(id = 1, name = "Living room", type = PlantSpaceType.LivingRoom),
                    plants =
                        listOf(
                            Plant(
                                id = 1,
                                scientificName = "Monstera deliciosa",
                                commonName = "Swiss Cheese Plant",
                                nickname = "Monty",
                                spaceId = 1,
                                imageUri = null,
                            ),
                        ),
                    isLoading = false,
                ),
            onBackClick = {},
            onPlantSelected = {},
        )
    }
}
