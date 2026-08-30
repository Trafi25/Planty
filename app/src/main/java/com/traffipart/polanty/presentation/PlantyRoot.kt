package com.traffipart.polanty.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.traffipart.polanty.domain.model.PlantCandidate
import com.traffipart.polanty.presentation.scan.IdentifyPlantScreen
import com.traffipart.polanty.presentation.setup.PlantSetupScreen

@Composable
fun PlantyRoot() {
    var selectedCandidate by remember {
        mutableStateOf<PlantCandidate?>(null)
    }

    val candidate = selectedCandidate

    if (candidate == null) {
        IdentifyPlantScreen(
            onCandidateSelected = {
                selectedCandidate = it
            },
        )
    } else {
        PlantSetupScreen(
            candidate = candidate,
            onPlantSaved = { plantId -> selectedCandidate = null },
            onBack = {
                selectedCandidate = null
            },
        )
    }
}
