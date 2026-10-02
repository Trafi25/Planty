package com.traffipart.polanty.domain.care

import com.traffipart.polanty.domain.model.care.CareTask
import com.traffipart.polanty.domain.model.care.CareTaskType
import com.traffipart.polanty.domain.reminder.CareReminderScheduler
import com.traffipart.polanty.domain.repository.care.CareTaskRepository
import com.traffipart.polanty.domain.usecase.care.RescheduleOpenCareRemindersUseCase
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class RescheduleOpenCareRemindersUseCaseTest {
    private val repository =
        mockk<CareTaskRepository>()

    private val scheduler =
        mockk<CareReminderScheduler>(
            relaxed = true,
        )

    private val useCase =
        RescheduleOpenCareRemindersUseCase(
            careTaskRepository = repository,
            reminderScheduler = scheduler,
        )

    @Test
    fun reschedulesReminderForEveryOpenTask() =
        runTest {
            val firstTask =
                CareTask(
                    id = 10L,
                    plantId = 1L,
                    type = CareTaskType.CheckSoil,
                    dueAt = 1000L,
                    isCompleted = false,
                    completedAt = null,
                    xpReward = 10,
                )

            val secondTask =
                CareTask(
                    id = 11L,
                    plantId = 2L,
                    type = CareTaskType.Water,
                    dueAt = 2000L,
                    isCompleted = false,
                    completedAt = null,
                    xpReward = 10,
                )

            every {
                repository.observeOpenTasks()
            } returns
                flowOf(
                    listOf(
                        firstTask,
                        secondTask,
                    ),
                )

            useCase()

            verify {
                scheduler.schedule(
                    taskId = 10L,
                    dueAt = 1000L,
                )
            }

            verify {
                scheduler.schedule(
                    taskId = 11L,
                    dueAt = 2000L,
                )
            }
        }
}
