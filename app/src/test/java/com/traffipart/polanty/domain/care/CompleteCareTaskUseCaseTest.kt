package com.traffipart.polanty.domain.care

import com.google.common.truth.Truth.assertThat
import com.traffipart.polanty.domain.reminder.CareReminderRefresher
import com.traffipart.polanty.domain.reminder.CareReminderScheduler
import com.traffipart.polanty.domain.repository.care.CareTaskRepository
import com.traffipart.polanty.domain.usecase.care.CompleteCareTaskUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.coVerifyOrder
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.Test

class CompleteCareTaskUseCaseTest {
    private val repository = mockk<CareTaskRepository>()
    private val scheduler = mockk<CareReminderScheduler>(relaxed = true)
    private val refresher = mockk<CareReminderRefresher>(relaxed = true)
    private val useCase = CompleteCareTaskUseCase(repository, scheduler, refresher)

    @Test
    fun completionCancelsScheduledWorkAndRefreshesVisibleSummary() =
        runTest {
            coEvery { repository.completeTask(1L, 10L) } returns true

            assertThat(useCase(1L, 10L)).isTrue()

            coVerifyOrder {
                repository.completeTask(1L, 10L)
                scheduler.cancel(1L)
                refresher.refreshVisibleReminder()
            }
        }

    @Test
    fun unchangedTaskDoesNotCancelOrRefresh() =
        runTest {
            coEvery { repository.completeTask(1L, 10L) } returns false

            assertThat(useCase(1L, 10L)).isFalse()

            verify(exactly = 0) { scheduler.cancel(any()) }
            coVerify(exactly = 0) { refresher.refreshVisibleReminder() }
        }
}
