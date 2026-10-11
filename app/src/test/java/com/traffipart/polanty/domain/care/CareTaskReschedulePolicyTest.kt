package com.traffipart.polanty.domain.care

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

class CareTaskReschedulePolicyTest {
    private val policy =
        CareTaskReschedulePolicy()

    @Test
    fun smallChangeIsIgnored() {
        val now =
            1_700_000_000_000L

        val currentDueAt = now + 5.days.inWholeMilliseconds

        val recommendedDueAt = currentDueAt + 2.hours.inWholeMilliseconds

        val result =
            policy.shouldReschedule(
                currentDueAt = currentDueAt,
                recommendedDueAt = recommendedDueAt,
                now = now,
            )

        assertThat(result).isFalse()
    }

    @Test
    fun meaningfulChangeIsAccepted() {
        val now =
            1_700_000_000_000L

        val result =
            policy.shouldReschedule(
                currentDueAt = now + 7.days.inWholeMilliseconds,
                recommendedDueAt = now + 9.days.inWholeMilliseconds,
                now = now,
            )

        assertThat(result).isTrue()
    }

    @Test
    fun overdueTaskIsNeverMoved() {
        val now = 1_700_000_000_000L

        val result =
            policy.shouldReschedule(
                currentDueAt = now - 1.days.inWholeMilliseconds,
                recommendedDueAt = now + 2.days.inWholeMilliseconds,
                now = now,
            )

        assertThat(result).isFalse()
    }
}
