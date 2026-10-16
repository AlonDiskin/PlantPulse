package com.alon.plantpulse.plantcare.ui.controller

import android.os.Looper
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alon.plantpulse.common.ui.HiltTestActivity
import com.alon.plantpulse.common.ui.launchFragmentInHiltContainer
import com.alon.plantpulse.plantcare.ui.R
import com.alon.plantpulse.plantcare.application.model.PlantCareError
import com.alon.plantpulse.plantcare.ui.model.WateringReminderUiState
import com.alon.plantpulse.plantcare.ui.viewmodel.WateringReminderViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows
import org.robolectric.annotation.LooperMode

/**
 * Unit tests for [WateringReminderFragment] using Robolectric.
 */
@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
class WateringReminderFragmentTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // Activity scenario to host fragment under test.
    private lateinit var scenario: ActivityScenario<HiltTestActivity>

    // Collaborators
    private val mockViewModel: WateringReminderViewModel = mockk(relaxed = true)
    private val uiStateLiveData = MutableLiveData<WateringReminderUiState>()

    @Before
    fun setUp() {
        // Stub view model creation with test mock
        mockkConstructor(ViewModelLazy::class)
        every { anyConstructed<ViewModelLazy<ViewModel>>().value } returns mockViewModel

        // Stub the LiveData member
        every { mockViewModel.uiState } returns uiStateLiveData

        // Launch fragment under test
        scenario = launchFragmentInHiltContainer<WateringReminderFragment>()
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }
    
    @Test
    fun whenCreated_AndDataLoaded_ShouldDisplayPlantNameAndRecommendedWatering() {
        // Given
        val state = WateringReminderUiState(
            plantName = "Snake Plant",
            recommendedWatering = "Recommended: Every 14-21 days"
        )

        // When
        scenario.onActivity {
            uiStateLiveData.value = state
        }
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then
        onView(withId(R.id.tv_plant_name)).check(matches(withText(state.plantName)))
        onView(withId(R.id.tv_recommended_watering)).check(matches(withText(state.recommendedWatering)))
    }

    @Test
    fun whenCreated_AndErrorOccurs_ShouldShowErrorMessage() {
        // Given
        val error = PlantCareError.Internal(Exception("Test error"))
        val state = WateringReminderUiState(error = error)

        // When
        scenario.onActivity {
            uiStateLiveData.value = state
        }
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then
        onView(withText(R.string.error_message_internal_error)).check(matches(isDisplayed()))
    }
}
