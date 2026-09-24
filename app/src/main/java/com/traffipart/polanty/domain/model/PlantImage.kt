package com.traffipart.polanty.domain.model

/**
 * Encapsulates image binary data for plant identification calls.
 *
 * @property bytes The raw image byte data.
 * @property fileName The source image file name.
 * @property mimeType Image MIME type (e.g., `image/jpeg` or `image/png`).
 */
data class PlantImage(
    val bytes: ByteArray,
    val fileName: String,
    val mimeType: String,
)
