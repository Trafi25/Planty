package com.traffipart.polanty.domain.care

import com.traffipart.polanty.domain.model.care.CareContext
import com.traffipart.polanty.domain.model.care.CareTask
import com.traffipart.polanty.domain.model.care.CareTaskType
import com.traffipart.polanty.domain.model.knowledge.PlantKnowledge
import javax.inject.Inject
import kotlin.time.Duration.Companion.days

/**
 * Engine responsible for calculating and scheduling care tasks for plants based on botanical knowledge and environment.
 *
 * @property intervalCalculator Calculator used to adapt soil moisture check intervals based on environmental conditions.
 * @property photoperiodAdjuster Adjuster used to fine-tune intervals based on seasonal daylight duration.
 */
class CareEngine
    @Inject
    constructor(
        private val intervalCalculator: SoilCheckIntervalCalculator,
        private val photoperiodAdjuster: SoilCheckPhotoperiodAdjuster,
    ) {
        /**
         * Determines which care tasks are needed for a specific plant based on its history, botanical requirements, and ambient climate.
         *
         * @param plantId The unique ID of the plant.
         * @param knowledge The botanical knowledge containing care intervals and preferred climate ranges.
         * @param existingTasks The current list of tasks for this plant to avoid duplicates.
         * @param careContext Evaluated microclimate and photoperiod context.
         * @param now The current reference timestamp in milliseconds.
         * @return A list of new [CareTask]s that should be scheduled.
         */
        fun generateTasks(
            plantId: Long,
            knowledge: PlantKnowledge,
            existingTasks: List<CareTask>,
            careContext: CareContext = CareContext(),
            now: Long = System.currentTimeMillis(),
        ): List<CareTask> {
            require(plantId > 0) { "Plant ID must be valid" }

            // Filter tasks for this specific plant and type once for efficiency
            val relevantTasks =
                existingTasks.filter { task ->
                    task.plantId == plantId &&
                        (
                            task.type == CareTaskType.CheckSoil ||
                                task.type == CareTaskType.Water
                        )
                }

            // Rule 1: Don't create a new task if there's already an incomplete one
            if (relevantTasks.any { task -> !task.isCompleted }) {
                return emptyList()
            }

            val recommendedDueAt =
                calculateRecommendedSoilCheckDueAt(
                    plantId = plantId,
                    knowledge = knowledge,
                    existingTasks = existingTasks,
                    careContext = careContext,
                    now = now,
                ) ?: return emptyList()

            val nextDueAt = maxOf(now, recommendedDueAt)

            return listOf(
                CareTask(
                    id = 0L,
                    plantId = plantId,
                    type = CareTaskType.CheckSoil,
                    dueAt = nextDueAt,
                    isCompleted = false,
                    completedAt = null,
                    xpReward = CHECK_SOIL_XP,
                ),
            )
        }

        /**
         * Calculates the recommended due date timestamp for a plant's next soil moisture check.
         *
         * Anchors the calculation to the plant's last completed care task (watering or soil check).
         * If no completion history exists, defaults to [now].
         *
         * @param plantId The target plant ID.
         * @param knowledge Botanical knowledge containing watering guidelines and preferred climate ranges.
         * @param existingTasks Historic and open tasks associated with the plant.
         * @param careContext Evaluated microclimate and photoperiod context.
         * @param now Current reference timestamp in milliseconds.
         * @return Recommended due date timestamp in milliseconds, or `null` if care profile guidelines are missing.
         */
        fun calculateRecommendedSoilCheckDueAt(
            plantId: Long,
            knowledge: PlantKnowledge,
            existingTasks: List<CareTask>,
            careContext: CareContext = CareContext(),
            now: Long = System.currentTimeMillis(),
        ): Long? {
            require(plantId > 0) {
                "Plant ID must be valid"
            }

            val careProfile =
                knowledge.careProfile
                    ?: return null

            val wateringProfile =
                careProfile.watering
                    ?: return null

            val baseIntervalDays =
                intervalCalculator.calculateDays(
                    watering = wateringProfile,
                    humidityRange =
                        careProfile.humidity,
                    temperatureRange =
                        careProfile.temperature,
                    environment =
                        careContext.environment,
                )

            val intervalDays =
                photoperiodAdjuster.adjustDays(
                    baseDays =
                        baseIntervalDays,
                    watering =
                        wateringProfile,
                    daylightHours =
                        careContext.daylightHours,
                )

            val lastCompletedAt =
                existingTasks
                    .asSequence()
                    .filter { task ->
                        task.plantId == plantId &&
                            task.isCompleted &&
                            (
                                task.type == CareTaskType.CheckSoil ||
                                    task.type == CareTaskType.Water
                            )
                    }.mapNotNull { task ->
                        task.completedAt
                    }.maxOrNull()

            return if (lastCompletedAt == null) {
                now
            } else {
                lastCompletedAt + intervalDays.days.inWholeMilliseconds
            }
        }

        private companion object {
            const val CHECK_SOIL_XP = 10
        }
    }
