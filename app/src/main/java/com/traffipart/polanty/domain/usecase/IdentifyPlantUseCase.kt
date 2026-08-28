package com.traffipart.polanty.domain.usecase

import com.traffipart.polanty.core.common.DataError
import com.traffipart.polanty.core.common.Result
import com.traffipart.polanty.domain.model.PlantIdentification
import com.traffipart.polanty.domain.model.PlantImage
import com.traffipart.polanty.domain.repository.PlantIdentificationRepository
import javax.inject.Inject

class IdentifyPlantUseCase
    @Inject
    constructor(
        private val repository: PlantIdentificationRepository,
    ) {
        private val allowedMimeTypes = setOf("image/jpeg", "image/png")

        suspend operator fun invoke(image: PlantImage): Result<PlantIdentification, DataError> {
            if (image.bytes.isEmpty() || image.mimeType !in allowedMimeTypes) {
                return Result.Error(DataError.InvalidImage)
            }

            return repository.identifyPlant(image)
        }
    }
