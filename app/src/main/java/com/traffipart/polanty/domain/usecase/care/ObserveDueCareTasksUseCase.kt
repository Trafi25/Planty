package com.traffipart.polanty.domain.usecase.care

import com.traffipart.polanty.domain.model.CareTask
import com.traffipart.polanty.domain.repository.care.CareTaskRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveDueCareTasksUseCase
    @Inject
    constructor(
        private val repository: CareTaskRepository,
    ) {
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
