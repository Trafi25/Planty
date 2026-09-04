package com.traffipart.polanty.presentation.garden

sealed interface GardenEvent {
    data object SpaceCreated : GardenEvent

    data object SpaceDeleted : GardenEvent
}
