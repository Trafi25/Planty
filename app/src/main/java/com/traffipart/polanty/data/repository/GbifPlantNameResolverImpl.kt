package com.traffipart.polanty.data.repository

import com.traffipart.polanty.data.remote.taxonomy.GbifApi
import com.traffipart.polanty.data.remote.taxonomy.dto.GbifNameUsageDto
import com.traffipart.polanty.domain.repository.PlantNameResolver
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class GbifPlantNameResolverImpl @Inject constructor(
    private val gbifApi: GbifApi
) : PlantNameResolver {
    override suspend fun resolveNames(scientificName: String): List<String> {
        val originalName = scientificName.trim()
        if (originalName.isEmpty()) return emptyList()

        return try {
            val result = gbifApi.matchSpecies(originalName)
            buildList {
                add(originalName)
                result.usage?.toCanonicalName()?.let(::add)
                result.acceptedUsage?.toCanonicalName()?.let { add(it) }
            }.map{
                it.trim()
            }.filter { it.isNotEmpty() }.distinctBy { it.lowercase() }
        } catch (
            e: CancellationException,
        ) {
            throw e
        } catch (_: Exception) {
            listOf(originalName)
        }
    }


}

private fun GbifNameUsageDto.toCanonicalName(): String? {
    canonicalName?.takeIf { it.isNotBlank() }?.let { return it }
    val generated = listOfNotNull(genericName, specificEpithet).joinToString(" ").trim()
    return generated.takeIf { it.isNotBlank() }
}