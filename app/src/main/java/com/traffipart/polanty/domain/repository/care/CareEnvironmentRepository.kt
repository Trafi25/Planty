package com.traffipart.polanty.domain.repository.care

import com.traffipart.polanty.domain.model.care.CareEnvironment

interface CareEnvironmentRepository {
    suspend fun getCurrentEnvironment(
        latitude: Double,
        longitude: Double,
    ): CareEnvironment?
}
