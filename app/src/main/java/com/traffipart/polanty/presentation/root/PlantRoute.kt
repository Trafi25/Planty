package com.traffipart.polanty.presentation.root

internal object PlantRoute {
    const val GARDEN = "garden"
    const val IDENTIFY = "identify"
    const val SETUP = "setup"

    const val SPACE_ID = "spaceId"

    const val PLANT_ID = "plantId"

    const val DETAILS =
        "plant/{$PLANT_ID}"

    const val SPACE_DETAILS = "space/{$SPACE_ID}"

    fun details(plantId: Long): String = "plant/$plantId"

    fun spaceDetails(spaceId: Long): String = "space/$spaceId"
}
