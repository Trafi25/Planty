package com.traffipart.polanty.domain.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * Results of a plant image identification operation.
 *
 * @property bestMatch Best matching scientific name identified by the system.
 * @property candidates Prioritized list of candidate matches sorted by confidence score.
 */
data class PlantIdentification(
    val bestMatch: String?,
    val candidates: List<PlantCandidate>,
)

/**
 * An individual plant match candidate from identification services.
 *
 * @property scientificName Candidate's scientific botanical name.
 * @property commonName Candidate's common name, if available.
 * @property confidence Confidence score between 0.0 and 1.0.
 */
@Parcelize
data class PlantCandidate(
    val scientificName: String,
    val commonName: String?,
    val confidence: Double,
) : Parcelable
