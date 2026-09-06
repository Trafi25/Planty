package com.traffipart.polanty.data.repository

import com.traffipart.polanty.data.mapper.toDomain
import com.traffipart.polanty.data.remote.knowledge.PerenualApi
import com.traffipart.polanty.domain.model.PlantKnowledge
import com.traffipart.polanty.domain.repository.PlantKnowledgeRepository
import javax.inject.Inject

class PlantKnowledgeRepositoryImpl
    @Inject
    constructor(
        private val perenualApi: PerenualApi,
    ) : PlantKnowledgeRepository {
        override suspend fun getPlantKnowledge(scientificName: String): PlantKnowledge? {
            val searchResult = perenualApi.searchSpecies(scientificName)
            val exactMathc =
                searchResult.data.firstOrNull { species ->
                    species.scientificNames.any { name ->
                        name.equals(other = scientificName, ignoreCase = true)
                    }
                } ?: return null
            val details = perenualApi.getSpeciesDetails(exactMathc.id)
            return details.toDomain()
        }
    }
