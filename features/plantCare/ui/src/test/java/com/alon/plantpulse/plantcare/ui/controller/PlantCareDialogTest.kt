package com.alon.plantpulse.plantcare.ui.controller

import android.os.Looper
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.core.os.bundleOf
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alon.plantpulse.common.ui.HiltTestActivity
import com.alon.plantpulse.common.ui.launchFragmentInHiltContainer
import com.alon.plantpulse.plantcare.ui.R
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows
import org.robolectric.annotation.LooperMode

/**
 * Unit tests for [PlantCareDialog] using Robolectric.
 */
@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
class PlantCareDialogTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val plantId = 42
    private lateinit var navController: TestNavHostController

    @Before
    fun setUp() {
        navController = TestNavHostController(ApplicationProvider.getApplicationContext())
    }

    @Test
    fun whenUserSelectToCloseDialog_ShouldDismissDialog() {
        // Given a launched dialog fragment
        val scenario = launchFragmentInHiltContainer<PlantCareDialog>(
            fragmentArgs = bundleOf("plantId" to plantId)
        )
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // When user clicks the close button
        onView(withId(R.id.btn_close)).perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then dialog should be dismissed (removed from fragment manager in this test setup)
        scenario.onActivity { activity ->
            val fragment = activity.supportFragmentManager.fragments.firstOrNull()
            assertThat(fragment).isNull()
        }
    }

    @Test
    fun whenUserSelectToOpenLightMeterScreen_ShouldNavigateToLightMeterFragment() {
        // Given a launched dialog fragment with a valid plantId and navigation set up
        launchFragmentInHiltContainer<PlantCareDialog>(
            fragmentArgs = bundleOf("plantId" to plantId),
            navController = navController
        ) {
            navController.setGraph(R.navigation.plant_care_nav_graph)
            // Start at the implicit parent or just assume it's showing the dialog
        }
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // When user clicks the light meter button
        onView(withId(R.id.btn_light_meter)).perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then app should navigate to light meter fragment
        assertThat(navController.currentDestination?.id).isEqualTo(R.id.lightMeterFragment)
        
        // And pass the correct plantId as an argument
        val args = navController.backStack.last().arguments
        assertThat(args?.getInt("plantId")).isEqualTo(plantId)
    }
}
