package com.traffipart.polanty.domain.usecase.plant

import com.traffipart.polanty.domain.model.Plant
import com.traffipart.polanty.domain.repository.PlantRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObservePlantsBySpaceUseCase
    @Inject
    constructor(
        private val repository: PlantRepository,
    ) {
        operator fun invoke(spaceId: Long): Flow<List<Plant>> = repository.observePlantsBySpace(spaceId)
    }
