package com.traffipart.polanty.domain

import com.traffipart.polanty.domain.model.PlantKnowledge

interface PlantKnowledgeGenerator {
    suspend fun generate(
        scientificName: String,
        commonName: String?,
    ): PlantKnowledge?
}
