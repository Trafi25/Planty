package com.traffipart.polanty.domain.usecase.location

import com.traffipart.polanty.domain.location.GeoCoordinates
import com.traffipart.polanty.domain.repository.location.GardenLocationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveGardenLocationUseCase
    @Inject
    constructor(
        private val repository: GardenLocationRepository,
    ) {
        operator fun invoke(): Flow<GeoCoordinates?> = repository.observeLocation()
    }
