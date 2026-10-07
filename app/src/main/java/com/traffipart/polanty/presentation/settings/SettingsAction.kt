package com.traffipart.polanty.presentation.settings

sealed interface SettingsAction {
    data object SaveCurrentPlantHomeLocation :
        SettingsAction

    data object ClearPlantHomeLocation :
        SettingsAction
}
