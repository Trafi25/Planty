package com.traffipart.polanty.data.repository

import com.traffipart.polanty.core.model.PlantIdentification
import com.traffipart.polanty.core.model.PlantImage
import com.traffipart.polanty.core.repository.PlantIdentificationRepository
import com.traffipart.polanty.data.mapper.toDomain
import com.traffipart.polanty.data.remote.plant.PlantNetApi
import jakarta.inject.Inject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class PlantIdentificationRepositoryImpl
    @Inject
    constructor(
        private val plantNetApi: PlantNetApi,
    ) : PlantIdentificationRepository {
        override suspend fun identifyPlant(image: PlantImage): Result<PlantIdentification> =
            runCatching {
                val requestBody =
                    image.bytes.toRequestBody(
                        image.mimeType.toMediaType(),
                    )

                val multipart =
                    MultipartBody.Part.createFormData(
                        name = "images",
                        filename = image.fileName,
                        body = requestBody,
                    )

                plantNetApi
                    .identifyPlant(
                        image = multipart,
                    ).toDomain()
            }
    }
