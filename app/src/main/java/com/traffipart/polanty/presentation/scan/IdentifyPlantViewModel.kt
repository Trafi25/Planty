package com.traffipart.polanty.presentation.scan

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.traffipart.polanty.core.common.Result
import com.traffipart.polanty.domain.model.PlantImage
import com.traffipart.polanty.domain.usecase.plant.IdentifyPlantUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import jakarta.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * ViewModel responsible for the plant identification screen.
 * It manages the process of identifying a plant from an image and presenting the results.
 *
 * @property identifyPlantUseCase Use case to perform the plant identification.
 */
@HiltViewModel
class IdentifyPlantViewModel
    @Inject
    constructor(
        private val identifyPlantUseCase: IdentifyPlantUseCase,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(IdentifyPlantUiState())

        /**
         * The current UI state for the plant identification screen.
         */
        val uiState = _uiState.asStateFlow()

        /**
         * Processes user actions from the identification screen.
         *
         * @param action The action to be performed.
         */
        fun onAction(action: IdentifyPlantAction) {
            when (action) {
                is IdentifyPlantAction.IdentifyPlant ->
                    identifyPlant(action.image)
                IdentifyPlantAction.ClearError ->
                    _uiState.update {
                        it.copy(error = null)
                    }
                IdentifyPlantAction.Reset ->
                    _uiState.value = IdentifyPlantUiState()
            }
        }

        /**
         * Triggers the identification process for a given plant image.
         *
         * @param plantImage The image of the plant to identify.
         */
        private fun identifyPlant(plantImage: PlantImage) {
            viewModelScope.launch {
                _uiState.update {
                    it.copy(
                        isLoading = true,
                        identification = null,
                        error = null,
                    )
                }
                when (val result = identifyPlantUseCase(plantImage)) {
                    is Result.Success -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                identification = result.data,
                            )
                        }
                    }
                    is Result.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                error = result.error,
                            )
                        }
                    }
                }
            }
        }
    }
