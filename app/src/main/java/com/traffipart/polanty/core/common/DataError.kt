package com.traffipart.polanty.core.common

sealed interface DataError {
    data object NoInternet : DataError

    data object Unauthorized : DataError

    data object RequestTimeout : DataError

    data object TooManyRequests : DataError

    data object ServerError : DataError

    data object InvalidImage : DataError

    data object Serialization : DataError

    data object Unknown : DataError
}

fun DataError.toMessage(): String =
    when (this) {
        DataError.NoInternet ->
            "No internet connection"

        DataError.Unauthorized ->
            "Plant identification request was not authorized"

        DataError.RequestTimeout ->
            "The request took too long. Please try again"

        DataError.TooManyRequests ->
            "Too many identification requests. Please try again later"

        DataError.ServerError ->
            "Plant identification service is temporarily unavailable"

        DataError.InvalidImage ->
            "Please choose a valid plant image"

        DataError.Serialization ->
            "Could not read the server response"

        DataError.Unknown ->
            "Something went wrong"
    }
