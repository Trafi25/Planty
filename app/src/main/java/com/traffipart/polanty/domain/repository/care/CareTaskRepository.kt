package com.traffipart.polanty.domain.repository.care

import com.traffipart.polanty.domain.model.CareTask
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing plant care tasks.
 */
interface CareTaskRepository {
    /**
     * Returns a Flow that emits the list of all open (non-completed) care tasks.
     */
    fun observeOpenTasks(): Flow<List<CareTask>>

    /**
     * Returns a Flow that emits the list of all care tasks associated with a specific plant.
     *
     * @param plantId The unique ID of the plant.
     */
    fun observePlantTasks(plantId: Long): Flow<List<CareTask>>

    /**
     * Marks a care task as completed.
     *
     * @param taskId The unique ID of the task to complete.
     * @param completedAt The timestamp when the task was completed.
     * @return True if the operation was successful, false otherwise.
     */
    suspend fun completeTask(
        taskId: Long,
        completedAt: Long,
    ): Boolean

    /**
     * Creates a new care task.
     *
     * @param task The [CareTask] domain model to persist.
     * @return The unique ID of the created task.
     */
    suspend fun createTask(task: CareTask): Long

    /**
     * Deletes a care task.
     *
     * @param task The [CareTask] domain model to delete.
     * @return True if the operation was successful, false otherwise.
     */
    suspend fun deleteTask(task: CareTask): Boolean
}
