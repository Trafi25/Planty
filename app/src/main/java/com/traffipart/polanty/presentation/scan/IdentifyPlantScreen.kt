package com.traffipart.polanty.presentation.scan

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.traffipart.polanty.core.common.toMessage
import com.traffipart.polanty.domain.model.identification.PlantCandidate
import com.traffipart.polanty.domain.model.identification.PlantIdentification
import com.traffipart.polanty.presentation.theme.PolantyTheme
import com.traffipart.polanty.presentation.theme.spacing

/**
 * Stateful wrapper for the Plant Identification screen.
 *
 * @param onCandidateSelected Callback invoked when a plant candidate is selected.
 * @param viewModel The ViewModel providing state and handling actions for this screen.
 */
@Composable
fun IdentifyPlantScreen(
    onCandidateSelected: (candidate: PlantCandidate, imageUri: String?) -> Unit,
    viewModel: IdentifyPlantViewModel = hiltViewModel(),
) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current
    var selectedImageUri by remember { mutableStateOf<String?>(null) }

    val photoPicker =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
        ) { uri ->
            if (uri != null) {
                viewModel.onAction(IdentifyPlantAction.Reset)
                selectedImageUri = uri.toString()
                val plantImage = uri.toPlantImage(context)
                if (plantImage != null) {
                    viewModel.onAction(IdentifyPlantAction.IdentifyPlant(plantImage))
                }
            }
        }

    IdentifyPlantContent(
        state = state,
        onPickPhoto = {
            photoPicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        },
        onCandidateSelected = { candidate ->
            val uri = selectedImageUri
            viewModel.onAction(IdentifyPlantAction.Reset)
            selectedImageUri = null
            onCandidateSelected(candidate, uri)
        },
    )
}

/**
 * Stateless Plant Identification content composable.
 *
 * @param state Current [IdentifyPlantUiState].
 * @param onPickPhoto Callback to trigger the photo picker launcher.
 * @param onCandidateSelected Callback when a plant candidate is tapped.
 * @param modifier The modifier to be applied to the layout.
 */
@Composable
fun IdentifyPlantContent(
    state: IdentifyPlantUiState,
    onPickPhoto: () -> Unit,
    onCandidateSelected: (candidate: PlantCandidate) -> Unit,
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
        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onPickPhoto,
        ) {
            Text("Choose plant photo")
        }

        if (state.isLoading) {
            CircularProgressIndicator()
        }

        state.identification?.let { identification ->
            Text(
                text = "Best match: ${identification.bestMatch}",
                style = MaterialTheme.typography.titleMedium,
            )

            identification.candidates.forEach { candidate ->
                Card(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clickable { onCandidateSelected(candidate) },
                ) {
                    Column(
                        modifier = Modifier.padding(MaterialTheme.spacing.medium),
                    ) {
                        Text(
                            text = candidate.commonName ?: candidate.scientificName,
                            style = MaterialTheme.typography.titleMedium,
                        )
                        Text(
                            text = "${(candidate.confidence * 100).toInt()}% match",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.secondary,
                        )
                    }
                }
            }
        }

        state.error?.let { error ->
            Text(
                text = error.toMessage(),
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun IdentifyPlantContentPreview() {
    PolantyTheme {
        IdentifyPlantContent(
            state =
                IdentifyPlantUiState(
                    identification =
                        PlantIdentification(
                            bestMatch = "Monstera deliciosa",
                            candidates =
                                listOf(
                                    PlantCandidate("Monstera deliciosa", "Swiss Cheese Plant", 0.95),
                                ),
                        ),
                    isLoading = false,
                ),
            onPickPhoto = {},
            onCandidateSelected = {},
        )
    }
}
