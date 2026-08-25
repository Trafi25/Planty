package com.traffipart.polanty.core

data class Plant(
    val id: Long,
    val scientificName: String,
    val commonName: String?,
    val nickname: String?,
    val spaceId: Long?,
    val imageUri: String?,
)