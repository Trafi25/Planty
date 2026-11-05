package com.traffipart.polanty.domain.usecase.health

import com.traffipart.polanty.core.common.Result
import com.traffipart.polanty.core.common.trimToNull
import com.traffipart.polanty.domain.error.PlantHealthError
import com.traffipart.polanty.domain.model.health.PlantHealthAssessment
import com.traffipart.polanty.domain.model.plant.PlantImage
import com.traffipart.polanty.domain.repository.health.PlantHealthRepository
import javax.inject.Inject

/**
 * Validates an image and requests an assessment without saving plants or modifying care tasks.
 *
 * @property repository Provides the health assessment implementation.
 */
class AssessPlantHealthUseCase
@Inject
constructor(
    private val repository: PlantHealthRepository,
) {
    private val allowedMimeTypes =
        setOf(
            "image/jpeg",
            "image/png",
        )

    suspend operator fun invoke(
        image: PlantImage,
        scientificName: String? = null,
    ): Result<
            PlantHealthAssessment,
            PlantHealthError,
            > {
        if (
            image.bytes.isEmpty() ||
            image.mimeType !in allowedMimeTypes
        ) {
            return Result.Error(
                PlantHealthError.InvalidImage,
            )
        }

        return repository.assessPlantHealth(
            image = image,
            scientificName =
                scientificName.trimToNull(),
        )
    }
}
