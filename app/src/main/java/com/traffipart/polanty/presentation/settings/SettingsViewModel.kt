package com.traffipart.polanty.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.traffipart.polanty.domain.usecase.location.ClearGardenLocationUseCase
import com.traffipart.polanty.domain.usecase.location.ObserveGardenLocationUseCase
import com.traffipart.polanty.domain.usecase.location.SaveCurrentGardenLocationUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class SettingsViewModel
    @Inject
    constructor(
        observeGardenLocationUseCase: ObserveGardenLocationUseCase,
        private val saveCurrentGardenLocationUseCase: SaveCurrentGardenLocationUseCase,
        private val clearGardenLocationUseCase: ClearGardenLocationUseCase,
    ) : ViewModel() {
        private val isSavingLocation = MutableStateFlow(false)

        private val locationErrorMessage = MutableStateFlow<String?>(null)

        val uiState =
            combine(
                observeGardenLocationUseCase(),
                isSavingLocation,
                locationErrorMessage,
            ) {
                    location,
                    isSaving,
                    errorMessage,
                ->

                SettingsUiState(
                    hasPlantHomeLocation =
                        location != null,
                    isSavingLocation =
                    isSaving,
                    locationErrorMessage =
                    errorMessage,
                )
            }.stateIn(
                scope = viewModelScope,
                started =
                    SharingStarted.WhileSubscribed(
                        5_000,
                    ),
                initialValue =
                    SettingsUiState(),
            )

        fun onAction(action: SettingsAction) {
            when (action) {
                SettingsAction
                    .SaveCurrentPlantHomeLocation,
                -> {
                    savePlantHomeLocation()
                }

                SettingsAction
                    .ClearPlantHomeLocation,
                -> {
                    clearPlantHomeLocation()
                }
            }
        }

        private fun savePlantHomeLocation() {
            viewModelScope.launch {
                if (isSavingLocation.value) return@launch
                isSavingLocation.value = true
                locationErrorMessage.value = null

                try {
                    val saved = saveCurrentGardenLocationUseCase()
                    if (!saved) {
                        locationErrorMessage.value = "Unable to determine location."
                    }
                } catch (
                    e: CancellationException,
                ) {
                    throw e
                } catch (
                    _: Exception,
                ) {
                    locationErrorMessage.value = "Failed to save location."
                } finally {
                    isSavingLocation.value = false
                }
            }
        }

        private fun clearPlantHomeLocation() {
            viewModelScope.launch {
                try {
                    clearGardenLocationUseCase()
                    locationErrorMessage.value = null
                } catch (
                    e: CancellationException,
                ) {
                    throw e
                } catch (
                    _: Exception,
                ) {
                    locationErrorMessage.value = "Failed to clear location."
                }
            }
        }
    }
