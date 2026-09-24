package com.traffipart.polanty.presentation.home

import com.traffipart.polanty.domain.model.CareTaskType

/**
 * UI state for the Home screen.
 *
 * @property plantCount Total number of plants in the garden.
 * @property spaceCount Total number of defined plant spaces.
 * @property isLoading Whether the data is currently being loaded.
 */
data class HomeUiState(
    val plantCount: Int = 0,
    val spaceCount: Int = 0,
    val isLoading: Boolean = true,
    val careTasks: List<HomeCareTaskUiModel> = emptyList(),
)

data class HomeCareTaskUiModel(
    val taskId: Long,
    val plantId: Long,
    val plantName: String,
    val scientificName: String,
    val type: CareTaskType,
    val dueAt: Long,
)
