package com.traffipart.polanty.domain.care

import com.google.common.truth.Truth.assertThat
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.time.Instant

class DaylightDurationCalculatorTest {
    private val calculator = DaylightDurationCalculator()

    private fun hours(
        latitude: Double,
        date: String,
    ) = calculator.calculateHours(latitude, Instant.parse("${date}T12:00:00Z").toEpochMilli())

    @Test
    fun equatorHasTwelveHourDaysAcrossSeasons() {
        listOf("2026-03-20", "2026-06-21", "2026-09-22", "2026-12-21").forEach {
            assertThat(hours(0.0, it)).isWithin(0.001).of(12.0)
        }
    }

    @Test
    fun berlinHasLongSummerDaysAndShortWinterDays() {
        assertThat(hours(52.52, "2026-06-21")).isWithin(0.5).of(16.5)
        assertThat(hours(52.52, "2026-12-21")).isWithin(0.5).of(7.5)
    }

    @Test
    fun hemispheresHaveOppositeSeasons() {
        val north = hours(52.52, "2026-06-21")
        val south = hours(-52.52, "2026-06-21")
        assertThat(north).isGreaterThan(south)
        assertThat(north + south).isWithin(0.001).of(24.0)
    }

    @Test
    fun polarDayAndNightIncludeThePoles() {
        listOf(80.0, 90.0).forEach {
            assertThat(hours(it, "2026-06-21")).isEqualTo(24.0)
            assertThat(hours(it, "2026-12-21")).isEqualTo(0.0)
            assertThat(hours(-it, "2026-06-21")).isEqualTo(0.0)
            assertThat(hours(-it, "2026-12-21")).isEqualTo(24.0)
        }
    }

    @Test
    fun equinoxAndLeapDayProduceFiniteEstimates() {
        assertThat(hours(52.52, "2026-03-20")).isWithin(0.4).of(12.0)
        assertThat(hours(52.52, "2024-02-29")).isWithin(0.15).of(hours(52.52, "2024-02-28"))
    }

    @Test
    fun rejectsInvalidAndNonFiniteLatitudes() {
        listOf(-90.1, 90.1, Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY).forEach {
            assertThrows<IllegalArgumentException> { hours(it, "2026-06-21") }
        }
    }

    @Test
    fun usesTheUtcCalendarDateRatherThanTimeOfDay() {
        val start = Instant.parse("2026-06-21T00:00:00Z").toEpochMilli()
        val end = Instant.parse("2026-06-21T23:59:59Z").toEpochMilli()
        assertThat(calculator.calculateHours(52.52, start)).isEqualTo(calculator.calculateHours(52.52, end))
    }
}
