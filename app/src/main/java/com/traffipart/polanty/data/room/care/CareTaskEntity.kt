package com.traffipart.polanty.data.room.care

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.traffipart.polanty.data.room.plant.PlantEntity

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
