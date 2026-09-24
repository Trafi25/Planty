package com.traffipart.polanty.domain.model

/**
 * Represents an individual plant in the user's garden.
 *
 * @property id Unique identifier of the plant entity.
 * @property scientificName Scientific botanical name of the plant.
 * @property commonName Primary common name of the plant.
 * @property nickname User-assigned custom nickname.
 * @property spaceId Identifier of the assigned [PlantSpace], or `null` if unassigned.
 * @property imageUri Local URI string of the plant's photo.
 */
data class Plant(
    val id: Long = 0,
    val scientificName: String,
    val commonName: String?,
    val nickname: String?,
    val spaceId: Long?,
    val imageUri: String?,
) {
    /**
     * User-facing primary display label, prioritized as nickname -> commonName -> scientificName.
     */
    val displayName: String
        get() = nickname ?: commonName ?: scientificName
}
