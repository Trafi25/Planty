package com.traffipart.polanty.domain.repository

interface SpaceInitializationRepository {
    suspend fun isInitialized(): Boolean

    suspend fun markInitialized()
}
