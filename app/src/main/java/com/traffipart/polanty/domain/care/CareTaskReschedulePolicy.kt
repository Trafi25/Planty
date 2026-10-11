package com.traffipart.polanty.domain.care

import javax.inject.Inject
import kotlin.math.abs
import kotlin.time.Duration.Companion.hours

/**
 * Policy deciding whether an open care task's due date shift warrants rescheduling.
 *
 * To avoid unnecessary notification rescheduling churn for minor climate fluctuations,
 * a task is only rescheduled if the recommended due date differs from the current due date
 * by at least 12 hours.
 */
class CareTaskReschedulePolicy
    @Inject
    constructor() {
        /**
         * Evaluates whether a care task should be rescheduled given its current due date and recommended due date.
         *
         * @param currentDueAt The task's existing scheduled due date in milliseconds.
         * @param recommendedDueAt The newly calculated recommended due date in milliseconds.
         * @param now Current reference timestamp in milliseconds.
         * @return `true` if the due date shift is at least 12 hours and the current task is not already overdue, `false` otherwise.
         */
        fun shouldReschedule(
            currentDueAt: Long,
            recommendedDueAt: Long,
            now: Long,
        ): Boolean {
            if (currentDueAt <= now) return false

            val effectiveRecommendedDueAt = maxOf(now, recommendedDueAt)
            val difference = abs(effectiveRecommendedDueAt - currentDueAt)

            return difference >= MINIMUM_CHANGE
        }

        private companion object {
            val MINIMUM_CHANGE = 12.hours.inWholeMilliseconds
        }
    }
