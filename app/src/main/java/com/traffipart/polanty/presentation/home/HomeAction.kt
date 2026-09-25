package com.traffipart.polanty.presentation.home

/**
 * User interface actions that can be triggered on the Home screen.
 */
sealed interface HomeAction {
    /**
     * Action dispatched when a user marks a care task as complete.
     *
     * @property task The care task UI model being completed.
     */
    data class CompleteCareTask(
        val task: HomeCareTaskUiModel,
    ) : HomeAction

    data class SoilCheckResult(
        val task: HomeCareTaskUiModel,
        val soilIsDry: Boolean,
    ) : HomeAction
}
