package com.traffipart.polanty.data.repository

import com.traffipart.polanty.domain.PlantKnowledgeGenerator
import com.traffipart.polanty.domain.model.PlantKnowledge

class GeminiPlantKnowledgeGenerator : PlantKnowledgeGenerator {
    override suspend fun generate(
        scientificName: String,
        commonName: String?,
    ): PlantKnowledge? {
        TODO("Not yet implemented")
    }
}
