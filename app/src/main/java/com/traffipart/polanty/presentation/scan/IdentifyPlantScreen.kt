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
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.traffipart.polanty.core.common.toMessage
import com.traffipart.polanty.domain.model.PlantCandidate
import com.traffipart.polanty.ui.theme.spacing

/**
 * Screen for identifying a plant from a photo.
 * Users can pick a photo, see identification results, and select a candidate plant.
 *
 * @param onCandidateSelected Callback invoked when a plant candidate is selected.
 * @param viewModel The ViewModel providing the state and handling actions for this screen.
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
                selectedImageUri = uri.toString()
                val plantImage = uri.toPlantImage(context)
                if (plantImage != null) {
                    viewModel.onAction(IdentifyPlantAction.IdentifyPlant(plantImage))
                }
            }
        }
    Column(
        modifier = Modifier.fillMaxSize().padding(MaterialTheme.spacing.large),
        verticalArrangement = Arrangement.spacedBy(MaterialTheme.spacing.medium),
    ) {
        Button(
            onClick = {
                photoPicker.launch(
                    PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly,
                    ),
                )
            },
        ) {
            Text("Choose plant photo")
        }
        if (state.isLoading) {
            CircularProgressIndicator()
        }
        state.identification?.let { identification ->
            Text(text = "Best match: ${identification.bestMatch}")

            identification.candidates.forEach { candidate ->
                Card(
                    modifier =
                        Modifier.fillMaxWidth().clickable {
                            onCandidateSelected(candidate, selectedImageUri)
                        },
                ) {
                    Text(text = candidate.commonName ?: candidate.scientificName)
                    Text(
                        text =
                            "${(candidate.confidence * 100).toInt()}% match",
                    )
                }
            }
        }
        state.error?.let { error ->
            Text(text = error.toMessage())
        }
    }
}
