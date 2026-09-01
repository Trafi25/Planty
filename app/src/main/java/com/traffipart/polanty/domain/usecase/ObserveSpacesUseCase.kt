package com.traffipart.polanty.domain.usecase

import com.traffipart.polanty.domain.model.PlantSpace
import com.traffipart.polanty.domain.repository.PlantSpaceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveSpacesUseCase
    @Inject
    constructor(
        private val repository: PlantSpaceRepository,
    ) {
        operator fun invoke(): Flow<List<PlantSpace>> = repository.observeSpaces()
    }
