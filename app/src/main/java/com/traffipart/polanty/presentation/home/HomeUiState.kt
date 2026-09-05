package com.traffipart.polanty.presentation.home

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
)
