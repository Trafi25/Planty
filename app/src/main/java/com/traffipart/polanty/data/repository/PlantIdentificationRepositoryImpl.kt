package com.traffipart.polanty.data.repository

import com.squareup.moshi.JsonDataException
import com.traffipart.polanty.core.common.DataError
import com.traffipart.polanty.core.common.Result
import com.traffipart.polanty.data.mapper.toDomain
import com.traffipart.polanty.data.remote.plant.PlantNetApi
import com.traffipart.polanty.domain.model.PlantIdentification
import com.traffipart.polanty.domain.model.PlantImage
import com.traffipart.polanty.domain.repository.PlantIdentificationRepository
import jakarta.inject.Inject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import kotlin.coroutines.cancellation.CancellationException

class PlantIdentificationRepositoryImpl
    @Inject
    constructor(
        private val plantNetApi: PlantNetApi,
    ) : PlantIdentificationRepository {
        override suspend fun identifyPlant(image: PlantImage): Result<PlantIdentification, DataError> =
            try {
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

                val response =
                    plantNetApi
                        .identifyPlant(
                            image = multipart,
                        ).toDomain()

                Result.Success(response)
            } catch (e: CancellationException) {
                throw e
            } catch (e: SocketTimeoutException) {
                Result.Error(DataError.RequestTimeout)
            } catch (e: UnknownHostException) {
                Result.Error(
                    DataError.NoInternet,
                )
            } catch (e: HttpException) {
                when (e.code()) {
                    401, 403 ->
                        Result.Error(DataError.Unauthorized)

                    429 ->
                        Result.Error(DataError.TooManyRequests)

                    in 500..599 ->
                        Result.Error(DataError.ServerError)

                    else ->
                        Result.Error(DataError.Unknown)
                }
            } catch (e: JsonDataException) {
                Result.Error(
                    DataError.Serialization,
                )
            } catch (e: Exception) {
                Result.Error(
                    DataError.Unknown,
                )
            }
    }
