package com.traffipart.polanty.domain.usecase.care

import com.traffipart.polanty.domain.model.CareTask
import com.traffipart.polanty.domain.repository.care.CareTaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Use case for observing open care tasks that are due on or before the current timestamp.
 *
 * @property repository The repository providing open task streams.
 */
class ObserveDueCareTasksUseCase
    @Inject
    constructor(
        private val repository: CareTaskRepository,
    ) {
        /**
         * Returns a Flow that emits the list of open care tasks due by the timestamp returned by [nowProvider].
         *
         * @param nowProvider Lambda returning the current timestamp in milliseconds. Defaults to system time.
         * @return A [Flow] emitting a list of due [CareTask] models.
         */
        operator fun invoke(
            nowProvider: () -> Long = {
                System.currentTimeMillis()
            },
        ): Flow<List<CareTask>> =
            repository.observeOpenTasks().map { tasks ->
                val now = nowProvider()
                tasks.filter { task -> task.dueAt <= now }
            }
    }
