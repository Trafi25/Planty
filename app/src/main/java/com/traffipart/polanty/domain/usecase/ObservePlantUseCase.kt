package com.traffipart.polanty.domain.usecase

import com.traffipart.polanty.domain.repository.PlantRepository
import javax.inject.Inject

class ObservePlantUseCase
    @Inject
    constructor(
        private val repository: PlantRepository,
    ) {
        operator fun invoke(plantId: Long) = repository.observePlant(plantId)
    }
