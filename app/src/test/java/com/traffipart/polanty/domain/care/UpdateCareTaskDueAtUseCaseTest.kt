package com.traffipart.polanty.domain.care

import com.google.common.truth.Truth.assertThat
import com.traffipart.polanty.domain.reminder.CareReminderScheduler
import com.traffipart.polanty.domain.repository.care.CareTaskRepository
import com.traffipart.polanty.domain.usecase.care.UpdateCareTaskDueAtUseCase
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class UpdateCareTaskDueAtUseCaseTest {
    private val repository =
        mockk<CareTaskRepository>()

    private val scheduler =
        mockk<CareReminderScheduler>(
            relaxed = true,
        )

    private val useCase =
        UpdateCareTaskDueAtUseCase(
            careTaskRepository = repository,
            careReminderScheduler = scheduler,
        )

    @Test
    fun successfulUpdateReschedulesReminder() =
        runTest {
            coEvery {
                repository.updateDueAt(
                    taskId = 10L,
                    dueAt = 20_000L,
                )
            } returns true

            val result =
                useCase(
                    taskId = 10L,
                    dueAt = 20_000L,
                )

            assertThat(result)
                .isTrue()

            verify {
                scheduler.schedule(
                    taskId = 10L,
                    dueAt = 20_000L,
                )
            }
        }

    @Test
    fun failedUpdateDoesNotScheduleReminder() =
        runTest {
            coEvery {
                repository.updateDueAt(
                    taskId = 10L,
                    dueAt = 20_000L,
                )
            } returns false

            val result =
                useCase(
                    taskId = 10L,
                    dueAt = 20_000L,
                )

            assertThat(result)
                .isFalse()

            verify(exactly = 0) {
                scheduler.schedule(
                    any(),
                    any(),
                )
            }
        }
}
