package com.traffipart.polanty.core

data class CareTask(
    val id: Long,
    val plantId: Long,
    val type: CareTaskType,
    val dueAt: Long,
    val isCompleted: Boolean,
    val xpReward: Int,
)

enum class CareTaskType {
    Water,
    CheckSoil,
    Fertilize,
    Rotate,
    Prune,
    CleanLeaves,
    HealthCheck,
    PestInspection,
    Repot,
}
