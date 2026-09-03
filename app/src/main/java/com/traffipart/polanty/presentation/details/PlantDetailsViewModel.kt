package com.traffipart.polanty.presentation.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.traffipart.polanty.domain.usecase.DeletePlantUseCase
import com.traffipart.polanty.domain.usecase.ObservePlantUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel responsible for displaying and managing the details of a specific plant.
 *
 * @param savedStateHandle Handle to saved state, used to retrieve the [plantId].
 * @property observePlantUseCase Use case to observe a specific plant by its ID.
 * @property deletePlantUseCase Use case to delete a plant.
 */
@HiltViewModel
class PlantDetailsViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        private val observePlantUseCase: ObservePlantUseCase,
        private val deletePlantUseCase: DeletePlantUseCase,
    ) : ViewModel() {
        private val plantId: Long = checkNotNull(savedStateHandle.get<Long>("plantId"))

        private val _uiState = MutableStateFlow(PlantDetailsUiState())

        /**
         * The current UI state for the plant details screen.
         */
        val uiState: StateFlow<PlantDetailsUiState> = _uiState.asStateFlow()

        init {
            observePlantUseCase(plantId)
                .onEach { plant ->
                    _uiState.update {
                        it.copy(
                            plant = plant,
                            isLoading = false,
                        )
                    }
                }.launchIn(viewModelScope)
        }

        /**
         * Processes user actions from the plant details screen.
         *
         * @param action The action to be performed.
         */
        fun onAction(action: PlantDetailsAction) {
            when (action) {
                PlantDetailsAction.DeletePlant -> deletePlant()
            }
        }

        /**
         * Triggers the deletion of the current plant.
         */
        private fun deletePlant() {
            val plant = _uiState.value.plant ?: return
            viewModelScope.launch {
                _uiState.update {
                    it.copy(
                        isDeleting = true,
                        errorMessage = null,
                    )
                }
                try {
                    deletePlantUseCase(plant)
                    _uiState.update {
                        it.copy(
                            isDeleting = false,
                            isDeleted = true,
                        )
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    _uiState.update {
                        it.copy(
                            isDeleting = false,
                            errorMessage = "Could not delete plant",
                        )
                    }
                }
            }
        }
    }
