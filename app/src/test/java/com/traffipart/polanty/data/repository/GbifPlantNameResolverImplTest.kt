package com.traffipart.polanty.data.repository

import com.google.common.truth.Truth.assertThat
import com.traffipart.polanty.data.remote.taxonomy.GbifApi
import com.traffipart.polanty.data.remote.taxonomy.dto.GbifMatchResponseDto
import com.traffipart.polanty.data.remote.taxonomy.dto.GbifNameUsageDto
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.io.IOException

class GbifPlantNameResolverImplTest {
    private val gbifApi =
        mockk<GbifApi>()

    private lateinit var resolver:
        GbifPlantNameResolverImpl

    @BeforeEach
    fun setUp() {
        resolver =
            GbifPlantNameResolverImpl(
                gbifApi = gbifApi,
            )
    }

    @Test
    fun `returns original and accepted scientific names`() =
        runTest {
            coEvery {
                gbifApi.matchSpecies(
                    scientificName =
                        "Echinocactus grusonii",
                )
            } returns
                GbifMatchResponseDto(
                    usage =
                        GbifNameUsageDto(
                            canonicalName =
                                "Echinocactus grusonii",
                            genericName =
                                "Echinocactus",
                            specificEpithet =
                                "grusonii",
                            rank =
                                "SPECIES",
                        ),
                    acceptedUsage =
                        GbifNameUsageDto(
                            canonicalName =
                                "Kroenleinia grusonii",
                            genericName =
                                "Kroenleinia",
                            specificEpithet =
                                "grusonii",
                            rank =
                                "SPECIES",
                        ),
                )

            val result =
                resolver.resolveNames(
                    "Echinocactus grusonii",
                )

            assertThat(result)
                .containsExactly(
                    "Echinocactus grusonii",
                    "Kroenleinia grusonii",
                ).inOrder()
        }

    @Test
    fun `returns original name when GBIF fails`() =
        runTest {
            coEvery {
                gbifApi.matchSpecies(any())
            } throws IOException()

            val result =
                resolver.resolveNames(
                    "Opuntia rufida",
                )

            assertThat(result)
                .containsExactly(
                    "Opuntia rufida",
                )
        }

    @Test
    fun `removes duplicate scientific names`() =
        runTest {
            coEvery {
                gbifApi.matchSpecies(any())
            } returns
                GbifMatchResponseDto(
                    usage =
                        GbifNameUsageDto(
                            canonicalName =
                                "Astrophytum myriostigma",
                            specificEpithet =
                                "myriostigma",
                            rank =
                                "SPECIES",
                        ),
                    acceptedUsage =
                        GbifNameUsageDto(
                            canonicalName =
                                "Astrophytum myriostigma",
                            specificEpithet =
                                "myriostigma",
                            rank =
                                "SPECIES",
                        ),
                )

            val result =
                resolver.resolveNames(
                    "Astrophytum myriostigma",
                )

            assertThat(result)
                .containsExactly(
                    "Astrophytum myriostigma",
                )
        }
}
