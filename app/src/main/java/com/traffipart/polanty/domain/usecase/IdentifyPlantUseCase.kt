package com.traffipart.polanty.domain.usecase

import com.traffipart.polanty.domain.model.PlantIdentification
import com.traffipart.polanty.domain.model.PlantImage
import com.traffipart.polanty.domain.repository.PlantIdentificationRepository
import javax.inject.Inject

class IdentifyPlantUseCase
    @Inject
    constructor(
        private val repository: PlantIdentificationRepository,
    ) {
        suspend operator fun invoke(image: PlantImage): Result<PlantIdentification> = repository.identifyPlant(image)
    }
