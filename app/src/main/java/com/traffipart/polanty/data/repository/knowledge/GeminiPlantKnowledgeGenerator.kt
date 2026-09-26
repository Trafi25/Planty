package com.traffipart.polanty.data.repository.knowledge

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

/**
 * An AI-driven implementation of [PlantKnowledgeGenerator] leveraging the Google Gemini API via Firebase.
 *
 * This generator constructs structured prompts requesting detailed botanical care information for specific plant species,
 * enforces a strict JSON [responseSchema] on the model to guarantee structural validity, and deserializes the resulting
 * content into safe domain models.
 *
 * **Key Logic & Architecture:**
 * - **Response Schema Enforcement:** Defines an explicit, typed schema for properties including validation constraints (like minimum/maximum values for humidity and temperature). This forces Gemini to return a reliable JSON payload matching [GeminiPlantKnowledgeDto].
 * - **Structured Prompts:** Supplies context and specific rules to the AI model, ensuring it remains conservative, provides safe default estimates when data is missing, handles ranges correctly, and sticks strictly to the specified enums.
 * - **Graceful Parsing and Error Handling:** Handles JSON parsing anomalies or connectivity issues cleanly, logging failures and returning `null` rather than propagating raw model or network crashes up the stack.
 */
class GeminiPlantKnowledgeGenerator
    @Inject
    constructor(
        private val moshi: Moshi,
    ) : PlantKnowledgeGenerator {
        private val jsonAdapter = moshi.adapter(GeminiPlantKnowledgeDto::class.java)

        /**
         * The strict JSON schema provided to the Gemini model to regulate its output fields, types, and values.
         * Enforces strict boundaries such as percentage ranges for humidity, minimum bounds for height, and
         * enumeration lists for toxicity and lighting needs.
         */
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

        /**
         * Triggers content generation for a specific plant species using Gemini AI.
         *
         * This function prepares a customized prompt, calls the Gemini model, verifies and parses the
         * resulting JSON against the established adapter, and maps the DTO into a valid [PlantKnowledge] domain model.
         * If the model call fails (e.g. due to invalid App Check tokens, network errors, or malformed JSON), it logs
         * detailed diagnostic warnings and returns a safe fallback [PlantKnowledge] model to prevent application crashes
         * and ensure care scheduling can continue.
         *
         * @param scientificName The unique botanical or scientific name of the plant.
         * @param commonName An optional colloquial or common name to give the AI additional context.
         * @return A valid [PlantKnowledge] domain model (generated or fallback defaults).
         * @throws CancellationException if the underlying coroutine or network request is cancelled.
         */
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
                    Log.w(TAG, "Gemini returned empty response for $cleanScientificName. Using fallback.")
                    return createFallbackKnowledge(cleanScientificName, commonName)
                }

                Log.d(TAG, "Raw Gemini JSON: $responseJson")

                val dto =
                    try {
                        jsonAdapter.fromJson(responseJson)
                    } catch (e: Exception) {
                        Log.e(TAG, "JSON Parsing failed for $cleanScientificName. JSON: $responseJson", e)
                        null
                    } ?: return createFallbackKnowledge(cleanScientificName, commonName)

                val knowledge = dto.toDomain(cleanScientificName, commonName)
                if (knowledge != null) {
                    Log.d(TAG, "Successfully generated domain model for $cleanScientificName")
                    knowledge
                } else {
                    Log.w(TAG, "toDomain() returned null for $cleanScientificName. Using fallback.")
                    createFallbackKnowledge(cleanScientificName, commonName)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e(TAG, "Gemini generation failed for $cleanScientificName: ${e.message}", e)
                if (e.message?.contains("App Check", ignoreCase = true) == true ||
                    e.message?.contains("AppCheck", ignoreCase = true) == true
                ) {
                    Log.w(
                        TAG,
                        "Firebase App Check token validation failed for $cleanScientificName. " +
                            "If testing in debug mode, register your Debug App Check secret token in the Firebase Console.",
                    )
                }
                createFallbackKnowledge(cleanScientificName, commonName)
            }
        }

        /**
         * Creates a safe default [PlantKnowledge] model when AI generation fails or is restricted (e.g. App Check / offline).
         *
         * @param scientificName Scientific name of the species.
         * @param commonName Optional common name.
         * @return Conservative [PlantKnowledge] model with default care parameters.
         */
        private fun createFallbackKnowledge(
            scientificName: String,
            commonName: String?,
        ): PlantKnowledge =
            PlantKnowledge(
                speciesInfo =
                    com.traffipart.polanty.domain.model.PlantSpeciesInfo(
                        scientificName = scientificName,
                        commonName = commonName,
                        description = "Botanical care information for $scientificName.",
                        origin = null,
                        toxicity =
                            com.traffipart.polanty.domain.model.PlantToxicity(
                                pets = ToxicityLevel.Unknown,
                                humans = ToxicityLevel.Unknown,
                                notes = "Toxicity details unavailable.",
                            ),
                        typicalHeightCmMin = null,
                        typicalHeightCmMax = null,
                    ),
                careProfile =
                    com.traffipart.polanty.domain.model.PlantCareProfile(
                        scientificName = scientificName,
                        watering =
                            com.traffipart.polanty.domain.model.WateringProfile(
                                soilCheckIntervalDaysMin = 7,
                                soilCheckIntervalDaysMax = 10,
                                instruction = "Check the top inch of soil before watering.",
                            ),
                        light = LightRequirement.MediumIndirect,
                        humidity = com.traffipart.polanty.domain.model.HumidityRange(minPercent = 40, maxPercent = 60),
                        temperature = com.traffipart.polanty.domain.model.TemperatureRange(minCelsius = 18.0, maxCelsius = 26.0),
                        fertilizing = "Fertilize lightly during the active growing season.",
                    ),
            )

        /**
         * Constructs a highly-structured and detailed textual prompt instructing the AI on how to assemble its response.
         *
         * Enforces rules like metric constraints (centimeters, Celsius), conservative estimates, safe fallback phrases,
         * and the exact vocabulary for enum classifications.
         *
         * @param scientificName The precise botanical name to write into the prompt requirements.
         * @param commonName Contextual common name info, incorporated to help the AI narrow down the correct species.
         * @return A fully formatted string containing instructions for the generative model.
         */
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
