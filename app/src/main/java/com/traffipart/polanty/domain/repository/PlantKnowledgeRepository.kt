package com.traffipart.polanty.domain.repository

import com.traffipart.polanty.domain.model.PlantKnowledge

interface PlantKnowledgeRepository {
    suspend fun getPlantKnowledge(scientificName: String): PlantKnowledge?
}
