package com.traffipart.polanty.presentation.garden

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.traffipart.polanty.domain.usecase.CreateSpaceUseCase
import com.traffipart.polanty.domain.usecase.ObservePlantsUseCase
import com.traffipart.polanty.domain.usecase.ObserveSpacesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class SpaceCreationState(
    val isAdding: Boolean = false,
    val error: String? = null,
)

@HiltViewModel
class GardenViewModel
    @Inject
    constructor(
        private val observePlantsUseCase: ObservePlantsUseCase,
        private val observeSpacesUseCase: ObserveSpacesUseCase,
        private val createCustomSpaceUseCase: CreateSpaceUseCase,
    ) : ViewModel() {
        private val spaceCreationState = MutableStateFlow(SpaceCreationState())
        val uiState =
            combine(
                observePlantsUseCase(),
                observeSpacesUseCase(),
                spaceCreationState,
            ) { plants, spaces, creationState ->
                GardenUiState(
                    plants = plants,
                    spaces = spaces,
                    isLoading = false,
                    isAddingSpace = creationState.isAdding,
                    addSpaceError = creationState.error,
                )
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = GardenUiState(),
            )

        fun onAction(action: GardenAction) {
            when (action) {
                is GardenAction.AddCustomSpace -> {
                    addCustomSpace(action.name)
                }
                GardenAction.ClearAddSpaceError -> {
                    spaceCreationState.update { it.copy(error = null) }
                }
            }
        }

        private fun addCustomSpace(name: String) {
            viewModelScope.launch {
                spaceCreationState.update {
                    it.copy(isAdding = true, error = null)
                }

                try {
                    createCustomSpaceUseCase(name)
                    spaceCreationState.update {
                        it.copy(isAdding = false)
                    }
                } catch (
                    e: CancellationException,
                ) {
                    throw e
                } catch (
                    e: IllegalArgumentException,
                ) {
                    spaceCreationState.update {
                        it.copy(isAdding = false, error = e.message ?: "Invalid space")
                    }
                } catch (e: Exception) {
                    spaceCreationState.update {
                        it.copy(isAdding = false, error = "Could not add space")
                    }
                }
            }
        }
    }
