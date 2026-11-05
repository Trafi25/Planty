package com.traffipart.polanty.domain.care

import com.google.common.truth.Truth.assertThat
import com.traffipart.polanty.domain.model.knowledge.WateringProfile
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

/**
 * Unit tests for [SoilCheckPhotoperiodAdjuster], verifying interval adjustments based on seasonal photoperiod.
 */
class SoilCheckPhotoperiodAdjusterTest {
    @Test
    fun thresholdsAreInclusiveAndNeutralDaysKeepInterval() {
        assertThat(adjuster.adjustDays(8, watering, 9.0)).isEqualTo(9)
        assertThat(adjuster.adjustDays(8, watering, 9.01)).isEqualTo(8)
        assertThat(adjuster.adjustDays(8, watering, 13.99)).isEqualTo(8)
        assertThat(adjuster.adjustDays(8, watering, 14.0)).isEqualTo(7)
    }

    @Test
    fun winterAdjustmentCannotExceedMaximum() {
        assertThat(adjuster.adjustDays(10, watering, 0.0)).isEqualTo(10)
        assertThat(adjuster.adjustDays(7, watering, 24.0)).isEqualTo(7)
    }

    @Test
    fun fixedSpeciesIntervalDoesNotChange() {
        val fixed = watering.copy(soilCheckIntervalDaysMax = 7)
        listOf(null, 0.0, 12.0, 24.0).forEach {
            assertThat(adjuster.adjustDays(7, fixed, it)).isEqualTo(7)
        }
    }

    @Test
    fun invalidDaylightIsRejected() {
        listOf(-0.1, 24.1, Double.NaN, Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY).forEach {
            assertThrows<IllegalArgumentException> { adjuster.adjustDays(8, watering, it) }
        }
    }

    @Test
    fun missingDaylightStillRespectsSpeciesBounds() {
        assertThat(adjuster.adjustDays(1, watering, null)).isEqualTo(7)
        assertThat(adjuster.adjustDays(20, watering, null)).isEqualTo(10)
    }

    @Test
    fun invalidSpeciesBoundsAreRejected() {
        assertThrows<IllegalArgumentException> {
            adjuster.adjustDays(8, watering.copy(soilCheckIntervalDaysMin = 0), null)
        }
        assertThrows<IllegalArgumentException> {
            adjuster.adjustDays(8, watering.copy(soilCheckIntervalDaysMax = 6), 12.0)
        }
    }

    @Test
    fun extremeBaseIntervalsDoNotOverflow() {
        assertThat(adjuster.adjustDays(Int.MAX_VALUE, watering, 8.0)).isEqualTo(10)
        assertThat(adjuster.adjustDays(Int.MIN_VALUE, watering, 16.0)).isEqualTo(7)
    }

    private val adjuster = SoilCheckPhotoperiodAdjuster()

    private val watering =
        WateringProfile(
            soilCheckIntervalDaysMin = 7,
            soilCheckIntervalDaysMax = 10,
            instruction = "Check soil",
        )

    /**
     * Verifies that short winter days (<= 9 hours daylight) increase the check interval by 1 day due to reduced transpiration.
     */
    @Test
    fun shortDaysSlightlyExtendInterval() {
        val result =
            adjuster.adjustDays(
                baseDays = 8,
                watering = watering,
                daylightHours = 8.0,
            )

        assertThat(result).isEqualTo(9)
    }

    /**
     * Verifies that long summer days (>= 14 hours daylight) decrease the check interval by 1 day due to increased transpiration.
     */
    @Test
    fun longDaysSlightlyReduceInterval() {
        val result =
            adjuster.adjustDays(
                baseDays = 8,
                watering = watering,
                daylightHours = 16.0,
            )

        assertThat(result).isEqualTo(7)
    }

    /**
     * Verifies that photoperiod adjustments are strictly constrained to the species's defined minimum and maximum limits.
     */
    @Test
    fun adjustmentNeverLeavesSpeciesRange() {
        val result =
            adjuster.adjustDays(
                baseDays = 7,
                watering = watering,
                daylightHours = 16.0,
            )

        assertThat(result).isEqualTo(7)
    }

    /**
     * Verifies that when daylight duration is unavailable (`null`), the base interval remains unchanged.
     */
    @Test
    fun missingDaylightKeepsBaseInterval() {
        val result =
            adjuster.adjustDays(
                baseDays = 8,
                watering = watering,
                daylightHours = null,
            )

        assertThat(result).isEqualTo(8)
    }
}
