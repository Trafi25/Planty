package com.traffipart.polanty.data.repository

interface SpaceInitializationRepository {
    suspend fun isInitialized(): Boolean

    suspend fun markInitialized()
}
