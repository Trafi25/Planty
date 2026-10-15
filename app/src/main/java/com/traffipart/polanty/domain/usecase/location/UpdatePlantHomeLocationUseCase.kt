package com.traffipart.polanty.domain.usecase.location

import com.traffipart.polanty.domain.usecase.care.SyncEnvironmentAwareCarePlansUseCase
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

sealed interface UpdatePlantHomeLocationResult {
    data object LocationUnavailable :
        UpdatePlantHomeLocationResult

    data class Updated(
        val rescheduledTaskCount: Int,
    ) : UpdatePlantHomeLocationResult

    data object UpdatedCareSyncDeferred :
        UpdatePlantHomeLocationResult
}

class UpdatePlantHomeLocationUseCase
    @Inject
    constructor(
        private val saveCurrentGardenLocationUseCase: SaveCurrentGardenLocationUseCase,
        private val syncEnvironmentAwareCarePlansUseCase: SyncEnvironmentAwareCarePlansUseCase,
    ) {
        suspend operator fun invoke(): UpdatePlantHomeLocationResult {
            val saved = saveCurrentGardenLocationUseCase()

            if (!saved) return UpdatePlantHomeLocationResult.LocationUnavailable

            return try {
                val updatedTasks = syncEnvironmentAwareCarePlansUseCase()
                UpdatePlantHomeLocationResult.Updated(
                    rescheduledTaskCount =
                    updatedTasks,
                )
            } catch (e: CancellationException) {
                throw e
            } catch (
                _: Exception,
            ) {
                UpdatePlantHomeLocationResult.UpdatedCareSyncDeferred
            }
        }
    }
