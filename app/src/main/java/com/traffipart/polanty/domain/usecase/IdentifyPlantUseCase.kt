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
        suspend operator fun invoke(image: PlantImage): Result<PlantIdentification, DataError> = repository.identifyPlant(image)
    }
