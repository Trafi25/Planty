package com.traffipart.polanty.domain.error

/** Failures specific to health assessment, including unusable images and provider failures. */
sealed interface PlantHealthError {
    data object InvalidImage : PlantHealthError

    data object NoPlantDetected : PlantHealthError

    data object NoInternet : PlantHealthError

    data object Unauthorized : PlantHealthError

    data object RequestTimeout : PlantHealthError

    data object TooManyRequests : PlantHealthError

    data object ServerError : PlantHealthError

    data object InvalidResponse : PlantHealthError

    data object Unknown : PlantHealthError
}
