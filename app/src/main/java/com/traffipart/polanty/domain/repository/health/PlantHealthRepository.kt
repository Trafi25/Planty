package com.traffipart.polanty.domain.repository.health

import com.traffipart.polanty.core.common.Result
import com.traffipart.polanty.domain.error.PlantHealthError
import com.traffipart.polanty.domain.model.health.PlantHealthAssessment
import com.traffipart.polanty.domain.model.plant.PlantImage

/** Provider-independent contract for image-based plant health assessment. */
interface PlantHealthRepository {
    /**
     * Assesses visible plant health from an image, optionally informed by its known species.
     * Implementations map provider failures to [PlantHealthError] and propagate cancellation.
     *
     * @param image JPEG or PNG image to assess.
     * @param scientificName Known species name, or null when the plant is unidentified.
     */
    suspend fun assessPlantHealth(
        image: PlantImage,
        scientificName: String? = null,
    ): Result<PlantHealthAssessment, PlantHealthError>
}
