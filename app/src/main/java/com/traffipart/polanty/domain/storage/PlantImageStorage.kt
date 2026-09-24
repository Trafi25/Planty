package com.traffipart.polanty.domain.storage

/**
 * Service interface for persisting and removing local plant image files.
 */
interface PlantImageStorage {
    /**
     * Persists an image from a source URI to local app storage.
     *
     * @param imageUri Source image URI string.
     * @return Local target URI string where the file is stored.
     */
    suspend fun saveImage(imageUri: String): String

    /**
     * Deletes a stored plant image file given its local URI string.
     *
     * @param imageUri Target image URI string to remove.
     */
    suspend fun deleteImage(imageUri: String)
}
