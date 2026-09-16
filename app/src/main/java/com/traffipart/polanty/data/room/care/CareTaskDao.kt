package com.traffipart.polanty.data.room.care

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) for executing database operations on the `care_tasks` table.
 *
 * All operations take advantage of the following database design choices:
 * - **Foreign Key Integrity:** Cascades deletion when a plant is deleted, meaning tasks are cleaned up automatically.
 * - **Index-driven Lookups:** Queries filtering by `plantId` or sorting by `dueAt` are highly optimized by dedicated database indices.
 */
@Dao
interface CareTaskDao {
    /**
     * Observes all active, uncompleted care tasks across all plants, sorted chronologically by due date.
     * Utilizes the index on `dueAt` for high performance sorting.
     *
     * @return A reactive [Flow] containing a list of uncompleted [CareTaskEntity] objects.
     */
    @Query("SELECT * FROM care_tasks WHERE isCompleted = 0 ORDER BY dueAt ASC")
    fun observeOpenTasks(): Flow<List<CareTaskEntity>>

    /**
     * Observes all scheduled or historic care tasks for a specific plant, ordered by due date.
     * Leverages the `plantId` index to filter records quickly.
     *
     * @param plantId The unique ID of the plant whose tasks are being observed.
     * @return A reactive [Flow] containing a list of [CareTaskEntity] objects associated with the plant.
     */
    @Query("SELECT * FROM care_tasks WHERE plantId = :plantId ORDER BY dueAt ASC")
    fun observePlantTasks(plantId: Long): Flow<List<CareTaskEntity>>

    /**
     * Inserts a new care task record into the database.
     *
     * @param task The [CareTaskEntity] to insert.
     * @return The row ID of the newly inserted task.
     */
    @Insert
    suspend fun insert(task: CareTaskEntity): Long

    /**
     * Marks an existing uncompleted care task as completed and updates its completion timestamp.
     *
     * @param taskId The identifier of the care task to complete.
     * @param completedAt The timestamp (in milliseconds) indicating when the task was finished.
     * @return The number of rows affected (should be 1 if the task existed and was open, or 0 otherwise).
     */
    @Query("UPDATE care_tasks SET isCompleted = 1, completedAt = :completedAt WHERE id = :taskId AND isCompleted = 0")
    suspend fun completeTask(
        taskId: Long,
        completedAt: Long,
    ): Int

    /**
     * Deletes a specific care task record from the database.
     *
     * @param task The [CareTaskEntity] to be removed.
     * @return The number of rows affected by the deletion.
     */
    @Delete
    suspend fun delete(task: CareTaskEntity): Int
}
