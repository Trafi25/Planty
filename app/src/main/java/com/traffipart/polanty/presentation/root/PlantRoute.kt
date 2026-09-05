package com.traffipart.polanty.presentation.root

/**
 * Centralized navigation routes and argument keys for the application.
 */
internal object PlantRoute {
    const val HOME = "home"
    const val GARDEN = "garden"
    const val IDENTIFY = "identify"
    const val PROGRESS = "progress"
    const val SETUP = "setup"

    /** Key for the space ID navigation argument. */
    const val SPACE_ID = "spaceId"

    /** Key for the plant ID navigation argument. */
    const val PLANT_ID = "plantId"

    /** Route for the plant details screen. */
    const val DETAILS = "plant/{$PLANT_ID}"

    /** Route for the space details screen. */
    const val SPACE_DETAILS = "space/{$SPACE_ID}"

    /**
     * Builds a navigation route for a specific plant's details.
     * @param plantId The unique ID of the plant.
     */
    fun details(plantId: Long): String = "plant/$plantId"

    /**
     * Builds a navigation route for a specific space's details.
     * @param spaceId The unique ID of the space.
     */
    fun spaceDetails(spaceId: Long): String = "space/$spaceId"
}
