package com.traffipart.polanty.presentation.setup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.traffipart.polanty.domain.model.PlantCandidate

/**
 * Screen for setting up a new plant before adding it to the garden.
 * Users can provide a nickname, select a space, and confirm saving the plant.
 *
 * @param candidate The plant candidate selected from the identification results.
 * @param onPlantSaved Callback invoked when the plant has been successfully saved, providing its ID.
 * @param onBack Callback invoked to navigate back to the previous screen.
 * @param imageUri The URI of the image taken or selected for identification.
 * @param viewModel The ViewModel providing the state and handling actions for this screen.
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

    val selectedSpace = state.spaces.firstOrNull { it.id == state.spaceId }

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

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(24.dp),
        verticalArrangement =
            Arrangement.spacedBy(16.dp),
    ) {
        Button(
            onClick = onBack,
        ) {
            Text("Back")
        }

        AsyncImage(
            model = imageUri,
            contentDescription = null,
            modifier =
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(16 / 9f)
                    .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop,
        )

        Column {
            Text(
                text = candidate.commonName ?: candidate.scientificName,
                style = MaterialTheme.typography.headlineSmall,
            )
            Text(
                text = "${(candidate.confidence * 100).toInt()}% match",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.secondary,
            )
        }

        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.nickname,
            onValueChange = { nickname ->
                viewModel.onAction(PlantSetupAction.NicknameChanged(nickname))
            },
            label = { Text("Plant nickname") },
            singleLine = true,
        )

        Card(
            modifier = Modifier.fillMaxWidth(),
            onClick = {
                showSpacePicker = true
            },
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Space",
                        style = MaterialTheme.typography.labelMedium,
                    )
                    Text(
                        text = selectedSpace?.name ?: "Select a space",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                }
                Text(">")
            }
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isSaving,
            onClick = { viewModel.onAction(PlantSetupAction.SavePlant) },
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
                viewModel.onAction(PlantSetupAction.SpaceIdSelected(spaceId))
                showSpacePicker = false
            },
            onDismiss = { showSpacePicker = false },
        )
    }
}
