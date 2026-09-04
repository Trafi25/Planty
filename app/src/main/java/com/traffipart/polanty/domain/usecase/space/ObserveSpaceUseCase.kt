package com.traffipart.polanty.domain.usecase.space

import com.traffipart.polanty.domain.model.PlantSpace
import com.traffipart.polanty.domain.repository.PlantSpaceRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveSpaceUseCase
    @Inject
    constructor(
        private val repository: PlantSpaceRepository,
    ) {
        operator fun invoke(spaceId: Long): Flow<PlantSpace?> = repository.observeSpace(spaceId)
    }
