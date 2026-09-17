package com.traffipart.polanty.data.repository.care

import com.traffipart.polanty.data.mapper.toDomain
import com.traffipart.polanty.data.mapper.toEntity
import com.traffipart.polanty.data.room.care.CareTaskDao
import com.traffipart.polanty.domain.model.CareTask
import com.traffipart.polanty.domain.repository.care.CareTaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Implementation of [CareTaskRepository] using Room database for persistence.
 *
 * @property careTaskDao The DAO for accessing the care tasks table.
 */
class CareTaskRepositoryImpl
    @Inject
    constructor(
        private val careTaskDao: CareTaskDao,
    ) : CareTaskRepository {
        /**
         * Observes all open care tasks from the database.
         */
        override fun observeOpenTasks(): Flow<List<CareTask>> =
            careTaskDao.observeOpenTasks().map { entities ->
                entities.mapNotNull { entity ->
                    entity.toDomain()
                }
            }

        /**
         * Observes all care tasks for a specific plant from the database.
         *
         * @param plantId The unique ID of the plant.
         */
        override fun observePlantTasks(plantId: Long): Flow<List<CareTask>> =
            careTaskDao.observePlantTasks(plantId).map { entities ->
                entities.mapNotNull { entities ->
                    entities.toDomain()
                }
            }

        /**
         * Marks a care task as completed in the database.
         */
        override suspend fun completeTask(
            taskId: Long,
            completedAt: Long,
        ): Boolean = careTaskDao.completeTask(taskId, completedAt) == 1

        /**
         * Inserts a new care task into the database.
         */
        override suspend fun createTask(task: CareTask): Long = careTaskDao.insert(task = task.toEntity())

        /**
         * Deletes a care task from the database.
         */
        override suspend fun deleteTask(task: CareTask): Boolean = careTaskDao.delete(task = task.toEntity()) == 1
    }
