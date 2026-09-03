package com.traffipart.polanty.domain.repository

import com.traffipart.polanty.core.common.DataError
import com.traffipart.polanty.core.common.Result
import com.traffipart.polanty.domain.model.PlantIdentification
import com.traffipart.polanty.domain.model.PlantImage

/**
 * Repository for identifying plants from images.
 */
interface PlantIdentificationRepository {
    /**
     * Identifies a plant from the provided image.
     *
     * @param image The [PlantImage] to identify.
     * @return A [Result] containing the [PlantIdentification] results or a [DataError].
     */
    suspend fun identifyPlant(image: PlantImage): Result<PlantIdentification, DataError>
}
