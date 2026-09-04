package com.traffipart.polanty.domain.usecase.plant

import com.traffipart.polanty.core.common.DataError
import com.traffipart.polanty.core.common.Result
import com.traffipart.polanty.domain.model.PlantIdentification
import com.traffipart.polanty.domain.model.PlantImage
import com.traffipart.polanty.domain.repository.PlantIdentificationRepository
import javax.inject.Inject

/**
 * Use case to identify a plant from an image.
 *
 * This use case validates the image MIME type and content before delegating to the repository.
 * Only JPEG and PNG images are allowed.
 *
 * @property repository The repository used to perform the identification.
 */
class IdentifyPlantUseCase
    @Inject
    constructor(
        private val repository: PlantIdentificationRepository,
    ) {
        private val allowedMimeTypes = setOf("image/jpeg", "image/png")

        /**
         * Triggers the plant identification process.
         *
         * @param image The [com.traffipart.polanty.domain.model.PlantImage] to be identified.
         * @return A [com.traffipart.polanty.core.common.Result] containing either [com.traffipart.polanty.domain.model.PlantIdentification] on success or [com.traffipart.polanty.core.common.DataError] on failure.
         */
        suspend operator fun invoke(image: PlantImage): Result<PlantIdentification, DataError> {
            if (image.bytes.isEmpty() || image.mimeType !in allowedMimeTypes) {
                return Result.Error(DataError.InvalidImage)
            }

            return repository.identifyPlant(image)
        }
    }
