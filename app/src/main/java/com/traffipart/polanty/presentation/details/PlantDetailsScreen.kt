package com.traffipart.polanty.presentation.details

import android.widget.Button
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@Composable
fun PlantDetailsScreen(
    onBack: () -> Unit,
    onDeleted: () -> Unit,
    viewModel: PlantDetailsViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(state.isDeleted) {
        if (state.isDeleted) {
            onDeleted()
        }
    }

    Column(modifier = Modifier.fillMaxSize().padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Button(onClick = onBack) {
            Text("Back")
        }
        if (state.isLoading) {
            CircularProgressIndicator()
            return@Column
        }
        val plant = state.plant
        if (plant == null) {
            Text("Could not load plant")
            return@Column
        }
        Text(text = plant.displayName)
        plant.nickname?.let {
            Text(
                text = "Nickname: $it",
            )
        }

        plant.commonName?.let {
            Text(
                text = "Common name: $it",
            )
        }
        plant.spaceId?.let {
            Text(
                text = "Space ID: $it",
            )
        }

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isDeleting,
            onClick = { viewModel.onAction(PlantDetailsAction.DeletePlant) },
        ) {
            if (state.isDeleting) {
                CircularProgressIndicator()
            } else {
                Text("Delete plant")
            }
        }

        state.errorMessage?.let { Text(it) }
    }
}
