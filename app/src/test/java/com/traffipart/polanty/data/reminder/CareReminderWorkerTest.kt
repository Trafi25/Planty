package com.traffipart.polanty.data.reminder

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.google.common.truth.Truth.assertThat
import com.traffipart.polanty.domain.model.care.CareTask
import com.traffipart.polanty.domain.model.care.CareTaskType
import com.traffipart.polanty.domain.model.plant.Plant
import com.traffipart.polanty.domain.repository.care.CareTaskRepository
import com.traffipart.polanty.domain.repository.plant.PlantRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class CareReminderWorkerTest {
    private val tasks = mockk<CareTaskRepository>()
    private val plants = mockk<PlantRepository>()
    private val notifications = mockk<CareNotificationManager>(relaxed = true)
    private val parameters = mockk<WorkerParameters>(relaxed = true)
    private val context = mockk<Context>(relaxed = true)

    private fun worker(): CareReminderWorker {
        every { parameters.inputData } returns workDataOf(CareReminderWorker.KEY_TASK_ID to 1L)
        return CareReminderWorker(context, parameters, tasks, plants, notifications)
    }

    @Test
    fun combinesDueTasksAndExcludesFutureCompletedAndDeletedPlants() =
        runTest {
            val first = task(1, 1)
            val second = task(2, 2)
            coEvery { tasks.getTask(1) } returns first
            every { tasks.observeOpenTasks() } returns
                flowOf(
                    listOf(first, second, task(3, 3, Long.MAX_VALUE), task(4, 4), task(5, 1).copy(isCompleted = true)),
                )
            every { plants.observePlants() } returns
                flowOf(
                    listOf(Plant(1, "Monstera", null, null, null, null), Plant(2, "Fern", null, null, null, null)),
                )

            assertThat(worker().doWork()).isEqualTo(ListenableWorker.Result.success())

            verify(exactly = 1) { notifications.showCareReminder(listOf(first to "Monstera", second to "Fern")) }
        }

    @Test
    fun completedTriggerDoesNotPostNotification() =
        runTest {
            coEvery { tasks.getTask(1) } returns task(1, 1).copy(isCompleted = true)

            assertThat(worker().doWork()).isEqualTo(ListenableWorker.Result.success())

            verify(exactly = 0) { notifications.showCareReminder(any()) }
        }

    @Test
    fun rescheduledFutureTriggerDoesNotPostNotification() =
        runTest {
            coEvery { tasks.getTask(1) } returns task(1, 1, Long.MAX_VALUE)

            assertThat(worker().doWork()).isEqualTo(ListenableWorker.Result.success())

            verify(exactly = 0) { notifications.showCareReminder(any()) }
        }

    private fun task(
        id: Long,
        plantId: Long,
        dueAt: Long = 0,
    ) = CareTask(id, plantId, CareTaskType.CheckSoil, dueAt, false, null, 10)
}
