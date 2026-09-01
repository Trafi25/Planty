package com.traffipart.polanty.presentation.setup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.traffipart.polanty.domain.model.Plant
import com.traffipart.polanty.domain.model.PlantCandidate
import com.traffipart.polanty.domain.usecase.SavePlantUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

@HiltViewModel
class PlantSetupViewModel
    @Inject
    constructor(
        private val savePlantUseCase: SavePlantUseCase,
    ) : ViewModel() {
        private val _uiState = MutableStateFlow(PlantSetupUiState())
        val uiState = _uiState.asStateFlow()

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
