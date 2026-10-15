package com.traffipart.polanty.domain.usecase.location

import com.google.common.truth.Truth.assertThat
import com.traffipart.polanty.domain.usecase.care.SyncEnvironmentAwareCarePlansUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test
import java.io.IOException

/**
 * Unit tests for [UpdatePlantHomeLocationUseCase], verifying location saving and
 * environment-aware care plan synchronization behavior.
 */
class UpdatePlantHomeLocationUseCaseTest {
    private val saveCurrentGardenLocationUseCase =
        mockk<SaveCurrentGardenLocationUseCase>()

    private val syncEnvironmentAwareCarePlansUseCase =
        mockk<SyncEnvironmentAwareCarePlansUseCase>()

    private val useCase =
        UpdatePlantHomeLocationUseCase(
            saveCurrentGardenLocationUseCase = saveCurrentGardenLocationUseCase,
            syncEnvironmentAwareCarePlansUseCase = syncEnvironmentAwareCarePlansUseCase,
        )

    @Test
    fun unavailableLocationDoesNotStartCareSync() =
        runTest {
            coEvery {
                saveCurrentGardenLocationUseCase()
            } returns false

            val result = useCase()

            assertThat(result)
                .isEqualTo(
                    UpdatePlantHomeLocationResult.LocationUnavailable,
                )

            coVerify(exactly = 0) {
                syncEnvironmentAwareCarePlansUseCase(any())
            }
        }

    @Test
    fun savedLocationSynchronizesCareImmediately() =
        runTest {
            coEvery {
                saveCurrentGardenLocationUseCase()
            } returns true

            coEvery {
                syncEnvironmentAwareCarePlansUseCase(any())
            } returns 3

            val result = useCase()

            assertThat(result)
                .isEqualTo(
                    UpdatePlantHomeLocationResult.Updated(
                        rescheduledTaskCount = 3,
                    ),
                )

            coVerify(exactly = 1) {
                syncEnvironmentAwareCarePlansUseCase(any())
            }
        }

    @Test
    fun syncFailureKeepsSavedLocationSuccessful() =
        runTest {
            coEvery {
                saveCurrentGardenLocationUseCase()
            } returns true

            coEvery {
                syncEnvironmentAwareCarePlansUseCase(any())
            } throws IOException()

            val result = useCase()

            assertThat(result)
                .isEqualTo(
                    UpdatePlantHomeLocationResult.UpdatedCareSyncDeferred,
                )
        }
}
