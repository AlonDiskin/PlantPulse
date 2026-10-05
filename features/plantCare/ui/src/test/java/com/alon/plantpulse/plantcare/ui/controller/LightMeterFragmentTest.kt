package com.alon.plantpulse.plantcare.ui.controller

import android.os.Looper
import android.view.View
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
import com.alon.plantpulse.plantcare.ui.model.LightMeterStatusUiState
import com.alon.plantpulse.plantcare.ui.model.PlantCareDiagnosticUiState
import com.alon.plantpulse.plantcare.ui.viewmodel.LightMeterViewModel
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows
import org.robolectric.annotation.LooperMode

/**
 * Unit tests for [LightMeterFragment] using Robolectric.
 */
@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
class LightMeterFragmentTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // Activity scenario to host fragment under test.
    private lateinit var scenario: ActivityScenario<HiltTestActivity>

    // Collaborators
    private val mockViewModel: LightMeterViewModel = mockk(relaxed = true)
    private val plantDiagnosticLiveData = MutableLiveData<PlantCareDiagnosticUiState>()
    private val meterStatusLiveData = MutableLiveData<LightMeterStatusUiState>()

    @Before
    fun setUp() {
        // Stub view model creation with test mock
        mockkConstructor(ViewModelLazy::class)
        every { anyConstructed<ViewModelLazy<ViewModel>>().value } returns mockViewModel

        // Stub the LiveData members
        every { mockViewModel.plantDiagnostic } returns plantDiagnosticLiveData
        every { mockViewModel.meterStatus } returns meterStatusLiveData

        // Launch fragment under test
        scenario = launchFragmentInHiltContainer<LightMeterFragment>()
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }
    
    @Test
    fun whenCreated_ShouldDisplayPlantNameAndRecommendedRange() {
        // Given
        val state = PlantCareDiagnosticUiState(
            plantName = "Monstera",
            recommendedRange = "1,000 ~ 5,000 Lux"
        )
        
        // When
        scenario.onActivity {
            plantDiagnosticLiveData.value = state
        }
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then
        onView(withId(R.id.tv_plant_name)).check(matches(withText(state.plantName)))
        onView(withId(R.id.tv_recommended_range)).check(matches(withText(state.recommendedRange)))
    }

    @Test
    fun whenMeterStatusUpdates_ShouldDisplayLuxValueAndStatusLabel() {
        // Given
        val state = LightMeterStatusUiState(
            luxValue = "1,250",
            status = "Sufficient"
        )

        // When
        scenario.onActivity {
            meterStatusLiveData.value = state
        }
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then
        onView(withId(R.id.tv_current_lux)).check(matches(withText(state.luxValue)))
        onView(withId(R.id.tv_light_status)).check(matches(withText(state.status)))
    }

    @Test
    fun whenMeterStatusUpdates_ShouldUpdateProgressBarProgress() {
        // Given
        val state = LightMeterStatusUiState(
            luxValue = "1,250",
            status = "Sufficient"
        )

        // When
        scenario.onActivity {
            meterStatusLiveData.value = state
        }
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then
        onView(withId(R.id.pb_lux_meter)).check(matches(withProgress(1250)))
    }

    @Test
    fun whenPlantDiagnosticErrorOccurs_ShouldShowErrorMessage() {
        // Given
        val error = PlantCareError.Internal(Exception("Test failure"))
        val state = PlantCareDiagnosticUiState(error = error)

        // When
        scenario.onActivity {
            plantDiagnosticLiveData.value = state
        }
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then
        onView(withText(R.string.error_message_internal_error)).check(matches(isDisplayed()))
    }

    @Test
    fun whenMeterStatusErrorOccurs_ShouldShowErrorMessage() {
        // Given
        val error = PlantCareError.LightSensorNotExist
        val state = LightMeterStatusUiState(error = error)

        // When
        scenario.onActivity {
            meterStatusLiveData.value = state
        }
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then
        onView(withText(R.string.error_message_light_sensor_not_exist)).check(matches(isDisplayed()))
    }

    private fun withProgress(expectedProgress: Int) = object : androidx.test.espresso.matcher.BoundedMatcher<View, android.widget.ProgressBar>(android.widget.ProgressBar::class.java) {
        override fun describeTo(description: org.hamcrest.Description) {
            description.appendText("with progress: $expectedProgress")
        }

        override fun matchesSafely(progressBar: android.widget.ProgressBar): Boolean {
            return progressBar.progress == expectedProgress
        }
    }
}
