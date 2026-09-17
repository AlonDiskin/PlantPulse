package com.alon.plantpulse.usergarden.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.alon.plantpulse.usergarden.application.model.Result
import com.alon.plantpulse.usergarden.application.usecase.DeleteUserPlantUseCase
import com.alon.plantpulse.usergarden.application.usecase.GetUserPlantDataUseCase
import com.alon.plantpulse.usergarden.ui.model.UserPlantDeleteUiState
import com.alon.plantpulse.usergarden.ui.model.UserPlantDetailUiState
import com.alon.plantpulse.usergarden.ui.model.toUiState
import com.alon.plantpulse.usergarden.ui.model.toUserPlantDetailErrorUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel for the User Plant Detail screen.
 *
 * This ViewModel handles loading detailed information for a specific plant in the user's garden
 * and exposing it as UI state, as well as managing plant deletion.
 */
@HiltViewModel
class UserPlantDetailViewModel @Inject constructor(
    private val getUserPlantDataUseCase: GetUserPlantDataUseCase,
    private val deleteUserPlantUseCase: DeleteUserPlantUseCase,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val plantId: Int = checkNotNull(savedStateHandle["plantId"])

    private val _uiState = MutableLiveData<UserPlantDetailUiState>()
    val uiState: LiveData<UserPlantDetailUiState> = _uiState

    private val _deleteUiState = MutableLiveData<UserPlantDeleteUiState>(UserPlantDeleteUiState.Idle)
    val deleteUiState: LiveData<UserPlantDeleteUiState> = _deleteUiState

    init {
        loadPlantDetails(plantId)
    }

    private fun loadPlantDetails(id: Int) {
        viewModelScope.launch {
            _uiState.value = when (val result = getUserPlantDataUseCase(id)) {
                is Result.Success -> result.data.toUiState()
                is Result.Failure -> result.error.toUserPlantDetailErrorUiState()
            }
        }
    }

    /**
     * Triggers the deletion of the current plant from the user's garden.
     */
    fun deletePlant() {
        viewModelScope.launch {
            _deleteUiState.value = UserPlantDeleteUiState.Loading
            _deleteUiState.value = when (val result = deleteUserPlantUseCase(plantId)) {
                is Result.Success -> UserPlantDeleteUiState.Success
                is Result.Failure -> UserPlantDeleteUiState.Error(result.error)
            }
        }
    }
}
