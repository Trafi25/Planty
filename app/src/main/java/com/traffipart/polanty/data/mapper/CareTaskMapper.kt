package com.traffipart.polanty.data.mapper

import com.traffipart.polanty.data.room.care.CareTaskEntity
import com.traffipart.polanty.domain.model.CareTask
import com.traffipart.polanty.domain.model.CareTaskType

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
