package com.traffipart.polanty.data.mapper

import com.traffipart.polanty.data.room.care.CareTaskEntity
import com.traffipart.polanty.domain.model.CareTask
import com.traffipart.polanty.domain.model.CareTaskType

/**
 * Maps a [CareTask] domain model object to its corresponding database [CareTaskEntity] representation.
 *
 * Serialization details:
 * - The [CareTask.type] enum value is persisted as its exact string name representation via [Enum.name].
 *
 * @return A fully populated [CareTaskEntity] instance ready for database persistence.
 */
fun CareTask.toEntity(): CareTaskEntity =
    CareTaskEntity(
        id = id,
        plantId = plantId,
        type = type.name,
        dueAt = dueAt,
        isCompleted = isCompleted,
        completedAt = completedAt,
        xpReward = xpReward,
    )

/**
 * Maps a database [CareTaskEntity] back to its domain layer [CareTask] representation.
 *
 * Deserialization and safety details:
 * - Safely converts the persisted [CareTaskEntity.type] string back into a [CareTaskType] enum variant using [CareTaskType.valueOf] wrapped in a [runCatching] block.
 * - If the persisted string does not match any known enum constant (e.g., due to schema updates or database tampering), the conversion fails gracefully and returns `null` to prevent app crashes.
 *
 * @return A valid [CareTask] domain object, or `null` if the task type string cannot be recognized.
 */
fun CareTaskEntity.toDomain(): CareTask? {
    val taskType =
        runCatching {
            CareTaskType.valueOf(type)
        }.getOrNull()
            ?: return null

    return CareTask(
        id = id,
        plantId = plantId,
        type = taskType,
        dueAt = dueAt,
        isCompleted = isCompleted,
        completedAt = completedAt,
        xpReward = xpReward,
    )
}
