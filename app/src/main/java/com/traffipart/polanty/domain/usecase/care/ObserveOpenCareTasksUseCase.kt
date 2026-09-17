package com.traffipart.polanty.domain.usecase.care

import com.traffipart.polanty.domain.model.CareTask
import com.traffipart.polanty.domain.repository.care.CareTaskRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Use case for observing all currently open (incomplete) care tasks.
 *
 * @property careRepository The repository providing the task stream.
 */
class ObserveOpenCareTasksUseCase
    @Inject
    constructor(
        private val careRepository: CareTaskRepository,
    ) {
        /**
         * Returns a Flow that emits the list of open care tasks whenever it changes.
         */
        operator fun invoke(): Flow<List<CareTask>> = careRepository.observeOpenTasks()
    }
