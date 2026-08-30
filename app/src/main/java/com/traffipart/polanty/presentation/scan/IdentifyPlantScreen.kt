package com.traffipart.polanty.presentation.scan

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import com.traffipart.polanty.core.common.toMessage

@Composable
fun IdentifyPlantScreen(viewModel: IdentifyPlantViewModel = hiltViewModel()) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    val photoPicker =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
        ) { uri ->
            if (uri != null) {
                val plantImage = uri.toPlantImage(context)
                if (plantImage != null) {
                    viewModel.onAction(IdentifyPlantAction.IdentifyPlant(plantImage))
                }
            }
        }
    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
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
                Text(
                    text =
                        "${candidate.scientificName} - " +
                            "${(candidate.confidence * 100).toInt()}%",
                )
            }
        }
        state.error?.let { error ->
            Text(text = error.toMessage())
        }
    }
}
