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

        val result = try {
            gbifApi.matchSpecies(originalName)
        } catch (
            e: CancellationException,
        ) {
            throw e
        } catch (_: Exception) {
            return listOf(originalName)
        }
        val taxonKey = result.acceptedUsage?.key ?: result.usage?.key

        val synonyms = if (taxonKey != null) {
            try {
                gbifApi.getSynonyms(taxonKey).results
            } catch (
                e: CancellationException,
            ) {
                throw e
            } catch (_: Exception) {
                emptyList()
            }
        } else {
            emptyList()
        }

        return buildList {
            add(originalName)
            result.usage
                ?.toCanonicalName()
                ?.let(::add)

            result.acceptedUsage
                ?.toCanonicalName()
                ?.let(::add)
            synonyms
                .mapNotNull { synonym ->
                    synonym.canonicalName
                        ?.takeIf {
                            synonym.rank
                                ?.equals(
                                    "SPECIES",
                                    ignoreCase = true,
                                ) == true
                        }
                }
                .forEach(::add)
        }
            .map { it.trim() }
            .filter { it.isNotEmpty() }
            .distinctBy {
                it.lowercase()
            }
    }
}

private fun GbifNameUsageDto.toCanonicalName(): String? {
    val speciesLevel = specificEpithet?.isNotBlank() == true || rank?.uppercase() in setOf(
        "SPECIES",
        "SUBSPECIES",
        "VARIETY",
        "FORM",)
    if (!speciesLevel) {
        return null
    }
    canonicalName?.takeIf { it.isNotBlank() }?.let { return it }
    val generated = listOfNotNull(genericName, specificEpithet).joinToString(" ").trim()
    return generated.takeIf { it.isNotBlank() }
}