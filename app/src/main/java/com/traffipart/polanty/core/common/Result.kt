package com.traffipart.polanty.core.common

/**
 * A discriminated union representing either a success result with data [D] or an error with failure details [E].
 */
sealed interface Result<out D, out E> {
    /**
     * Represents a successful outcome containing [data].
     */
    data class Success<D>(
        val data: D,
    ) : Result<D, Nothing>

    /**
     * Represents an error outcome containing [error].
     */
    data class Error<E>(
        val error: E,
    ) : Result<Nothing, E>
}
