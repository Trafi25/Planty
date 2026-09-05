package com.traffipart.polanty.presentation.root

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.traffipart.polanty.domain.model.PlantCandidate
import com.traffipart.polanty.presentation.details.PlantDetailsScreen
import com.traffipart.polanty.presentation.garden.GardenScreen
import com.traffipart.polanty.presentation.home.HomeScreen
import com.traffipart.polanty.presentation.progress.ProgressScreen
import com.traffipart.polanty.presentation.scan.IdentifyPlantScreen
import com.traffipart.polanty.presentation.setup.PlantSetupScreen
import com.traffipart.polanty.presentation.spaceDetails.SpaceDetailsScreen

/**
 * The root Composable for the application, defining the global navigation graph and structure.
 *
 * It manages the [Scaffold] with a bottom navigation bar and coordinates transitions
 * between different screens like Home, Garden, Identification, and Details.
 *
 * @param rootViewModel The ViewModel handling global app initialization.
 */
@Composable
fun PlantyRoot(rootViewModel: PlantyRootViewModel = hiltViewModel()) {
    val navController = rememberNavController()

    var selectedCandidate by remember {
        mutableStateOf<PlantCandidate?>(null)
    }

    var selectedImageUri by
        rememberSaveable {
            mutableStateOf<String?>(null)
        }
    val backStackEntry by navController.currentBackStackEntryAsState()

    val currentRoute = backStackEntry?.destination?.route
    val showBottomBar =
        TopLevelDestination.entries.any { destination ->
            destination.route == currentRoute
        }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                PlantyBottomBar(
                    currentRoute = currentRoute,
                    onDestinationSelected = { destination ->
                        navController.navigateToTopLevel(destination.route)
                    },
                )
            }
        },
    ) { innerPadding ->
        NavHost(
            modifier = Modifier.padding(innerPadding),
            navController = navController,
            startDestination = PlantRoute.HOME,
        ) {
            composable(route = PlantRoute.HOME) {
                HomeScreen(
                    onOpenGarden = {
                        navController.navigateToTopLevel(PlantRoute.GARDEN)
                    },
                    onScanPlant = {
                        navController.navigateToTopLevel(PlantRoute.IDENTIFY)
                    },
                )
            }
            composable(route = PlantRoute.PROGRESS) {
                ProgressScreen()
            }
            composable(route = PlantRoute.GARDEN) {
                GardenScreen(
                    onAddPlant = {
                        navController.navigateToTopLevel(PlantRoute.IDENTIFY)
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
}

/**
 * Extension function for [NavController] to handle top-level destination navigation.
 *
 * It ensures proper backstack management by popping to the start destination,
 * enabling single-top launches, and restoring state.
 *
 * @param route The target route to navigate to.
 */
private fun NavController.navigateToTopLevel(route: String) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }
        launchSingleTop = true
        restoreState = true
    }
}
