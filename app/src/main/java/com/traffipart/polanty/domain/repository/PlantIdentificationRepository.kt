package com.traffipart.polanty.domain.repository

import com.traffipart.polanty.domain.model.PlantIdentification
import com.traffipart.polanty.domain.model.PlantImage

interface PlantIdentificationRepository {
    suspend fun identifyPlant(image: PlantImage): Result<PlantIdentification>
}
