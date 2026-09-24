package com.traffipart.polanty.data.room.care

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.traffipart.polanty.data.room.plant.PlantEntity

/**
 * Represents a care task stored in the Room database (`care_tasks` table).
 *
 * Configured with a foreign key referencing [PlantEntity] with `CASCADE` deletion behavior,
 * ensuring tasks are cleaned up when their parent plant is removed.
 * Indexed by `plantId` and `dueAt` for performance.
 *
 * @property id Auto-generated primary key.
 * @property plantId Foreign key referencing the associated plant's ID.
 * @property type String representation of the [com.traffipart.polanty.domain.model.CareTaskType].
 * @property dueAt Timestamp in milliseconds when the task is scheduled to be due.
 * @property isCompleted `true` if the task is finished, `false` otherwise.
 * @property completedAt Timestamp in milliseconds when completed, or `null` if pending.
 * @property xpReward Experience points awarded upon task completion.
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
