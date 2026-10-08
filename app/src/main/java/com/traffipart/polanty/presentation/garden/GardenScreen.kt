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
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.traffipart.polanty.domain.model.plant.Plant
import com.traffipart.polanty.domain.model.space.PlantSpace
import com.traffipart.polanty.domain.model.space.PlantSpaceType
import com.traffipart.polanty.presentation.garden.gardenContent.AddSpaceDialog
import com.traffipart.polanty.presentation.garden.gardenContent.DeleteSpaceDialog
import com.traffipart.polanty.presentation.garden.gardenContent.PlantsContent
import com.traffipart.polanty.presentation.garden.gardenContent.SpacesContent
import com.traffipart.polanty.presentation.theme.PolantyTheme
import com.traffipart.polanty.presentation.theme.spacing

enum class GardenTab {
    Plants,
    Spaces,
}

/**
 * Stateful wrapper for the Garden screen.
 */
@Composable
fun GardenScreen(
    onAddPlant: () -> Unit,
    onSpaceSelected: (Long) -> Unit,
    onPlantSelected: (Long) -> Unit,
    viewModel: GardenViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    var showAddSpaceDialog by rememberSaveable { mutableStateOf(false) }
    var newSpaceName by rememberSaveable { mutableStateOf("") }
    var newSpaceType by rememberSaveable { mutableStateOf(PlantSpaceType.Bedroom) }
    var spaceIdToDelete by rememberSaveable { mutableStateOf<Long?>(null) }

    val dismissAddSpace =
        remember(viewModel) {
            {
                showAddSpaceDialog = false
                newSpaceName = ""
                newSpaceType = PlantSpaceType.Bedroom
                viewModel.onAction(GardenAction.ClearAddSpaceError)
            }
        }

    val dismissDeleteSpace =
        remember(viewModel) {
            {
                spaceIdToDelete = null
                viewModel.onAction(GardenAction.ClearDeleteSpaceError)
            }
        }

    val spaceToDelete by remember {
        derivedStateOf {
            state.spaces.firstOrNull { space -> space.id == spaceIdToDelete }
        }
    }

    LaunchedEffect(viewModel) {
        viewModel.events.collect { event ->
            when (event) {
                GardenEvent.SpaceCreated -> dismissAddSpace()
                GardenEvent.SpaceDeleted -> dismissDeleteSpace()
            }
        }
    }

    GardenContent(
        state = state,
        onAction = viewModel::onAction,
        onAddPlant = onAddPlant,
        onSpaceSelected = onSpaceSelected,
        onPlantSelected = onPlantSelected,
        showAddSpaceDialog = showAddSpaceDialog,
        onShowAddSpaceDialogChanged = { showAddSpaceDialog = it },
        newSpaceName = newSpaceName,
        onNewSpaceNameChanged = { newSpaceName = it },
        newSpaceType = newSpaceType,
        onNewSpaceTypeChanged = { newSpaceType = it },
        spaceToDelete = spaceToDelete,
        onSpaceIdToDeleteChanged = { spaceIdToDelete = it },
        dismissAddSpace = dismissAddSpace,
        dismissDeleteSpace = dismissDeleteSpace,
    )
}

/**
 * Stateless Garden screen content composable.
 */
@Composable
fun GardenContent(
    state: GardenUiState,
    onAction: (GardenAction) -> Unit,
    onAddPlant: () -> Unit,
    onSpaceSelected: (Long) -> Unit,
    onPlantSelected: (Long) -> Unit,
    modifier: Modifier = Modifier,
    showAddSpaceDialog: Boolean = false,
    onShowAddSpaceDialogChanged: (Boolean) -> Unit = {},
    newSpaceName: String = "",
    onNewSpaceNameChanged: (String) -> Unit = {},
    newSpaceType: PlantSpaceType = PlantSpaceType.Bedroom,
    onNewSpaceTypeChanged: (PlantSpaceType) -> Unit = {},
    spaceToDelete: PlantSpace? = null,
    onSpaceIdToDeleteChanged: (Long?) -> Unit = {},
    dismissAddSpace: () -> Unit = {},
    dismissDeleteSpace: () -> Unit = {},
) {
    var selectedTab by rememberSaveable { mutableStateOf(GardenTab.Plants) }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(MaterialTheme.spacing.large),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
    ) {
        Text(
            text = "My Garden",
            style = MaterialTheme.typography.headlineMedium,
        )
        Text(
            text = "${state.plants.size} plants · ${state.spaces.size} spaces",
            style = MaterialTheme.typography.bodySmall,
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.small),
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
                    onAddSpace = { onShowAddSpaceDialogChanged(true) },
                    onSpaceLongClicked = { space ->
                        onSpaceIdToDeleteChanged(space.id)
                        onAction(GardenAction.ClearDeleteSpaceError)
                    },
                    onSpaceSelected = onSpaceSelected,
                )
            }
        }
    }

    if (showAddSpaceDialog) {
        AddSpaceDialog(
            selectedType = newSpaceType,
            name = newSpaceName,
            onNameChanged = {
                onNewSpaceNameChanged(it)
                onAction(GardenAction.ClearAddSpaceError)
            },
            onTypeChanged = { type ->
                onNewSpaceTypeChanged(type)
                onAction(GardenAction.ClearAddSpaceError)
            },
            errorMessage = state.addSpaceError,
            isLoading = state.isAddingSpace,
            onAdd = {
                onAction(GardenAction.AddSpace(type = newSpaceType, customName = newSpaceName))
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
                onAction(GardenAction.DeleteSpace(spaceId = space.id))
            },
            onDismiss = dismissDeleteSpace,
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun GardenContentPreview() {
    PolantyTheme {
        GardenContent(
            state =
                GardenUiState(
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
                    spaces =
                        listOf(
                            PlantSpace(id = 1, name = "Living room", type = PlantSpaceType.LivingRoom),
                        ),
                    isLoading = false,
                ),
            onAction = {},
            onAddPlant = {},
            onSpaceSelected = {},
            onPlantSelected = {},
        )
    }
}
