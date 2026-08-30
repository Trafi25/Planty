package com.traffipart.polanty.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.traffipart.polanty.domain.model.PlantCandidate
import com.traffipart.polanty.presentation.garden.GardenScreen
import com.traffipart.polanty.presentation.scan.IdentifyPlantScreen
import com.traffipart.polanty.presentation.setup.PlantSetupScreen

private enum class PlantyScreen {
    Identify,
    Setup,
    Garden,
}

@Composable
fun PlantyRoot() {
    var selectedCandidate by remember {
        mutableStateOf<PlantCandidate?>(null)
    }

    var screen by remember {
        mutableStateOf(PlantyScreen.Identify)
    }

    when (screen) {
        PlantyScreen.Identify -> {
            IdentifyPlantScreen(
                onCandidateSelected = { candidate ->
                    selectedCandidate = candidate
                    screen = PlantyScreen.Setup
                },
            )
        }

        PlantyScreen.Setup -> {
            val candidate = selectedCandidate

            if (candidate != null) {
                PlantSetupScreen(
                    candidate = candidate,
                    onBack = { screen = PlantyScreen.Identify },
                    onPlantSaved = {
                        selectedCandidate = null
                        screen = PlantyScreen.Garden
                    },
                )
            }
        }

        PlantyScreen.Garden -> {
            GardenScreen(onAddPlant = {
                screen = PlantyScreen.Identify
            })
        }
    }
}
