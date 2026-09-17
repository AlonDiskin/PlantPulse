package com.alon.plantpulse.usergarden.ui.model

import com.alon.plantpulse.usergarden.application.model.UserGardenError

/**
 * Represents the UI state for the deletion process of a user's plant.
 */
sealed class UserPlantDeleteUiState {
    /** Initial state when no deletion is in progress. */
    object Idle : UserPlantDeleteUiState()

    /** State indicating that a deletion operation is currently in progress. */
    object Loading : UserPlantDeleteUiState()

    /** State indicating that the plant was successfully deleted. */
    object Success : UserPlantDeleteUiState()

    /**
     * State indicating that the deletion operation failed.
     * @property error The error that occurred during deletion.
     */
    data class Error(val error: UserGardenError) : UserPlantDeleteUiState()
}
