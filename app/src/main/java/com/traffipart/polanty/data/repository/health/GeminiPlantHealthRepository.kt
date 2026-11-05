package com.traffipart.polanty.data.repository.health

import android.graphics.BitmapFactory
import android.util.Log
import com.google.firebase.Firebase
import com.google.firebase.ai.ai
import com.google.firebase.ai.type.APINotConfiguredException
import com.google.firebase.ai.type.GenerativeBackend
import com.google.firebase.ai.type.InvalidAPIKeyException
import com.google.firebase.ai.type.PermissionMissingException
import com.google.firebase.ai.type.PromptBlockedException
import com.google.firebase.ai.type.QuotaExceededException
import com.google.firebase.ai.type.RequestTimeoutException
import com.google.firebase.ai.type.ResponseStoppedException
import com.google.firebase.ai.type.Schema
import com.google.firebase.ai.type.SerializationException
import com.google.firebase.ai.type.ServerException
import com.google.firebase.ai.type.ServiceDisabledException
import com.google.firebase.ai.type.UnknownException
import com.google.firebase.ai.type.content
import com.google.firebase.ai.type.generationConfig
import com.squareup.moshi.Moshi
import com.traffipart.polanty.core.common.Result
import com.traffipart.polanty.core.common.trimToNull
import com.traffipart.polanty.data.remote.health.dto.GeminiPlantHealthAssessmentDto
import com.traffipart.polanty.data.remote.mapper.toDomain
import com.traffipart.polanty.domain.error.PlantHealthError
import com.traffipart.polanty.domain.model.health.PlantHealthAssessment
import com.traffipart.polanty.domain.model.health.PlantHealthIssueCategory
import com.traffipart.polanty.domain.model.health.PlantHealthStatus
import com.traffipart.polanty.domain.model.plant.PlantImage
import com.traffipart.polanty.domain.repository.health.PlantHealthRepository
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import javax.inject.Inject
import kotlin.coroutines.cancellation.CancellationException

/**
 * Gemini-powered implementation of [PlantHealthRepository].
 *
 * This repository sends a plant image together with optional known species context
 * to Firebase AI Logic and requests a structured health assessment.
 *
 * Responsibilities:
 * - Decode [PlantImage] bytes into an Android Bitmap.
 * - Build the multimodal Gemini request.
 * - Enforce structured JSON output with [responseSchema].
 * - Parse provider JSON into [GeminiPlantHealthAssessmentDto].
 * - Map the DTO into the provider-independent [PlantHealthAssessment] domain model.
 * - Convert Firebase/network failures into [PlantHealthError].
 *
 * Business/UI logic should remain outside this class.
 */
class GeminiPlantHealthRepository
@Inject
constructor(
    private val moshi: Moshi,
) : PlantHealthRepository {

    /**
     * Moshi adapter for converting Gemini's structured JSON response
     * into our data-layer DTO.
     */
    private val jsonAdapter =
        moshi.adapter(
            GeminiPlantHealthAssessmentDto::class.java,
        )

    /**
     * Defines exactly what JSON structure Gemini is allowed to return.
     *
     * Firebase AI Logic considers object properties required unless
     * explicitly marked optional, so the model is expected to provide
     * every field listed here.
     */
    private val responseSchema =
        Schema.obj(
            properties =
                mapOf(
                    "plantDetected" to
                            Schema.boolean(
                                description =
                                    "True only when a real plant is clearly visible in the image.",
                            ),
                    "status" to
                            Schema.enumeration(
                                values =
                                    PlantHealthStatus.entries
                                        .map { status ->
                                            status.name
                                        },
                                description =
                                    "Overall visible health state of the plant.",
                            ),
                    "summary" to
                            Schema.string(
                                description =
                                    "A short conservative summary of the visible plant health.",
                            ),
                    "findings" to
                            Schema.array(
                                items =
                                    Schema.obj(
                                        properties =
                                            mapOf(
                                                "category" to
                                                        Schema.enumeration(
                                                            values =
                                                                PlantHealthIssueCategory.entries
                                                                    .map { category ->
                                                                        category.name
                                                                    },
                                                            description =
                                                                "Most appropriate category for this visible finding.",
                                                        ),
                                                "title" to
                                                        Schema.string(
                                                            description =
                                                                "Short human-readable title for the finding.",
                                                        ),
                                                "evidence" to
                                                        Schema.string(
                                                            description =
                                                                "Visible evidence in the image supporting this finding.",
                                                        ),
                                            ),
                                    ),
                            ),
                    "careRecommendations" to
                            Schema.array(
                                items =
                                    Schema.string(
                                        description =
                                            "A practical, conservative next care step.",
                                    ),
                            ),
                ),
        )

    /**
     * Gemini model used for health analysis.
     *
     * I keep gemini-3.6-flash here because that is the model your existing
     * GeminiPlantKnowledgeGenerator currently uses.
     *
     * Later it would be better to move the model name into one shared
     * configuration instead of duplicating it across repositories.
     */
    private val model by lazy {
        Firebase
            .ai(
                backend =
                    GenerativeBackend.googleAI(),
            )
            .generativeModel(
                modelName = MODEL_NAME,
                generationConfig =
                    generationConfig {
                        responseMimeType =
                            "application/json"

                        responseSchema =
                            this@GeminiPlantHealthRepository
                                .responseSchema
                    },
            )
    }

    /**
     * Analyzes visible plant health from an image.
     *
     * The known scientific name is optional. When available, it gives Gemini
     * useful species context without asking the model to identify the plant again.
     *
     * @param image Image to analyze.
     * @param scientificName Known species name, if Planty already knows it.
     *
     * @return Successful [PlantHealthAssessment] or a domain-level [PlantHealthError].
     */
    override suspend fun assessPlantHealth(
        image: PlantImage,
        scientificName: String?,
    ): Result<
            PlantHealthAssessment,
            PlantHealthError,
            > {
        val bitmap =
            BitmapFactory.decodeByteArray(
                image.bytes,
                0,
                image.bytes.size,
            )
                ?: return Result.Error(
                    PlantHealthError.InvalidImage,
                )

        val cleanScientificName =
            scientificName.trimToNull()

        val prompt =
            createPrompt(
                scientificName =
                    cleanScientificName,
            )

        return try {
            /**
             * This is the multimodal part.
             *
             * One Gemini request receives:
             *
             * Bitmap
             * +
             * textual instructions/species context
             */
            val requestContent =
                content {
                    image(bitmap)

                    text(prompt)
                }

            val response =
                model.generateContent(
                    requestContent,
                )

            val responseJson =
                response.text
                    .trimToNull()
                    ?: return Result.Error(
                        PlantHealthError.InvalidResponse,
                    )

            Log.d(
                TAG,
                "Gemini health JSON: $responseJson",
            )

            val dto =
                try {
                    jsonAdapter.fromJson(
                        responseJson,
                    )
                } catch (
                    e: Exception,
                ) {
                    Log.e(
                        TAG,
                        "Failed to parse plant health JSON",
                        e,
                    )

                    null
                }
                    ?: return Result.Error(
                        PlantHealthError.InvalidResponse,
                    )

            /**
             * The model may correctly determine that the uploaded image
             * does not actually contain a visible plant.
             *
             * This is different from malformed JSON or a network failure.
             */
            if (!dto.plantDetected) {
                return Result.Error(
                    PlantHealthError.NoPlantDetected,
                )
            }

            val assessment =
                dto.toDomain()
                    ?: return Result.Error(
                        PlantHealthError.InvalidResponse,
                    )

            Result.Success(
                assessment,
            )
        } catch (
            e: CancellationException,
        ) {
            /**
             * Coroutine cancellation is not a normal application error.
             *
             * For example:
             * - screen disappeared
             * - coroutine scope was cancelled
             *
             * It must propagate instead of becoming PlantHealthError.Unknown.
             */
            throw e
        } catch (
            _: UnknownHostException,
        ) {
            Result.Error(
                PlantHealthError.NoInternet,
            )
        } catch (
            _: SocketTimeoutException,
        ) {
            Result.Error(
                PlantHealthError.RequestTimeout,
            )
        } catch (
            _: RequestTimeoutException,
        ) {
            Result.Error(
                PlantHealthError.RequestTimeout,
            )
        } catch (
            _: QuotaExceededException,
        ) {
            Result.Error(
                PlantHealthError.TooManyRequests,
            )
        } catch (
            _: PermissionMissingException,
        ) {
            Result.Error(
                PlantHealthError.Unauthorized,
            )
        } catch (
            _: InvalidAPIKeyException,
        ) {
            Result.Error(
                PlantHealthError.Unauthorized,
            )
        } catch (
            _: APINotConfiguredException,
        ) {
            Result.Error(
                PlantHealthError.Unauthorized,
            )
        } catch (
            _: ServiceDisabledException,
        ) {
            Result.Error(
                PlantHealthError.Unauthorized,
            )
        } catch (
            _: SerializationException,
        ) {
            Result.Error(
                PlantHealthError.InvalidResponse,
            )
        } catch (
            _: PromptBlockedException,
        ) {
            Result.Error(
                PlantHealthError.InvalidResponse,
            )
        } catch (
            _: ResponseStoppedException,
        ) {
            Result.Error(
                PlantHealthError.InvalidResponse,
            )
        } catch (
            _: ServerException,
        ) {
            Result.Error(
                PlantHealthError.ServerError,
            )
        } catch (
            _: UnknownException,
        ) {
            Result.Error(
                PlantHealthError.Unknown,
            )
        } catch (
            e: Exception,
        ) {
            Log.e(
                TAG,
                "Plant health assessment failed",
                e,
            )

            Result.Error(
                PlantHealthError.Unknown,
            )
        }
    }

    /**
     * Creates the textual part of the multimodal request.
     *
     * The main goal here is not only to tell Gemini what to return,
     * because [responseSchema] already controls the output structure.
     *
     * The prompt primarily controls the model's behavior:
     * - remain conservative,
     * - acknowledge uncertainty,
     * - avoid inventing diseases,
     * - distinguish observations from likely causes.
     */
    private fun createPrompt(
        scientificName: String?,
    ): String {
        val speciesContext =
            scientificName
                ?.let { name ->
                    """
                        Planty already knows the plant species.

                        Scientific name: $name

                        Treat this scientific name as known context.
                        Do not replace it with another species based on the image.
                        """.trimIndent()
                }
                ?: """
                        The exact plant species is not known.

                        Do not invent a species identification.
                        Focus primarily on visible plant-health symptoms.
                    """.trimIndent()

        return """
                Assess the visible health of the plant in the provided image.

                $speciesContext

                First determine whether a real plant is clearly visible.

                If no plant is clearly visible:
                - plantDetected must be false.
                - status must be Uncertain.
                - keep findings empty.
                - keep careRecommendations empty.
                - briefly explain that the image does not clearly show a plant.

                If a plant is visible:

                Evaluate only evidence that can reasonably be observed from the image.

                Possible issue categories:
                - Watering
                - Light
                - Pest
                - Disease
                - Nutrition
                - PhysicalDamage
                - Unknown

                Important rules:

                - Do not claim a definitive disease when several causes could produce
                  the same visual symptom.
                - Use Uncertain if the image quality or visible symptoms are insufficient.
                - A healthy-looking plant may be marked Healthy.
                - Use NeedsAttention when visible symptoms reasonably suggest that
                  care should be investigated.
                - Do not invent roots, soil moisture, pests, spots, discoloration,
                  or other symptoms that are not visible.
                - Clearly distinguish visible evidence from the likely explanation.
                - Prefer Unknown when no specific category can be supported.
                - Recommendations must be conservative and low-risk.
                - Do not recommend aggressive pesticide or fungicide treatment based
                  only on uncertain visual evidence.
                - Prefer actions such as inspecting leaves, checking soil moisture,
                  isolating a possibly pest-affected plant, adjusting light gradually,
                  or monitoring symptoms.
                - Provide no more than 5 findings.
                - Provide no more than 5 care recommendations.
                - Keep the summary concise and useful.
            """.trimIndent()
    }

    private companion object {
        const val TAG =
            "GeminiPlantHealth"

        const val MODEL_NAME =
            "gemini-3.6-flash"
    }
}