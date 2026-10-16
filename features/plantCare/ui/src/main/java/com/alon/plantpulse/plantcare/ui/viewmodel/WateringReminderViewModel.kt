package com.alon.plantpulse.plantcare.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import com.alon.plantpulse.common.application.Result
import com.alon.plantpulse.plantcare.application.model.PlantCareError
import com.alon.plantpulse.plantcare.application.model.WateringReminderStatus
import com.alon.plantpulse.plantcare.application.usecase.GetWateringReminderStatusUseCase
import com.alon.plantpulse.plantcare.ui.model.WateringReminderUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * ViewModel for managing watering reminders for a specific plant.
 *
 * @property getStatusUseCase Use case to retrieve the current reminder status.
 * @property savedStateHandle Handle to access navigation arguments (e.g., plantId).
 */
@HiltViewModel
class WateringReminderViewModel @Inject constructor(
    getStatusUseCase: GetWateringReminderStatusUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val plantId: Int = checkNotNull(savedStateHandle["plantId"])

    /**
     * Observable UI state representing the plant's watering reminder information.
     */
    val uiState: LiveData<WateringReminderUiState> = getStatusUseCase(plantId)
        .map(::mapResultToUiState)
        .asLiveData()

    private fun mapResultToUiState(result: Result<WateringReminderStatus, PlantCareError>): WateringReminderUiState {
        return when (result) {
            is Result.Success -> {
                when (val status = result.data) {
                    is WateringReminderStatus.NotScheduled -> {
                        WateringReminderUiState(
                            plantName = status.plantName,
                            recommendedWatering = "Recommended: Every ${status.recommendedFrequency.minDays}-${status.recommendedFrequency.maxDays} days"
                        )
                    }
                    is WateringReminderStatus.Scheduled -> {
                        WateringReminderUiState(
                            plantName = status.schedule.plantName,
                            recommendedWatering = "Recommended: Every ${status.recommendedFrequency.minDays}-${status.recommendedFrequency.maxDays} days"
                        )
                    }
                }
            }
            is Result.Failure -> {
                WateringReminderUiState(error = result.error)
            }
        }
    }
}
