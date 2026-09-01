package com.traffipart.polanty.domain.storage

interface PlantImageStorage {
    suspend fun saveImage(imageUri: String): String

    suspend fun deleteImage(imageUri: String)
}
