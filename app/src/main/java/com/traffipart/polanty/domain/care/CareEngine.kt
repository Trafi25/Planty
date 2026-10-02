package com.traffipart.polanty.domain.care

import com.traffipart.polanty.domain.model.care.CareEnvironment
import com.traffipart.polanty.domain.model.care.CareTask
import com.traffipart.polanty.domain.model.care.CareTaskType
import com.traffipart.polanty.domain.model.knowledge.PlantKnowledge
import javax.inject.Inject
import kotlin.time.Duration.Companion.days

/**
 * Engine responsible for calculating and scheduling care tasks for plants based on botanical knowledge and environment.
 *
 * @property intervalCalculator Calculator used to adapt soil moisture check intervals based on environmental conditions.
 */
class CareEngine
    @Inject
    constructor(
        private val intervalCalculator: SoilCheckIntervalCalculator,
    ) {
        /**
         * Determines which care tasks are needed for a specific plant based on its history, botanical requirements, and ambient climate.
         *
         * @param plantId The unique ID of the plant.
         * @param knowledge The botanical knowledge containing care intervals and preferred climate ranges.
         * @param existingTasks The current list of tasks for this plant to avoid duplicates.
         * @param environment Optional ambient temperature and humidity conditions.
         * @param now The current reference timestamp in milliseconds.
         * @return A list of new [CareTask]s that should be scheduled.
         */
        fun generateTasks(
            plantId: Long,
            knowledge: PlantKnowledge,
            existingTasks: List<CareTask>,
            environment: CareEnvironment? = null,
            now: Long = System.currentTimeMillis(),
        ): List<CareTask> {
            require(plantId > 0) { "Plant ID must be valid" }

            val wateringProfile = knowledge.careProfile?.watering ?: return emptyList()
            val intervalDays =
                intervalCalculator.calculateDays(
                    watering = wateringProfile,
                    humidityRange = knowledge.careProfile.humidity,
                    temperatureRange = knowledge.careProfile.temperature,
                    environment = environment,
                )

            // Filter tasks for this specific plant and type once for efficiency
            val relevantTasks =
                existingTasks.filter { task ->
                    task.plantId == plantId && (task.type == CareTaskType.CheckSoil || task.type == CareTaskType.Water)
                }

            // Rule 1: Don't create a new task if there's already an incomplete one
            if (relevantTasks.any { task -> !task.isCompleted }) {
                return emptyList()
            }

            // Rule 2: Schedule based on the last completion time or schedule for 'now' if first time
            val lastCompletedAt =
                relevantTasks
                    .asSequence()
                    .filter { task -> task.isCompleted }
                    .mapNotNull { task -> task.completedAt }
                    .maxOrNull()

            val nextDueAt =
                if (lastCompletedAt == null) {
                    now
                } else {
                    val scheduledTime = lastCompletedAt + intervalDays.days.inWholeMilliseconds
                    // If the interval has already passed, schedule for now, otherwise use the calculated time
                    maxOf(now, scheduledTime)
                }

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

        private companion object {
            const val CHECK_SOIL_XP = 10
        }
    }
