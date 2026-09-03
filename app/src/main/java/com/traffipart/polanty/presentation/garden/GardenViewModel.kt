package com.traffipart.polanty.presentation.garden

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.traffipart.polanty.domain.model.PlantSpaceType
import com.traffipart.polanty.domain.usecase.CreateSpaceUseCase
import com.traffipart.polanty.domain.usecase.InitializeDefaultSpacesUseCase
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

/**
 * Represents the state of a space creation operation.
 *
 * @property isAdding Whether a space is currently being added.
 * @property error An optional error message if the space creation failed.
 */
data class SpaceCreationState(
    val isAdding: Boolean = false,
    val error: String? = null,
)

/**
 * ViewModel responsible for managing the garden screen.
 * It handles observing plants and spaces, as well as creating new spaces.
 *
 * @property observePlantsUseCase Use case to observe the list of all plants.
 * @property observeSpacesUseCase Use case to observe the list of all plant spaces.
 * @property createSpaceUseCase Use case to create a new plant space.
 * @property initializeDefaultSpacesUseCase Use case to initialize the default plant spaces if they don't exist.
 */
@HiltViewModel
class GardenViewModel
    @Inject
    constructor(
        private val observePlantsUseCase: ObservePlantsUseCase,
        private val observeSpacesUseCase: ObserveSpacesUseCase,
        private val createSpaceUseCase: CreateSpaceUseCase,
        private val initializeDefaultSpacesUseCase: InitializeDefaultSpacesUseCase,
    ) : ViewModel() {
        init {
            initializeDefaultSpaces()
        }

        private val spaceCreationState = MutableStateFlow(SpaceCreationState())

        /**
         * The UI state for the garden screen, combining plants, spaces, and space creation status.
         */
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

        /**
         * Processes user actions from the garden screen.
         *
         * @param action The action to be performed.
         */
        fun onAction(action: GardenAction) {
            when (action) {
                is GardenAction.AddSpace -> {
                    addSpace(type = action.type, customName = action.customName)
                }
                GardenAction.ClearAddSpaceError -> {
                    spaceCreationState.update { it.copy(error = null) }
                }
            }
        }

        private fun initializeDefaultSpaces() {
            viewModelScope.launch {
                initializeDefaultSpacesUseCase()
            }
        }

        /**
         * Adds a new plant space.
         *
         * @param type The type of the space to add.
         * @param customName An optional custom name for the space.
         */
        private fun addSpace(
            type: PlantSpaceType,
            customName: String?,
        ) {
            viewModelScope.launch {
                spaceCreationState.update {
                    it.copy(isAdding = true, error = null)
                }

                try {
                    createSpaceUseCase(type = type, customName = customName)
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
