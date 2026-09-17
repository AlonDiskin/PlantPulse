package com.alon.plantpulse.usergarden.ui.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.lifecycle.SavedStateHandle
import com.alon.plantpulse.usergarden.application.model.Result
import com.alon.plantpulse.usergarden.application.model.UserPlantDetailDto
import com.alon.plantpulse.usergarden.application.usecase.DeleteUserPlantUseCase
import com.alon.plantpulse.usergarden.application.usecase.GetUserPlantDataUseCase
import com.alon.plantpulse.usergarden.ui.model.UserPlantDeleteUiState
import com.alon.plantpulse.usergarden.ui.model.UserPlantDetailUiState
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.coVerify
import org.robolectric.annotation.LooperMode

/**
 * Unit tests for [UserPlantDetailViewModel].
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
class UserPlantDetailViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = StandardTestDispatcher()
    
    // Collaborators
    private val mockGetUserPlantDataUseCase = mockk<GetUserPlantDataUseCase>()
    private val mockDeleteUserPlantUseCase = mockk<DeleteUserPlantUseCase>()
    private val detailObserver = mockk<Observer<UserPlantDetailUiState>>(relaxed = true)
    private val deleteObserver = mockk<Observer<UserPlantDeleteUiState>>(relaxed = true)

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun loadUserPlant_WhenCreated_AndUpdateUiState() = runTest {
        // Given a stub UserPlantDetailDto
        val plantId = 1
        val dto = UserPlantDetailDto(
            id = plantId,
            commonName = "Monstera",
            scientificName = "Monstera deliciosa",
            category = null,
            imageUrl = "url",
            subcategory = "Aroid",
            daysToGermination = null,
            daysToMaturity = null,
            germinationSoilTemp = null,
            matureHeight = null,
            matureWidth = null,
            bloomSeason = null,
            rowSpacing = null,
            sunCare = null,
            waterCare = null,
            directions = "Indirect light"
        )
        coEvery { mockGetUserPlantDataUseCase(plantId) } returns Result.Success(dto)
        val savedStateHandle = SavedStateHandle(mapOf("plantId" to plantId))

        // When view model is created
        val viewModel = UserPlantDetailViewModel(mockGetUserPlantDataUseCase, mockDeleteUserPlantUseCase, savedStateHandle)
        viewModel.uiState.observeForever(detailObserver)
        
        testDispatcher.scheduler.advanceUntilIdle()

        // Then view model should invoke GetUserPlantDataUseCase to load the plant dto
        coVerify { mockGetUserPlantDataUseCase(plantId) }

        // And set mapped UserPlantDetailUiState to an observable live data in the view model
        val state = viewModel.uiState.value
        assertThat(state).isNotNull()
        assertThat(state?.commonName).isEqualTo("Monstera")
        assertThat(state?.scientificName).isEqualTo("Monstera deliciosa")
        
        verify { detailObserver.onChanged(any()) }
        
        viewModel.uiState.removeObserver(detailObserver)
    }

    @Test
    fun deletePlantFromUserGarden_WhenRequestedByView() = runTest {
        // Given a view model with a plant id
        val plantId = 1

        coEvery { mockGetUserPlantDataUseCase(plantId) } returns Result.Failure(mockk(relaxed = true))
        coEvery { mockDeleteUserPlantUseCase(plantId) } returns Result.Success(Unit)
        
        val savedStateHandle = SavedStateHandle(mapOf("plantId" to plantId))
        val viewModel = UserPlantDetailViewModel(mockGetUserPlantDataUseCase, mockDeleteUserPlantUseCase, savedStateHandle)
        viewModel.deleteUiState.observeForever(deleteObserver)

        // When view model is invoked by view to delete the plant
        viewModel.deletePlant()
        testDispatcher.scheduler.advanceUntilIdle()
        
        // Then view model should invoke DeleteUserPlantUseCase to delete the plant
        coVerify { mockDeleteUserPlantUseCase(plantId) }
        
        // When plant is deleted successfully, view model should update UserPlantDeleteUiState
        verify { deleteObserver.onChanged(UserPlantDeleteUiState.Success) }
        
        viewModel.deleteUiState.removeObserver(deleteObserver)
    }
}
