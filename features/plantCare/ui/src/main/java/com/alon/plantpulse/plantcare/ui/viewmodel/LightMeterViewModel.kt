package com.alon.plantpulse.plantcare.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alon.plantpulse.common.application.Result
import com.alon.plantpulse.plantcare.application.usecase.DiagnosePlantLightUseCase
import com.alon.plantpulse.plantcare.application.usecase.GetPlantCareDiagnosticUseCase
import com.alon.plantpulse.plantcare.ui.model.LightMeterStatusUiState
import com.alon.plantpulse.plantcare.ui.model.PlantCareDiagnosticUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the Light Meter screen.
 *
 * This ViewModel coordinates the retrieval of plant care requirements and real-time
 * light sensor diagnostics to provide a comprehensive light sufficiency analysis.
 *
 * @property diagnosePlantLightUseCase Use case for real-time light intensity diagnosis.
 * @property getPlantCareDiagnosticUseCase Use case for retrieving plant-specific care data.
 * @property savedStateHandle Handle for state persistence and navigation argument retrieval.
 */
@HiltViewModel
class LightMeterViewModel @Inject constructor(
    private val diagnosePlantLightUseCase: DiagnosePlantLightUseCase,
    private val getPlantCareDiagnosticUseCase: GetPlantCareDiagnosticUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val plantId: Int = checkNotNull(savedStateHandle["plantId"])

    private val _meterStatus = MutableLiveData<LightMeterStatusUiState>()

    /**
     * Observable state for real-time light status (lux value and categorization).
     */
    val meterStatus: LiveData<LightMeterStatusUiState> = _meterStatus

    private val _plantDiagnostic = MutableLiveData<PlantCareDiagnosticUiState>()

    /**
     * Observable state for static plant identity and recommended light range.
     */
    val plantDiagnostic: LiveData<PlantCareDiagnosticUiState> = _plantDiagnostic

    init {
        loadPlantInfo()
        startLightDiagnosis()
    }

    /**
     * Loads static information about the plant being diagnosed.
     */
    private fun loadPlantInfo() {
        viewModelScope.launch {
            when (val result = getPlantCareDiagnosticUseCase(plantId)) {
                is Result.Success -> {
                    val data = result.data
                    _plantDiagnostic.value = PlantCareDiagnosticUiState(
                        plantName = data.commonName,
                        recommendedRange = "${data.recommendedLightRange.minLux.toInt()} ~ ${data.recommendedLightRange.maxLux.toInt()} Lux"
                    )
                }
                is Result.Failure -> {
                    _plantDiagnostic.value = PlantCareDiagnosticUiState(error = result.error)
                }
            }
        }
    }

    /**
     * Begins streaming real-time diagnostic updates from the light sensor.
     */
    private fun startLightDiagnosis() {
        viewModelScope.launch {
            diagnosePlantLightUseCase(plantId).collect { result ->
                when (result) {
                    is Result.Success -> {
                        val diagnostic = result.data
                        _meterStatus.value = LightMeterStatusUiState(
                            luxValue = "%,d".format(diagnostic.lux.toInt()),
                            status = diagnostic.status.name.lowercase().replaceFirstChar { it.uppercase() }
                        )
                    }
                    is Result.Failure -> {
                        _meterStatus.value = LightMeterStatusUiState(error = result.error)
                    }
                }
            }
        }
    }
}
