package com.alon.plantpulse.usergarden.application.model

/**
 * Sealed class representing potential errors that can occur in the plants detail feature.
 */
sealed class PlantsDetailError : Exception() {

    /**
     * Error indicating a problem with the device's internet connection.
     */
    class DeviceConnection : PlantsDetailError()

    /**
     * Error indicating that the remote news server returned an error or is unreachable.
     */
    class RemoteServer : PlantsDetailError()

    /**
     * Represents an internal or unexpected error, wrapping the original [Throwable].
     *
     * @property error The original exception that caused the internal error.
     */
    data class Internal(val error: Throwable) : PlantsDetailError()

    class EmptySearchQuery : PlantsDetailError()
}