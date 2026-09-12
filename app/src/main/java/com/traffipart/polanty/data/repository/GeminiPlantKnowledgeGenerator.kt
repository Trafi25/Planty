package com.traffipart.polanty.data.repository

import com.squareup.moshi.Moshi
import com.traffipart.polanty.domain.PlantKnowledgeGenerator
import com.traffipart.polanty.domain.model.PlantKnowledge
import javax.inject.Inject

class GeminiPlantKnowledgeGenerator @Inject constructor(
    private val moshi: Moshi
) : PlantKnowledgeGenerator {
    override suspend fun generate(
        scientificName: String,
        commonName: String?,
    ): PlantKnowledge? {
        TODO("Not yet implemented")
    }
}
