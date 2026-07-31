package com.alon.plantpulse.usergarden.application.model

/**
 * Defines the hierarchy of specific error types that can occur within the user garden feature.
 *
 * This sealed class enables exhaustive error handling, allowing the UI to react differently
 * based on the specific nature of the failure.
 */
sealed class UserGardenError : Exception() {

    /**
     * Represents an internal or unexpected error, typically wrapping a lower-level exception.
     *
     * @property error The original [Throwable] that caused this internal failure.
     */
    data class Internal(val error: Throwable) : UserGardenError()

    /**
     * Indicates that a search operation was attempted with an empty or invalid query.
     */
    class EmptySearchQuery : UserGardenError()
}
