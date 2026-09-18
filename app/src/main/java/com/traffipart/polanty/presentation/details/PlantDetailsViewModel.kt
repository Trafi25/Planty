package com.traffipart.polanty.presentation.details

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.traffipart.polanty.core.common.trimToNull
import com.traffipart.polanty.domain.usecase.plant.DeletePlantUseCase
import com.traffipart.polanty.domain.usecase.plant.GetPlantKnowledgeUseCase
import com.traffipart.polanty.domain.usecase.plant.ObservePlantUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
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
        private val getPlantKnowledgeUseCase: GetPlantKnowledgeUseCase,
    ) : ViewModel() {
        private val plantId: Long = checkNotNull(savedStateHandle.get<Long>("plantId"))

        private val _uiState = MutableStateFlow(PlantDetailsUiState())

        /**
         * The current UI state for the plant details screen.
         */
        val uiState: StateFlow<PlantDetailsUiState> = _uiState.asStateFlow()

        init {
            observePlant()
        }

        private fun observePlant() {
            observePlantUseCase(plantId)
                .onEach { plant ->
                    _uiState.update {
                        it.copy(
                            plant = plant,
                            isLoading = false,
                        )
                    }
                }.filterNotNull()
                .map { it.scientificName.trimToNull() }
                .filterNotNull()
                .distinctUntilChanged()
                .onEach { scientificName -> loadPlantKnowledge(scientificName) }
                .launchIn(viewModelScope)
        }

        /**
         * Loads botanical and care knowledge for a specific scientific name.
         *
         * @param scientificName The scientific name of the plant to fetch knowledge for.
         */
        suspend fun loadPlantKnowledge(scientificName: String) {
            _uiState.update {
                it.copy(
                    knowledgeState = PlantKnowledgeUiState.Loading,
                )
            }
            try {
                val knowledge = getPlantKnowledgeUseCase(scientificName)
                _uiState.update {
                    it.copy(
                        knowledgeState =
                            if (knowledge != null) {
                                PlantKnowledgeUiState.Available(knowledge)
                            } else {
                                PlantKnowledgeUiState.Unavailable
                            },
                    )
                }
            } catch (
                e: CancellationException,
            ) {
                throw e
            } catch (
                e: Exception,
            ) {
                Log.e("PlantDetails", "Care info error: ${e.message}", e)
                _uiState.update {
                    it.copy(
                        knowledgeState = PlantKnowledgeUiState.Error("Could not load care information."),
                    )
                }
            }
        }

        /**
         * Processes user actions from the plant details screen.
         *
         * @param action The action to be performed.
         */
        fun onAction(action: PlantDetailsAction) {
            when (action) {
                PlantDetailsAction.DeletePlant -> deletePlant()
                PlantDetailsAction.RetryKnowledge -> {
                    retryPlantKnowledge()
                }
            }
        }

        /**
         * Retries the plant knowledge retrieval for the current plant.
         */
        private fun retryPlantKnowledge() {
            val scientificName = _uiState.value.plant?.scientificName ?: return

            viewModelScope.launch {
                loadPlantKnowledge(scientificName)
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
