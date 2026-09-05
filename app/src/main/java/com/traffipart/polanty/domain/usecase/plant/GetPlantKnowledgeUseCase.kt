package com.traffipart.polanty.domain.usecase.plant

import com.traffipart.polanty.domain.model.PlantKnowledge
import com.traffipart.polanty.domain.repository.PlantKnowledgeRepository
import javax.inject.Inject

class GetPlantKnowledgeUseCase
    @Inject
    constructor(
        private val repository: PlantKnowledgeRepository,
    ) {
        suspend operator fun invoke(scientificName: String): PlantKnowledge? {
            val normalizedName = scientificName.trim()
            if (normalizedName.isEmpty()) return null
            return repository.getPlantKnowledge(normalizedName)
        }
    }
