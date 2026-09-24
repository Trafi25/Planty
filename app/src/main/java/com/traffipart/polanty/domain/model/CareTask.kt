package com.traffipart.polanty.domain.model

data class CareTask(
    val id: Long,
    val plantId: Long,
    val type: CareTaskType,
    val dueAt: Long,
    val isCompleted: Boolean,
    val completedAt: Long?,
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

fun CareTaskType.displayName(): String =
    when (this) {
        CareTaskType.Water ->
            "Water plant"
        CareTaskType.CheckSoil ->
            "Check soil"
        CareTaskType.Fertilize ->
            "Fertilize"
        CareTaskType.Rotate ->
            "Rotate plant"
        CareTaskType.Prune ->
            "Prune"
        CareTaskType.CleanLeaves ->
            "Clean leaves"
        CareTaskType.HealthCheck ->
            "Health check"
        CareTaskType.PestInspection ->
            "Check for pests"
        CareTaskType.Repot ->
            "Repot"
    }
