package com.traffipart.polanty.data.room.care

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.traffipart.polanty.data.room.plant.PlantEntity

/**
 * Room Entity representing a scheduled or completed care task for a plant.
 *
 * ### Foreign Key Relationships
 * Each care task is linked to a parent plant via the [plantId] field, which establishes a
 * foreign key relationship referencing [PlantEntity.id].
 * - **On Delete CASCADE:** When a plant is deleted from the database, all of its associated
 *   care tasks are automatically cascade deleted to maintain data integrity and prevent orphaned rows.
 *
 * ### Indexing Strategy
 * To guarantee optimal database performance and query efficiency, indices are declared for the following columns:
 * 1. **`plantId` Index:** Accelerates filtering tasks by a specific plant (e.g., when observing tasks for a given plant profile).
 * 2. **`dueAt` Index:** Accelerates time-based sorting and range queries (e.g., retrieving open tasks ordered by their due dates).
 *
 * @property id Unique auto-generated identifier for the care task.
 * @property plantId The identifier of the [PlantEntity] this task belongs to.
 * @property type The string representation of the care task type, mapped from the domain enum.
 * @property dueAt The timestamp (in milliseconds) when the task is due.
 * @property isCompleted Indicates whether the care task has been fulfilled.
 * @property completedAt The timestamp (in milliseconds) when the task was completed, or `null` if it is still open.
 * @property xpReward The experience point reward granted to the user upon completing this task.
 */
@Entity(
    tableName = "care_tasks",
    foreignKeys =
        [
            ForeignKey(
                entity = PlantEntity::class,
                parentColumns = ["id"],
                childColumns = ["plantId"],
                onDelete = ForeignKey.CASCADE,
            ),
        ],
    indices = [Index("plantId"), Index("dueAt")],
)
data class CareTaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val plantId: Long,
    val type: String,
    val dueAt: Long,
    val isCompleted: Boolean,
    val completedAt: Long?,
    val xpReward: Int,
)
