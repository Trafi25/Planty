package com.traffipart.polanty.data.repository

import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.generationConfig
import com.squareup.moshi.Moshi
import com.traffipart.polanty.core.common.trimToNull
import com.traffipart.polanty.data.mapper.toDomain
import com.traffipart.polanty.data.remote.knowledge.dtos.GeminiPlantKnowledgeDto
import com.traffipart.polanty.domain.PlantKnowledgeGenerator
import com.traffipart.polanty.domain.model.LightRequirement
import com.traffipart.polanty.domain.model.PlantKnowledge
import com.traffipart.polanty.domain.model.ToxicityLevel
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

class GeminiPlantKnowledgeGenerator
    @Inject
    constructor(
        private val moshi: Moshi,
    ) : PlantKnowledgeGenerator {
        private val jsonAdapter = moshi.adapter(GeminiPlantKnowledgeDto::class.java)
        private val responseSchema =
            Schema.obj(
                properties =
                    mapOf(
                        "scientificName" to Schema.string("The exact requested scientific plant name."),
                        "description" to Schema.string("A concise botanical description"),
                        "petToxicity" to
                            Schema.enumeration(
                                values = ToxicityLevel.entries.map { it.name },
                                description = "Toxicity level for pets",
                            ),
                        "humanToxicity" to
                            Schema.enumeration(
                                values = ToxicityLevel.entries.map { it.name },
                                description = "Toxicity level for humans",
                            ),
                        "commonName" to Schema.string("Common name of the plant"),
                        "origin" to Schema.string("Native geographic origin"),
                        "toxicityNotes" to Schema.string("Optional safety details or warnings"),
                        "typicalHeightCmMin" to
                            Schema.integer(
                                description = "Minimum typical height in centimeters",
                                minimum = 1.0,
                            ),
                        "typicalHeightCmMax" to
                            Schema.integer(
                                description = "Maximum typical height in centimeters",
                                minimum = 1.0,
                            ),
                        "wateringDaysMin" to
                            Schema.integer(
                                description = "Minimum days between soil moisture checks",
                                minimum = 1.0,
                                maximum = 365.0,
                            ),
                        "wateringDaysMax" to
                            Schema.integer(
                                description = "Maximum days between soil moisture checks",
                                minimum = 1.0,
                                maximum = 365.0,
                            ),
                        "wateringInstruction" to Schema.string("Specific watering advice and indicators"),
                        "lightRequirement" to
                            Schema.enumeration(
                                values = LightRequirement.entries.map { it.name },
                                description = "Light requirements for the plant",
                            ),
                        "humidityMinPercent" to
                            Schema.integer(
                                description = "Minimum ideal humidity percentage",
                                minimum = 0.0,
                                maximum = 100.0,
                            ),
                        "humidityMaxPercent" to
                            Schema.integer(
                                description = "Maximum ideal humidity percentage",
                                minimum = 0.0,
                                maximum = 100.0,
                            ),
                        "temperatureMinCelsius" to
                            Schema.double(
                                description = "Minimum ideal temperature in Celsius",
                                minimum = -50.0,
                                maximum = 80.0,
                            ),
                        "temperatureMaxCelsius" to
                            Schema.double(
                                description = "Maximum ideal temperature in Celsius",
                                minimum = -50.0,
                                maximum = 80.0,
                            ),
                        "fertilizing" to Schema.string("Feeding frequency, timing and fertilizer type"),
                    ),
            )
        private val model by lazy {
            Firebase
                .ai(backend = GenerativeBackend.googleAI())
                .generativeModel(
                    modelName = "gemini-3.6-flash",
                    generationConfig =
                        generationConfig {
                            responseMimeType = "application/json"
                            responseSchema = this@GeminiPlantKnowledgeGenerator.responseSchema
                        },
                )
        }
        override suspend fun generate(
            scientificName: String,
            commonName: String?,
        ): PlantKnowledge? {
            val cleanScientificName = scientificName.trimToNull() ?: return null
            val prompt = createPrompt(cleanScientificName, commonName)
            return try {
                Log.d(TAG, "Prompt sent to Gemini:\n$prompt")
                
                val response = model.generateContent(prompt)
                val responseJson = response.text.trimToNull()
                
                if (responseJson == null) {
                    Log.w(TAG, "Gemini returned empty response for $cleanScientificName")
                    return null
                }
                
                Log.d(TAG, "Raw Gemini JSON: $responseJson")
                
                val dto = try {
                    jsonAdapter.fromJson(responseJson)
                } catch (e: Exception) {
                    Log.e(TAG, "JSON Parsing failed for $cleanScientificName. JSON: $responseJson", e)
                    null
                } ?: return null
                
                val knowledge = dto.toDomain(cleanScientificName, commonName)
                if (knowledge != null) {
                    Log.d(TAG, "Successfully generated domain model for $cleanScientificName")
                } else {
                    Log.w(TAG, "toDomain() returned null for $cleanScientificName")
                }
                knowledge
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Gemini generation CRASHED for $cleanScientificName: ${e.message}", e)
                null
            }
        }

        private fun createPrompt(
            scientificName: String,
            commonName: String?,
        ): String {
            val commonNameInformation =
                commonName
                    .trimToNull()
                    ?.let { "Known common name: $it" }
                    ?: "Common name is unknown."

            return """
                Generate conservative botanical and indoor-care information
                for the following exact plant species.

                Scientific name: $scientificName
                $commonNameInformation

                Requirements:
                - Do not substitute a related species.
                - Return the scientific name exactly as provided.
                - Use centimeters for height.
                - Watering days mean how often the soil should be checked,
                  not a strict mandatory watering schedule.
                - Use Celsius for temperature.
                - Use percentage values between 0 and 100 for humidity.
                - For toxicity use only: NonToxic, Mild, Toxic or Unknown.
                - For light use only: Low, MediumIndirect, BrightIndirect or Direct.
                - All fields are mandatory. If reliable information is unavailable, 
                  provide your best estimate or a safe default (e.g., "Information 
                  not available" for strings, or a typical range for numbers).
                - Do not invent toxicity information; use 'Unknown' if unsure.
                - Keep the description and instructions concise.
                """.trimIndent()
        }

        private companion object {

            const val TAG = "GeminiPlantKnowledge"
        }
    }
