package com.traffipart.polanty.domain.model

data class PlantSpace(
    val id: Long,
    val name: String,
    val type: PlantSpaceType,
)

enum class PlantSpaceType(
    val displayName: String,
) {
    LivingRoom("Living room"),
    Bedroom("Bedroom"),
    Bathroom("Bathroom"),
    Backyard("Backyard"),
    Kitchen("Kitchen"),
    Balcony("Balcony"),
    Office("Office"),
    Custom("Custom"),
}
