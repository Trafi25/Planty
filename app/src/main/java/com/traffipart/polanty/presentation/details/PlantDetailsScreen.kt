package com.traffipart.polanty.presentation.details

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.traffipart.polanty.domain.model.plant.Plant
import com.traffipart.polanty.presentation.theme.PolantyTheme
import com.traffipart.polanty.presentation.theme.spacing

/**
 * Stateful wrapper for the Plant Details screen.
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

    PlantDetailsContent(
        state = state,
        onAction = viewModel::onAction,
        onBack = onBack,
    )
}

/**
 * Stateless Plant Details content composable.
 *
 * @param state Current [PlantDetailsUiState].
 * @param onAction Callback invoked when a [PlantDetailsAction] is dispatched.
 * @param onBack Callback invoked to navigate back.
 * @param modifier The modifier to be applied to the layout.
 */
@Composable
fun PlantDetailsContent(
    state: PlantDetailsUiState,
    onAction: (PlantDetailsAction) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(MaterialTheme.spacing.large),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
    ) {
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
        plant.nickname?.let { Text("Nickname: $it") }
        plant.commonName?.let { Text("Common name: $it") }
        plant.spaceId?.let { Text("Space ID: $it") }

        PlantKnowledgeContent(
            state = state.knowledgeState,
            onRetry = { onAction(PlantDetailsAction.RetryKnowledge) },
        )

        Button(
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isDeleting,
            onClick = { onAction(PlantDetailsAction.DeletePlant) },
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

@Preview(showBackground = true)
@Composable
private fun PlantDetailsContentPreview() {
    PolantyTheme {
        PlantDetailsContent(
            state =
                PlantDetailsUiState(
                    plant =
                        Plant(
                            id = 1,
                            scientificName = "Monstera deliciosa",
                            commonName = "Swiss Cheese Plant",
                            nickname = "Monty",
                            spaceId = 1,
                            imageUri = null,
                        ),
                    isLoading = false,
                ),
            onAction = {},
            onBack = {},
        )
    }
}
