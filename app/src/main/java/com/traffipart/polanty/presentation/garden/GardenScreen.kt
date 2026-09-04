package com.traffipart.polanty.presentation.garden

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.traffipart.polanty.domain.model.PlantSpaceType
import com.traffipart.polanty.presentation.garden.gardenContent.AddSpaceDialog
import com.traffipart.polanty.presentation.garden.gardenContent.DeleteSpaceDialog
import com.traffipart.polanty.presentation.garden.gardenContent.PlantsContent
import com.traffipart.polanty.presentation.garden.gardenContent.SpacesContent

private enum class GardenTab {
    Plants,
    Spaces,
}

/**
 * The main screen for the garden, displaying plants and spaces.
 * It allows users to switch between viewing plants and spaces, and to add new plants or spaces.
 *
 * @param onAddPlant Callback invoked when the user wants to add a new plant.
 * @param onPlantSelected Callback invoked when a plant is selected, providing its ID.
 * @param viewModel The ViewModel that provides the state and handles actions for this screen.
 */
@Composable
fun GardenScreen(
    onAddPlant: () -> Unit,
    onPlantSelected: (Long) -> Unit,
    viewModel: GardenViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    var selectedTab by rememberSaveable { mutableStateOf(GardenTab.Plants) }

    var showAddSpaceDialog by rememberSaveable { mutableStateOf(false) }

    var newSpaceName by rememberSaveable { mutableStateOf("") }

    var newSpaceType by rememberSaveable { mutableStateOf(PlantSpaceType.Bedroom) }

    var spaceIdToDelete by rememberSaveable {
        mutableStateOf<Long?>(null)
    }

    val dismissAddSpace = {
        showAddSpaceDialog = false
        newSpaceName = ""
        newSpaceType = PlantSpaceType.Bedroom
        viewModel.onAction(GardenAction.ClearAddSpaceError)
    }

    val dismissDeleteSpace = {
        spaceIdToDelete = null
        viewModel.onAction(GardenAction.ClearDeleteSpaceError)
    }

    val spaceToDelete =
        state.spaces.firstOrNull { space ->
            space.id == spaceIdToDelete
        }

    LaunchedEffect(state.isAddingSpace, state.addSpaceError, state.spaces) {
        if (!state.isAddingSpace && state.addSpaceError == null && showAddSpaceDialog) {
            dismissAddSpace()
        }
    }

    LaunchedEffect(state.isDeletingSpace, state.deleteSpaceError, state.spaces) {
        if (!state.isDeletingSpace && state.deleteSpaceError == null && spaceIdToDelete != null) {
            dismissDeleteSpace()
        }
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        Text(
            text = "My Garden",
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            text =
                "${state.plants.size} plants · " +
                    "${state.spaces.size} spaces",
            style = MaterialTheme.typography.bodySmall,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            FilterChip(
                modifier = Modifier.weight(1f),
                selected = selectedTab == GardenTab.Plants,
                onClick = { selectedTab = GardenTab.Plants },
                label = { Text("All plants") },
            )

            FilterChip(
                modifier = Modifier.weight(1f),
                selected = selectedTab == GardenTab.Spaces,
                onClick = { selectedTab = GardenTab.Spaces },
                label = { Text("Spaces") },
            )
        }

        when (selectedTab) {
            GardenTab.Plants -> {
                PlantsContent(
                    modifier = Modifier.weight(1f),
                    state = state,
                    onAddPlant = onAddPlant,
                    onPlantSelected = onPlantSelected,
                )
            }

            GardenTab.Spaces -> {
                SpacesContent(
                    modifier = Modifier.weight(1f),
                    state = state,
                    onAddSpace = {
                        showAddSpaceDialog = true
                    },
                    onSpaceLongClicked = { space ->
                        spaceIdToDelete = space.id
                        viewModel.onAction(
                            GardenAction.ClearDeleteSpaceError,
                        )
                    },
                )
            }
        }
    }

    if (showAddSpaceDialog) {
        AddSpaceDialog(
            selectedType = newSpaceType,
            name = newSpaceName,
            onNameChanged = {
                newSpaceName = it
                viewModel.onAction(GardenAction.ClearAddSpaceError)
            },
            onTypeChanged = { type ->
                newSpaceType = type
                viewModel.onAction(
                    GardenAction.ClearAddSpaceError,
                )
            },
            errorMessage = state.addSpaceError,
            isLoading = state.isAddingSpace,
            onAdd = {
                viewModel.onAction(GardenAction.AddSpace(type = newSpaceType, customName = newSpaceName))
            },
            onDismiss = dismissAddSpace,
        )
    }

    spaceToDelete?.let { space ->
        val plantCount = state.plants.count { plant -> plant.spaceId == space.id }
        DeleteSpaceDialog(
            space = space,
            plantCount = plantCount,
            isLoading = state.isDeletingSpace,
            errorMessage = state.deleteSpaceError,
            onDelete = {
                viewModel.onAction(
                    GardenAction.DeleteSpace(spaceId = space.id),
                )
            },
            onDismiss = dismissDeleteSpace,
        )
    }
}
