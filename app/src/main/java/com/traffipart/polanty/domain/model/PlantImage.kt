package com.traffipart.polanty.domain.model

data class PlantImage(
    val bytes: ByteArray,
    val fileName: String,
    val mimeType: String,
)
