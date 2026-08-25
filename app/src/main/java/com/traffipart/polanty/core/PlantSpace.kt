package com.traffipart.polanty.core

data class PlantSpace(
    val id: Long,
    val name: String,
    val type: PlantSpaceType,
)

enum class PlantSpaceType {
    LivingRoom,
    Bedroom,
    Backyard,
    Kitchen,
    Balcony,
    Office,
    Custom,
}
