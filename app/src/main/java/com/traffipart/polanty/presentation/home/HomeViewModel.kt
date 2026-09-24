package com.traffipart.polanty.presentation.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.traffipart.polanty.domain.usecase.care.CompleteCareTaskUseCase
import com.traffipart.polanty.domain.usecase.care.ObserveDueCareTasksUseCase
import com.traffipart.polanty.domain.usecase.care.RefreshPlantCarePlanUseCase
import com.traffipart.polanty.domain.usecase.plant.ObservePlantsUseCase
import com.traffipart.polanty.domain.usecase.space.ObserveSpacesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

/**
 * ViewModel for the Home screen.
 *
 * It observes the collection of plants and spaces to provide high-level statistics
 * and dashboard information to the user.
 *
 * @property observePlantsUseCase Use case to observe all plants.
 * @property observeSpacesUseCase Use case to observe all plant spaces.
 */
@HiltViewModel
class HomeViewModel
    @Inject
    constructor(
        private val observePlantsUseCase: ObservePlantsUseCase,
        private val observeSpacesUseCase: ObserveSpacesUseCase,
        private val observeDueCareTasksUseCase: ObserveDueCareTasksUseCase,
        private val completeCareTaskUseCase: CompleteCareTaskUseCase,
        private val refreshPlantCarePlanUseCase: RefreshPlantCarePlanUseCase,
    ) : ViewModel() {
        /**
         * The UI state for the Home screen, providing plant and space counts.
         */
        val uiState: StateFlow<HomeUiState> =
            combine(
                observePlantsUseCase(),
                observeSpacesUseCase(),
                observeDueCareTasksUseCase(),
            ) { plants, spaces, careTasks ->
                val plantsById = plants.associateBy { plant -> plant.id }
                val taskUiModels =
                    careTasks.mapNotNull { task ->
                        val plant = plantsById[task.plantId] ?: return@mapNotNull null
                        HomeCareTaskUiModel(
                            taskId = task.id,
                            plantId = task.plantId,
                            plantName = plant.displayName,
                            scientificName =
                                plant.scientificName,
                            type = task.type,
                            dueAt = task.dueAt,
                        )
                    }

                HomeUiState(
                    plantCount = plants.size,
                    spaceCount = spaces.size,
                    isLoading = false,
                    careTasks = taskUiModels,
                )
            }.stateIn(
                scope = viewModelScope,
                started = SharingStarted.WhileSubscribed(5_000),
                initialValue = HomeUiState(),
            )

        fun onAction(action: HomeAction) {
            when (action) {
                is HomeAction.CompleteCareTask -> {
                    completeCareTask(action.task)
                }
            }
        }

        private fun completeCareTask(task: HomeCareTaskUiModel) {
            viewModelScope.launch {
                val completed = completeCareTaskUseCase(taskId = task.taskId)
                if (!completed) {
                    return@launch
                }
                try {
                    refreshPlantCarePlanUseCase(plantId = task.plantId, scientificName = task.scientificName)
                } catch (
                    e: CancellationException,
                ) {
                    throw e
                } catch (
                    e: Exception,
                ) {
                    Log.e("HomeViewModel", "Failed to refresh care plan", e)
                }
            }
        }
    }
