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

    /**
     * Action dispatched when a user submits a soil check result.
     *
     * @property task The care task UI model.
     * @property soilIsDry Whether the soil was determined to be dry.
     */
    data class SoilCheckResult(
        val task: HomeCareTaskUiModel,
        val soilIsDry: Boolean,
    ) : HomeAction

    /**
     * Action dispatched when POST_NOTIFICATIONS permission is granted to trigger rescheduling care reminders.
     */
    data object NotificationPermissionGranted :
        HomeAction

    data object SaveCurrentGardenLocation : HomeAction
}
