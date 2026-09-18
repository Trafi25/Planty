package com.traffipart.polanty.data.repository.plant

import android.content.Context
import android.util.Log
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.traffipart.polanty.domain.repository.plant.SpaceInitializationRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.spaceDataStore by preferencesDataStore(
    name = "space_preferences",
)

class SpaceInitializationRepositoryImpl
    @Inject
    constructor(
        @ApplicationContext
        private val context: Context,
    ) : SpaceInitializationRepository {
        override suspend fun isInitialized(): Boolean =
            context.spaceDataStore.data
                .map { preferences ->
                    preferences[SPACE_INITIALIZED_KEY] ?: false
                }.first()

        override suspend fun markInitialized() {
            try {
                context.spaceDataStore.edit { preferences -> preferences[SPACE_INITIALIZED_KEY] = true }
            } catch (e: Exception) {
                Log.e("SpaceInitRepo", "Error marking initialized", e)
                throw e
            }
        }

        private companion object {
            val SPACE_INITIALIZED_KEY = booleanPreferencesKey("space_initialized")
        }
    }
