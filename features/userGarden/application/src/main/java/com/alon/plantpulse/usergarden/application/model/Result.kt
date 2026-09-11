package com.alon.plantpulse.usergarden.application.model

/**
 * A generic wrapper class for representing the result of an operation.
 *
 * This sealed class is used to encapsulate either a successful result containing data of type [T]
 * or a failure result containing an error of type [E].
 *
 * @param T The type of data in case of success.
 * @param E The type of error in case of failure.
 */
sealed class Result<out T : Any, out E : Any> {

    /**
     * Represents a successful operation result.
     *
     * @property data The data returned by the successful operation.
     */
    data class Success<out T : Any>(val data: T) : Result<T, Nothing>()

    /**
     * Represents a failed operation result.
     *
     * @property error The error encountered during the operation.
     */
    data class Failure<out E : Any>(val error: E) : Result<Nothing, E>()
}
