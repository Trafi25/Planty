package com.traffipart.polanty.core.repository

import com.traffipart.polanty.core.model.PlantIdentification
import com.traffipart.polanty.core.model.PlantImage

interface PlantIdentificationRepository {
    suspend fun identifyPlant(image: PlantImage): Result<PlantIdentification>
}
