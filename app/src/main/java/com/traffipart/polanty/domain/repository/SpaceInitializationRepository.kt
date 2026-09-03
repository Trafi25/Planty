package com.traffipart.polanty.domain.repository

/**
 * Repository for tracking the initialization status of plant spaces.
 */
interface SpaceInitializationRepository {
    /**
     * Checks if the default spaces have been initialized.
     *
     * @return `true` if initialized, `false` otherwise.
     */
    suspend fun isInitialized(): Boolean

    /**
     * Marks the application as having its default spaces initialized.
     */
    suspend fun markInitialized()
}
