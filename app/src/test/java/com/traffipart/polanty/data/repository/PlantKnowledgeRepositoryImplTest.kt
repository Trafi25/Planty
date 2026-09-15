package com.traffipart.polanty.data.repository

import com.google.common.truth.Truth.assertThat
import com.traffipart.polanty.data.remote.knowledge.PerenualApi
import com.traffipart.polanty.data.room.knowledge.PlantKnowledgeDao
import com.traffipart.polanty.data.room.knowledge.PlantKnowledgeEntity
import com.traffipart.polanty.domain.PlantKnowledgeGenerator
import com.traffipart.polanty.domain.repository.PlantNameResolver
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

private fun cachedKnowledgeEntity() =
    PlantKnowledgeEntity(
        lookupScientificName = "kroenleinia grusonii",
        scientificName = "Kroenleinia grusonii",
        commonName = "Golden barrel cactus",
        description = "A globular cactus.",
        origin = "Mexico",
        petToxicity = "Unknown",
        humanToxicity = "Unknown",
        toxicityNotes = null,
        heightMinCm = 30,
        heightMaxCm = 100,
        wateringDaysMin = 14,
        wateringDaysMax = 21,
        wateringInstruction = "Check the soil before watering.",
        lightRequirement = "Direct",
        humidityMinPercent = null,
        humidityMaxPercent = null,
        temperatureMinCelsius = null,
        temperatureMaxCelsius = null,
        fertilizing = null,
        cachedAt = 1L,
    )

class PlantKnowledgeRepositoryImplTest {
    private val perenualApi =
        mockk<PerenualApi>()

    private val plantNameResolver =
        mockk<PlantNameResolver>()

    private val plantKnowledgeDao =
        mockk<PlantKnowledgeDao>()

    private val plantKnowledgeGenerator =
        mockk<PlantKnowledgeGenerator>()

    private lateinit var repository:
        PlantKnowledgeRepositoryImpl

    @BeforeEach
    fun setUp() {
        repository =
            PlantKnowledgeRepositoryImpl(
                perenualApi = perenualApi,
                plantNameResolver = plantNameResolver,
                plantKnowledgeDao = plantKnowledgeDao,
                plantKnowledgeGenerator =
                plantKnowledgeGenerator,
            )
    }

    @Test
    fun `returns cached knowledge without calling remote sources`() =
        runTest {
            coEvery {
                plantKnowledgeDao.getByScientificName("kroenleinia grusonii")
            } returns cachedKnowledgeEntity()

            val result = repository.getPlantKnowledge("kroenleinia grusonii")

            assertThat(result?.speciesInfo?.scientificName).isEqualTo("kroenleinia grusonii")
            coVerify(exactly = 0) {
                plantNameResolver.resolveNames(any())
            }
            coVerify(exactly = 0) {
                perenualApi.searchSpecies(any())
            }
            coVerify(exactly = 0) {
                plantKnowledgeGenerator.generate(any(), any())
            }
        }
}
