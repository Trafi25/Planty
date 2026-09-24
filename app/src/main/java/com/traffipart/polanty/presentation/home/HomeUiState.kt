package com.traffipart.polanty.presentation.home

import com.traffipart.polanty.domain.model.CareTaskType

/**
 * UI state for the Home screen.
 *
 * @property plantCount Total number of plants in the garden.
 * @property spaceCount Total number of defined plant spaces.
 * @property isLoading Whether the data is currently being loaded.
 * @property careTasks List of care tasks due today or pending completion.
 */
data class HomeUiState(
    val plantCount: Int = 0,
    val spaceCount: Int = 0,
    val isLoading: Boolean = true,
    val careTasks: List<HomeCareTaskUiModel> = emptyList(),
)

/**
 * UI model representing a care task displayed on the home screen task list.
 *
 * @property taskId Unique ID of the care task.
 * @property plantId Unique ID of the plant.
 * @property plantName User-facing display name or nickname of the plant.
 * @property scientificName Botanical scientific name of the plant.
 * @property type The [CareTaskType] of the task.
 * @property dueAt Due timestamp in milliseconds.
 */
data class HomeCareTaskUiModel(
    val taskId: Long,
    val plantId: Long,
    val plantName: String,
    val scientificName: String,
    val type: CareTaskType,
    val dueAt: Long,
)
