package com.traffipart.polanty.presentation.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.traffipart.polanty.domain.model.Plant
import com.traffipart.polanty.domain.model.PlantCandidate
import com.traffipart.polanty.domain.usecase.ObserveSpacesUseCase
import com.traffipart.polanty.domain.usecase.SavePlantUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

/**
 * ViewModel responsible for the plant setup screen.
 * It handles the initial configuration of a plant before it is saved to the user's garden.
 *
 * @property savePlantUseCase Use case to save a new plant to the repository.
 * @property observeSpacesUseCase Use case to observe the list of available plant spaces.
 */
@HiltViewModel
class PlantSetupViewModel
    @Inject
    constructor(
        private val savePlantUseCase: SavePlantUseCase,
        private val observeSpacesUseCase: ObserveSpacesUseCase,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(PlantSetupUiState())

        /**
         * The current UI state for the plant setup screen.
         */
        val uiState = _uiState.asStateFlow()

        init {
            observeSpaces()
        }

        /**
         * Observes the available spaces and updates the UI state.
         */
        private fun observeSpaces() {
            observeSpacesUseCase()
                .onEach { spaces ->
                    _uiState.update {
                        it.copy(
                            spaces = spaces,
                        )
                    }
                }.launchIn(viewModelScope)
        }

        /**
         * Processes user actions from the plant setup screen.
         *
         * @param action The action to be performed.
         */
        fun onAction(action: PlantSetupAction) {
            when (action) {
                is PlantSetupAction.Initialize -> {
                    initialize(
                        candidate = action.candidate,
                        imageUri = action.imageUri,
                    )
                }
                is PlantSetupAction.NicknameChanged -> {
                    _uiState.update {
                        it.copy(
                            nickname = action.nickname,
                        )
                    }
                }
                is PlantSetupAction.SpaceIdSelected -> {
                    _uiState.update {
                        it.copy(
                            spaceId = action.spaceId,
                        )
                    }
                }
                is PlantSetupAction.SavePlant -> {
                    savePlant()
                }
            }
        }

        /**
         * Saves the plant to the garden based on the current UI state.
         */
        private fun savePlant() {
            val state = _uiState.value
            val candidate = state.candidate ?: return

            viewModelScope.launch {
                _uiState.update {
                    it.copy(
                        isSaving = true,
                        saveError = false,
                    )
                }

                try {
                    val plant =
                        Plant(
                            scientificName = candidate.scientificName,
                            commonName = candidate.commonName,
                            nickname = state.nickname.trim().takeIf { it.isNotEmpty() },
                            spaceId = state.spaceId,
                            imageUri = null,
                        )
                    val plantId = savePlantUseCase(plant = plant, sourceImageUri = state.imageUri)
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            savedPlantId = plantId,
                        )
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    _uiState.update {
                        it.copy(
                            isSaving = false,
                            saveError = true,
                        )
                    }
                }
            }
        }

        /**
         * Initializes the ViewModel with a plant candidate and an image URI.
         *
         * @param candidate The plant candidate to initialize with.
         * @param imageUri The optional image URI to initialize with.
         */
        private fun initialize(
            candidate: PlantCandidate,
            imageUri: String?,
        ) {
            if (_uiState.value.candidate != null) {
                return
            }
            _uiState.update {
                it.copy(
                    candidate = candidate,
                    imageUri = imageUri,
                )
            }
        }
    }
