package com.traffipart.polanty.domain.usecase

import com.traffipart.polanty.domain.model.Plant
import com.traffipart.polanty.domain.repository.PlantRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservePlantsUseCase
    @Inject
    constructor(
        private val repository: PlantRepository,
    ) {
        operator fun invoke(): Flow<List<Plant>> = repository.observePlants()
    }
