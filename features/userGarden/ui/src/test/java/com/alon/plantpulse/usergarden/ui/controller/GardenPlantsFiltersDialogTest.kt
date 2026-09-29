package com.alon.plantpulse.usergarden.ui.controller

import android.content.Context
import android.os.Looper
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alon.plantpulse.plantsdetail.ui.R
import com.alon.plantpulse.usergarden.domain.BloomingSeason
import com.alon.plantpulse.usergarden.domain.PlantCategory
import com.alon.plantpulse.usergarden.domain.PlantSunCare
import com.alon.plantpulse.usergarden.domain.PlantWaterCare
import com.alon.plantpulse.usergarden.ui.HiltTestActivity
import com.alon.plantpulse.usergarden.ui.model.UserPlantsFiltersUiState
import com.alon.plantpulse.usergarden.ui.viewmodel.GardenPlantsFiltersViewModel
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.verify
import org.hamcrest.Matchers.not
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows
import org.robolectric.annotation.LooperMode

@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
class GardenPlantsFiltersDialogTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val context: Context = ApplicationProvider.getApplicationContext()

    // Activity scenario to host dialog under test.
    private lateinit var scenario: ActivityScenario<HiltTestActivity>

    // Collaborators
    private val mockViewModel: GardenPlantsFiltersViewModel = mockk(relaxed = true)

    @Before
    fun setUp() {
        // Stub view model creation with test mock
        mockkConstructor(ViewModelLazy::class)
        every { anyConstructed<ViewModelLazy<ViewModel>>().value } returns mockViewModel


        // Launch dialog under test host
        scenario = ActivityScenario.launch(HiltTestActivity::class.java)
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Test
    fun setCurrentSelections_WhenCreated() {
        // Given a current selection
        val filters = UserPlantsFiltersUiState(
            categoryFilters = setOf(PlantCategory.VEGETABLE,PlantCategory.FRUIT),
            seasonFilters = setOf(BloomingSeason.WARM),
            sunCareFilters = setOf(PlantSunCare.FULL, PlantSunCare.PARTIAL),
            waterCareFilters = setOf(PlantWaterCare.LOW)
        )

        every { mockViewModel.filters } returns filters

        // When dialog is created
        scenario.onActivity { activity ->
            val dialog = GardenPlantsFiltersDialog()
            dialog.show(activity.supportFragmentManager,"filters dialog")
        }
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then dialog should get current selection from view model
        verify { mockViewModel.filters }

        // And dialog should check all current selected filters
        onView(withId(R.id.filter_vegetable)).inRoot(isDialog()).check(matches(isChecked()))
        onView(withId(R.id.filter_fruit)).inRoot(isDialog()).check(matches(isChecked()))
        onView(withId(R.id.filter_warm)).inRoot(isDialog()).check(matches(isChecked()))
        onView(withId(R.id.filter_full_sun)).inRoot(isDialog()).check(matches(isChecked()))
        onView(withId(R.id.filter_partial_sun)).inRoot(isDialog()).check(matches(isChecked()))
        onView(withId(R.id.filter_low_water)).inRoot(isDialog()).check(matches(isChecked()))
    }

    @Test
    fun updateSelectionState_WhenFiltersSelected() {
        // Given a created dialog and user selections
        val expectedCategoryFilters = setOf(PlantCategory.VEGETABLE,PlantCategory.HERB)
        val expectedSeasonFilters = setOf(BloomingSeason.COOL,BloomingSeason.WARM)
        val expectedSunCareFilters = setOf(PlantSunCare.FULL, PlantSunCare.PARTIAL)
        val expectedWaterCareFilters = setOf(PlantWaterCare.HIGH, PlantWaterCare.LOW)

        every { mockViewModel.filters } returns UserPlantsFiltersUiState()
        every { mockViewModel.setPlantCategoryFilters(any()) } returns Unit
        every { mockViewModel.setPlantSeasonFilters(any()) } returns Unit
        every { mockViewModel.setPlantSunCareFilters(any()) } returns Unit
        every { mockViewModel.setPlantWaterCareFilters(any()) } returns Unit

        scenario.onActivity { activity ->
            val dialog = GardenPlantsFiltersDialog()
            dialog.show(activity.supportFragmentManager,"filters dialog")
        }
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // When user select filters
        onView(withId(R.id.filter_vegetable)).inRoot(isDialog()).perform(click())
        onView(withId(R.id.filter_herb)).inRoot(isDialog()).perform(click())
        onView(withId(R.id.filter_warm)).inRoot(isDialog()).perform(scrollTo()).perform(click())
        onView(withId(R.id.filter_cool)).inRoot(isDialog()).perform(click())
        onView(withId(R.id.filter_full_sun)).inRoot(isDialog()).perform(scrollTo()).perform(click())
        onView(withId(R.id.filter_partial_sun)).inRoot(isDialog()).perform(click())
        onView(withId(R.id.filter_low_water)).inRoot(isDialog()).perform(scrollTo()).perform(click())
        onView(withId(R.id.filter_hige_water)).inRoot(isDialog()).perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then dialog should update selection state on filters
        onView(withId(R.id.filter_vegetable)).inRoot(isDialog()).check(matches(isChecked()))
        onView(withId(R.id.filter_herb)).inRoot(isDialog()).check(matches(isChecked()))
        onView(withId(R.id.filter_warm)).inRoot(isDialog()).check(matches(isChecked()))
        onView(withId(R.id.filter_cool)).inRoot(isDialog()).check(matches(isChecked()))
        onView(withId(R.id.filter_full_sun)).inRoot(isDialog()).check(matches(isChecked()))
        onView(withId(R.id.filter_partial_sun)).inRoot(isDialog()).check(matches(isChecked()))
        onView(withId(R.id.filter_low_water)).inRoot(isDialog()).check(matches(isChecked()))
        onView(withId(R.id.filter_hige_water)).inRoot(isDialog()).check(matches(isChecked()))
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // And update view model selection state
        verify(exactly = 1) { mockViewModel.setPlantCategoryFilters(expectedCategoryFilters) }
        verify(exactly = 1) { mockViewModel.setPlantSeasonFilters(expectedSeasonFilters) }
        verify(exactly = 1) { mockViewModel.setPlantSunCareFilters(expectedSunCareFilters) }
        verify(exactly = 1) { mockViewModel.setPlantWaterCareFilters(expectedWaterCareFilters) }
    }

    @Test
    fun clearAllSelections_WhenClearButtonClicked() {
        // Given a created dialog
        every { mockViewModel.filters } returns UserPlantsFiltersUiState()

        scenario.onActivity { activity ->
            val dialog = GardenPlantsFiltersDialog()
            dialog.show(activity.supportFragmentManager, "filters dialog")
        }
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // When user select filters
        onView(withId(R.id.filter_vegetable)).inRoot(isDialog()).perform(click())
        onView(withId(R.id.filter_warm)).inRoot(isDialog()).perform(scrollTo(), click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // And press the 'clear' button
        onView(withId(R.id.btn_reset)).inRoot(isDialog()).perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then dialog should clear all checked filters
        onView(withId(R.id.filter_vegetable)).inRoot(isDialog()).check(matches(not(isChecked())))
        onView(withId(R.id.filter_warm)).inRoot(isDialog()).check(matches(not(isChecked())))

        // And invoke view model to clear filters state
        verify(exactly = 1) { mockViewModel.clearFilters() }
    }

    @Test
    fun navigateBackToUserGardenScreen_AndPassAllFilterSelections_WhenApplyButtonClicked() {
        // Given a created dialog
        val expectedFilters = UserPlantsFiltersUiState(
            categoryFilters = setOf(PlantCategory.VEGETABLE),
            seasonFilters = setOf(BloomingSeason.COOL)
        )
        every { mockViewModel.filters } returns expectedFilters

        val navController = TestNavHostController(context)
        navController.setViewModelStore(androidx.lifecycle.ViewModelStore())

        scenario.onActivity { activity ->
            navController.setGraph(R.navigation.user_garden_nav_graph)
            navController.setCurrentDestination(R.id.gardenPlantsFiltersDialog)
            //navController.navigate(R.id.gardenPlantsFiltersDialog)

            val dialog = GardenPlantsFiltersDialog()
            dialog.show(activity.supportFragmentManager, "filters dialog")
        }
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        scenario.onActivity { activity ->
            val dialog = activity.supportFragmentManager.findFragmentByTag("filters dialog") as GardenPlantsFiltersDialog
            Navigation.setViewNavController(dialog.requireView(), navController)
        }

        // When user select filters (simulated by the expectedFilters being returned by viewModel.filters)

        // And press the 'apply' button
        onView(withId(R.id.btn_apply)).inRoot(isDialog()).perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then dialog should get UserPlantsFiltersUiState from view model
        verify { mockViewModel.filters }

        // And navigate back to user garden screen, passing the UserPlantsFiltersUiState 
        assertThat(navController.currentDestination?.id).isEqualTo(R.id.userGardenFragment)
        val result = navController.currentBackStackEntry?.savedStateHandle?.get<UserPlantsFiltersUiState>("filters")
        assertThat(result).isEqualTo(expectedFilters)
    }
}
