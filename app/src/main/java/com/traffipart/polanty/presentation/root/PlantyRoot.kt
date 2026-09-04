package com.traffipart.polanty.presentation.root

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.traffipart.polanty.domain.model.PlantCandidate
import com.traffipart.polanty.presentation.details.PlantDetailsScreen
import com.traffipart.polanty.presentation.garden.GardenScreen
import com.traffipart.polanty.presentation.scan.IdentifyPlantScreen
import com.traffipart.polanty.presentation.setup.PlantSetupScreen
import com.traffipart.polanty.presentation.spaceDetails.SpaceDetailsScreen

@Composable
fun PlantyRoot() {
    val navController = rememberNavController()

    var selectedCandidate by remember {
        mutableStateOf<PlantCandidate?>(null)
    }

    var selectedImageUri by
        rememberSaveable {
            mutableStateOf<String?>(null)
        }

    NavHost(
        navController = navController,
        startDestination = PlantRoute.GARDEN,
    ) {
        composable(route = PlantRoute.GARDEN) {
            GardenScreen(
                onAddPlant = {
                    navController.navigate(PlantRoute.IDENTIFY)
                },
                onPlantSelected = { plantId -> navController.navigate(PlantRoute.details(plantId)) },
                onSpaceSelected = { spaceId -> navController.navigate(PlantRoute.spaceDetails(spaceId)) },
            )
        }
        composable(route = PlantRoute.IDENTIFY) {
            IdentifyPlantScreen(
                onCandidateSelected = { candidate, imageUri ->
                    selectedCandidate = candidate
                    selectedImageUri = imageUri
                    navController.navigate(PlantRoute.SETUP)
                },
            )
        }
        composable(route = PlantRoute.SETUP) {
            val candidate = selectedCandidate
            if (candidate != null) {
                PlantSetupScreen(
                    candidate = candidate,
                    imageUri = selectedImageUri,
                    onBack = {
                        navController.popBackStack()
                    },
                    onPlantSaved = {
                        selectedCandidate = null
                        selectedImageUri = null
                        navController.navigate(PlantRoute.GARDEN) {
                            popUpTo(PlantRoute.GARDEN)
                            launchSingleTop = true
                        }
                    },
                )
            }
        }
        composable(
            route = PlantRoute.DETAILS,
            arguments =
                listOf(
                    navArgument(PlantRoute.PLANT_ID) {
                        type = NavType.LongType
                    },
                ),
        ) {
            PlantDetailsScreen(
                onBack = {
                    navController.popBackStack()
                },
                onDeleted = {
                    navController.popBackStack()
                },
            )
        }
        composable(
            route = PlantRoute.SPACE_DETAILS,
            arguments =
                listOf(
                    navArgument(PlantRoute.SPACE_ID) {
                        type = NavType.LongType
                    },
                ),
        ) {
            SpaceDetailsScreen(
                onBackClick = { navController.popBackStack() },
                onPlantSelected = { navController.navigate(PlantRoute.details(it)) },
            )
        }
    }
}
