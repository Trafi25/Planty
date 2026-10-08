package com.traffipart.polanty.presentation.setup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.traffipart.polanty.domain.model.identification.PlantCandidate
import com.traffipart.polanty.domain.model.space.PlantSpace
import com.traffipart.polanty.domain.model.space.PlantSpaceType
import com.traffipart.polanty.presentation.garden.gardenContent.AddSpaceDialog
import com.traffipart.polanty.presentation.theme.PolantyTheme
import com.traffipart.polanty.presentation.theme.spacing

/**
 * Stateful wrapper for the Plant Setup screen.
 */
@Composable
fun PlantSetupScreen(
    candidate: PlantCandidate,
    onPlantSaved: (Long) -> Unit,
    onBack: () -> Unit,
    imageUri: String?,
    viewModel: PlantSetupViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    var showSpacePicker by rememberSaveable { mutableStateOf(false) }
    var showAddSpaceDialog by rememberSaveable { mutableStateOf(false) }
    var newSpaceType by rememberSaveable { mutableStateOf(PlantSpaceType.LivingRoom) }
    var newSpaceName by rememberSaveable { mutableStateOf("") }

    LaunchedEffect(candidate, imageUri) {
        viewModel.onAction(
            PlantSetupAction.Initialize(
                candidate = candidate,
                imageUri = imageUri,
            ),
        )
    }

    LaunchedEffect(state.savedPlantId) {
        state.savedPlantId?.let { plantId ->
            onPlantSaved(plantId)
        }
    }

    PlantSetupContent(
        state = state,
        candidate = candidate,
        imageUri = imageUri,
        onAction = viewModel::onAction,
        onBack = onBack,
        showSpacePicker = showSpacePicker,
        onShowSpacePickerChanged = { showSpacePicker = it },
        showAddSpaceDialog = showAddSpaceDialog,
        onShowAddSpaceDialogChanged = { showAddSpaceDialog = it },
        newSpaceType = newSpaceType,
        onNewSpaceTypeChanged = { newSpaceType = it },
        newSpaceName = newSpaceName,
        onNewSpaceNameChanged = { newSpaceName = it },
    )
}

/**
 * Stateless Plant Setup content composable.
 */
@Composable
fun PlantSetupContent(
    state: PlantSetupUiState,
    candidate: PlantCandidate,
    imageUri: String?,
    onAction: (PlantSetupAction) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    showSpacePicker: Boolean = false,
    onShowSpacePickerChanged: (Boolean) -> Unit = {},
    showAddSpaceDialog: Boolean = false,
    onShowAddSpaceDialogChanged: (Boolean) -> Unit = {},
    newSpaceType: PlantSpaceType = PlantSpaceType.LivingRoom,
    onNewSpaceTypeChanged: (PlantSpaceType) -> Unit = {},
    newSpaceName: String = "",
    onNewSpaceNameChanged: (String) -> Unit = {},
) {
    val activeCandidate = state.candidate ?: candidate
    val selectedSpace = state.spaces.firstOrNull { it.id == state.spaceId }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(MaterialTheme.spacing.large),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
    ) {
        Button(onClick = onBack) {
            Text("Back")
        }

        AsyncImage(
            model = state.imageUri ?: imageUri,
            contentDescription = null,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(16 / 9f)
                    .clip(MaterialTheme.shapes.medium),
            contentScale = ContentScale.Crop,
        )

        Column {
            Text(
                text = activeCandidate.commonName ?: activeCandidate.scientificName,
                style = MaterialTheme.typography.headlineSmall,
            )
            Text(
                text = "${(activeCandidate.confidence * 100).toInt()}% match",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
            )
        }

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.nickname,
            onValueChange = { nickname ->
                onAction(PlantSetupAction.NicknameChanged(nickname))
            },
            label = { Text("Plant nickname") },
            singleLine = true,
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            onClick = { onShowSpacePickerChanged(true) },
        ) {
            Row(
                modifier = Modifier.padding(MaterialTheme.spacing.medium),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Space",
                        style = MaterialTheme.typography.labelMedium,
                    )
                    Text(
                        text = selectedSpace?.name ?: "No space",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
                Text(">")
            }
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isSaving,
            onClick = { onAction(PlantSetupAction.SavePlant) },
        ) {
            if (state.isSaving) {
                CircularProgressIndicator()
            } else {
                Text("Add to my garden")
            }
        }
        if (state.saveError) {
            Text(text = "Could not save plant. Please try again.")
        }
    }

    if (showSpacePicker) {
        SpacePickerDialog(
            spaces = state.spaces,
            selectedSpaceId = state.spaceId,
            onSpaceSelected = { spaceId ->
                onAction(PlantSetupAction.SpaceIdSelected(spaceId))
                onShowSpacePickerChanged(false)
            },
            onAddNewSpaceClick = {
                onShowSpacePickerChanged(false)
                onShowAddSpaceDialogChanged(true)
            },
            onDismiss = { onShowSpacePickerChanged(false) },
        )
    }

    if (showAddSpaceDialog) {
        AddSpaceDialog(
            selectedType = newSpaceType,
            name = newSpaceName,
            onTypeChanged = { onNewSpaceTypeChanged(it) },
            onNameChanged = { onNewSpaceNameChanged(it) },
            errorMessage = null,
            isLoading = false,
            onAdd = {
                onAction(
                    PlantSetupAction.CreateSpace(
                        customName = newSpaceName,
                        type = newSpaceType,
                    ),
                )
                onShowAddSpaceDialogChanged(false)
                onNewSpaceNameChanged("")
            },
            onDismiss = {
                onShowAddSpaceDialogChanged(false)
                onNewSpaceNameChanged("")
            },
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PlantSetupContentPreview() {
    PolantyTheme {
        PlantSetupContent(
            state =
                PlantSetupUiState(
                    spaces = listOf(PlantSpace(1, "Living room", PlantSpaceType.LivingRoom)),
                ),
            candidate = PlantCandidate("Monstera deliciosa", "Swiss Cheese Plant", 0.95),
            imageUri = null,
            onAction = {},
            onBack = {},
        )
    }
}
