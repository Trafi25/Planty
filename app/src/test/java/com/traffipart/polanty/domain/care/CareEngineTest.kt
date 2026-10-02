package com.traffipart.polanty.domain.care

import com.google.common.truth.Truth.assertThat
import com.traffipart.polanty.domain.model.care.CareTask
import com.traffipart.polanty.domain.model.care.CareTaskType
import com.traffipart.polanty.domain.model.knowledge.PlantCareProfile
import com.traffipart.polanty.domain.model.knowledge.PlantKnowledge
import com.traffipart.polanty.domain.model.knowledge.PlantSpeciesInfo
import com.traffipart.polanty.domain.model.knowledge.PlantToxicity
import com.traffipart.polanty.domain.model.knowledge.ToxicityLevel
import com.traffipart.polanty.domain.model.knowledge.WateringProfile
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.days
import kotlin.time.Duration.Companion.hours

private fun plantKnowledge(
    wateringMinDays: Int = 7,
    wateringMaxDays: Int = 10,
): PlantKnowledge =

    PlantKnowledge(
        speciesInfo =
            PlantSpeciesInfo(
                scientificName = "Test plant",
                commonName = "Test",
                description = "Test plant",
                origin = null,
                toxicity =
                    PlantToxicity(
                        pets = ToxicityLevel.Unknown,
                        humans = ToxicityLevel.Unknown,
                        notes = null,
                    ),
                typicalHeightCmMin = null,
                typicalHeightCmMax = null,
            ),
        careProfile =
            PlantCareProfile(
                scientificName = "Test plant",
                watering =
                    WateringProfile(
                        soilCheckIntervalDaysMin =
                        wateringMinDays,
                        soilCheckIntervalDaysMax =
                        wateringMaxDays,
                        instruction =
                            "Check the soil before watering.",
                    ),
                light = null,
                humidity = null,
                temperature = null,
                fertilizing = null,
            ),
    )

private val careEngine = CareEngine(intervalCalculator = SoilCheckIntervalCalculator())

class CareEngineTest {
    @Test
    fun createsSoilCheckImmediatelyWhenPlantHasNoCareHistory() {
        val now = 1_700_000_000_000L

        val result =
            careEngine.generateTasks(
                plantId = 1L,
                knowledge = plantKnowledge(),
                existingTasks = emptyList(),
                now = now,
            )

        assertThat(result).hasSize(1)

        val task = result.first()

        assertThat(task.type).isEqualTo(CareTaskType.CheckSoil)
        assertThat(task.dueAt).isEqualTo(now)
        assertThat(task.isCompleted).isFalse()
    }

    @Test
    fun doesNotCreateAnotherSoilCheckWhenOneIsAlreadyOpen() {
        val existingTask =
            CareTask(
                id = 12L,
                plantId = 1L,
                type = CareTaskType.CheckSoil,
                dueAt = 1000L,
                isCompleted = false,
                completedAt = null,
                xpReward = 10,
            )
        val result =
            careEngine.generateTasks(
                plantId = 1L,
                knowledge = plantKnowledge(),
                existingTasks = listOf(existingTask),
                now = 2000L,
            )
        assertThat(result).isEmpty()
    }

    @Test
    fun schedulesNextSoilCheckAfterMinimumInterval() {
        val completedAt =
            1_700_000_000_000L

        val existingTask =
            CareTask(
                id = 10L,
                plantId = 1L,
                type = CareTaskType.CheckSoil,
                dueAt = completedAt,
                isCompleted = true,
                completedAt = completedAt,
                xpReward = 10,
            )

        val result =
            careEngine.generateTasks(
                plantId = 1L,
                knowledge =
                    plantKnowledge(
                        wateringMinDays = 7,
                    ),
                existingTasks =
                    listOf(existingTask),
                now = completedAt,
            )

        val expectedDueAt =
            completedAt +
                7.days.inWholeMilliseconds

        assertThat(result.single().dueAt)
            .isEqualTo(expectedDueAt)
    }

    @Test
    fun doesNotCreateSoilCheckWhileWateringTaskIsOpen() {
        val wateringTask =
            CareTask(
                id = 20L,
                plantId = 1L,
                type = CareTaskType.Water,
                dueAt = 1000L,
                isCompleted = false,
                completedAt = null,
                xpReward = 10,
            )
        val result =
            careEngine.generateTasks(
                plantId = 1L,
                knowledge = plantKnowledge(),
                existingTasks = listOf(wateringTask),
                now = 2000L,
            )

        assertThat(result).isEmpty()
    }

    @Test
    fun schedulesNextSoilCheckFromLatestWatering() {
        val soilCheckCompletedAt =
            1_700_000_000_000L

        val wateredAt =
            soilCheckCompletedAt +
                2.hours.inWholeMilliseconds

        val soilCheck =
            CareTask(
                id = 10L,
                plantId = 1L,
                type = CareTaskType.CheckSoil,
                dueAt = soilCheckCompletedAt,
                isCompleted = true,
                completedAt =
                soilCheckCompletedAt,
                xpReward = 10,
            )

        val watering =
            CareTask(
                id = 11L,
                plantId = 1L,
                type = CareTaskType.Water,
                dueAt = wateredAt,
                isCompleted = true,
                completedAt = wateredAt,
                xpReward = 10,
            )

        val result =
            careEngine.generateTasks(
                plantId = 1L,
                knowledge =
                    plantKnowledge(
                        wateringMinDays = 7,
                    ),
                existingTasks =
                    listOf(
                        soilCheck,
                        watering,
                    ),
                now = wateredAt,
            )

        val expectedDueAt =
            wateredAt +
                7.days.inWholeMilliseconds

        assertThat(
            result.single().dueAt,
        ).isEqualTo(
            expectedDueAt,
        )
    }
}
