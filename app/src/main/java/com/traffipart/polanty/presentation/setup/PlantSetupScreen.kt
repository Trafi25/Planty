package com.traffipart.polanty.presentation.setup

import android.widget.Button
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.traffipart.polanty.domain.model.PlantCandidate

@Composable
fun PlantSetupScreen(
    candidate: PlantCandidate,
    onPlantSaved: (Long) -> Unit,
    onBack: () -> Unit,
    imageUri: String?,
    viewModel: PlantSetupViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

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
        Text(text = candidate.commonName ?: candidate.scientificName)
        Text(
            text =
                "${(candidate.confidence * 100).toInt()}% match",
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.nickname,
            onValueChange = { nickname ->
                viewModel.onAction(PlantSetupAction.NicknameChanged(nickname))
            },
            label = { Text("Plant nickname") },
            singleLine = true,
        )
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
}
