package com.alon.plantpulse.usergarden.ui.model

import com.alon.plantpulse.usergarden.application.model.UserGardenError

sealed class AddPlantUiState {

    object Success : AddPlantUiState()

    data class Error(val error: UserGardenError) : AddPlantUiState()
}
