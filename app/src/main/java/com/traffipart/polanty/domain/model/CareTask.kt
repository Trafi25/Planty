package com.traffipart.polanty.domain.model

/**
 * Domain model representing a scheduled or completed plant care task.
 *
 * @property id Unique identifier of the task.
 * @property plantId Unique identifier of the plant associated with this task.
 * @property type The type of care action required (e.g., [CareTaskType.Water], [CareTaskType.CheckSoil]).
 * @property dueAt Timestamp in milliseconds when the task is scheduled to be due.
 * @property isCompleted `true` if the task has been marked as finished.
 * @property completedAt Timestamp in milliseconds when the task was completed, or `null` if pending.
 * @property xpReward Gamification experience points awarded upon completion.
 */
data class CareTask(
    val id: Long,
    val plantId: Long,
    val type: CareTaskType,
    val dueAt: Long,
    val isCompleted: Boolean,
    val completedAt: Long?,
    val xpReward: Int,
)

/**
 * Enumeration of available care activities for plants.
 */
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

/**
 * Returns a human-readable display label for the [CareTaskType].
 */
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
