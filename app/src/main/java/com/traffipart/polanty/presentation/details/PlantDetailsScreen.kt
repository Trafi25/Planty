package com.traffipart.polanty.presentation.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage

/**
 * Screen displaying the details of a specific plant.
 * Allows the user to view plant information and delete the plant.
 *
 * @param onBack Callback invoked when the user wants to go back to the previous screen.
 * @param onDeleted Callback invoked when the plant has been successfully deleted.
 * @param viewModel The ViewModel providing the state and handling actions for this screen.
 */
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
        plant.imageUri?.let { imageUri ->
            AsyncImage(
                model = imageUri,
                contentDescription = plant.displayName,
                modifier = Modifier.fillMaxWidth().height(180.dp),
                contentScale = ContentScale.Crop,
            )
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
