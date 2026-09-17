package com.alon.plantpulse.usergarden.ui.controller

import android.content.Context
import android.os.Looper
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alon.plantpulse.plantsdetail.ui.R
import com.alon.plantpulse.usergarden.application.model.UserGardenError
import com.alon.plantpulse.usergarden.ui.HiltTestActivity
import com.alon.plantpulse.usergarden.ui.launchFragmentInHiltContainer
import com.alon.plantpulse.usergarden.ui.model.UserPlantDeleteUiState
import com.alon.plantpulse.usergarden.ui.model.UserPlantDetailUiState
import com.alon.plantpulse.usergarden.ui.viewmodel.UserPlantDetailViewModel
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

/**
 * Unit tests for [UserPlantDetailFragment].
 */
@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
class UserPlantDetailFragmentTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private val context: Context = ApplicationProvider.getApplicationContext()

    // Activity scenario to host fragment under test.
    private lateinit var scenario: ActivityScenario<HiltTestActivity>

    // Collaborators
    private val mockViewModel: UserPlantDetailViewModel = mockk(relaxed = true)
    private val uiStateLiveData = MutableLiveData<UserPlantDetailUiState>()
    private val deleteUiStateLiveData = MutableLiveData<UserPlantDeleteUiState>()

    @Before
    fun setUp() {
        // Stub view model creation with test mock
        mockkConstructor(ViewModelLazy::class)
        every { anyConstructed<ViewModelLazy<ViewModel>>().value } returns mockViewModel

        // Stub the uiState and deleteUiState LiveData
        every { mockViewModel.uiState } returns uiStateLiveData
        every { mockViewModel.deleteUiState } returns deleteUiStateLiveData

        // Launch fragment under test
        scenario = launchFragmentInHiltContainer<UserPlantDetailFragment>()
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Test
    fun showPlantDetail_WhenDataLoaded() {
        // Given a created fragment and a sample UI state
        val uiState = UserPlantDetailUiState(
            id = 1,
            commonName = "Monstera Deliciosa",
            scientificName = "Monstera deliciosa",
            imageUrl = "url",
            category = "Aroid",
            subcategory = "Climber",
            waterCare = "Weekly",
            sunCare = "Indirect",
            soilTemp = "70-80°F",
            matureHeight = "10ft",
            maturityTime = "2 years",
            bloomSeason = "Summer",
            directions = "Keep in bright indirect light. Water when the top inch of soil is dry."
        )

        // When UserPlantDetailUiState is updated from view model
        scenario.onActivity {
            uiStateLiveData.value = uiState
        }
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment should show all state fields in layout
        
        // Check top elements
        onView(withId(R.id.tv_common_name)).check(matches(withText(uiState.commonName)))
        onView(withId(R.id.tv_scientific_name)).check(matches(withText(uiState.scientificName)))
        
        // Verify Chips (scroll if necessary)
        onView(withText(uiState.category)).perform(scrollTo()).check(matches(isDisplayed()))
        onView(withText(uiState.subcategory)).perform(scrollTo()).check(matches(isDisplayed()))
        
        // Verify Care Stats
        onView(withText(uiState.waterCare)).perform(scrollTo()).check(matches(isDisplayed()))
        onView(withText(uiState.sunCare)).perform(scrollTo()).check(matches(isDisplayed()))
        onView(withText(uiState.soilTemp)).perform(scrollTo()).check(matches(isDisplayed()))
        
        // Verify Growth Details
        onView(withText(uiState.matureHeight)).perform(scrollTo()).check(matches(isDisplayed()))
        onView(withText(uiState.maturityTime)).perform(scrollTo()).check(matches(isDisplayed()))
        onView(withText(uiState.bloomSeason)).perform(scrollTo()).check(matches(isDisplayed()))

        // Verify Directions
        onView(withId(R.id.tv_directions)).perform(scrollTo()).check(matches(withText(uiState.directions)))
    }

    @Test
    fun showError_WhenPlantDetailLoadFail() {
        // Given a created fragment and a sample UI state with UserGardenError.Internal error
        val error = UserGardenError.Internal(RuntimeException("Test error"))
        val uiState = UserPlantDetailUiState(error = error)

        // When UserPlantDetailUiState is updated from view model
        scenario.onActivity {
            uiStateLiveData.value = uiState
        }
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment should show snackbar, with error_message_internal_error from strings.xml
        onView(withText(R.string.error_message_internal_error))
            .check(matches(isDisplayed()))
    }

    @Test
    fun deletePlant_WhenPlantDetailLoaded_AndUserSelectToDeleteIt() {
        // Given a created fragment and a sample UI state
        val uiState = UserPlantDetailUiState(
            id = 1,
            commonName = "Monstera Deliciosa",
            scientificName = "Monstera deliciosa",
            imageUrl = "url",
            category = "Aroid",
            subcategory = "Climber",
            waterCare = "Weekly",
            sunCare = "Indirect",
            soilTemp = "70-80°F",
            matureHeight = "10ft",
            maturityTime = "2 years",
            bloomSeason = "Summer",
            directions = "Keep in bright indirect light. Water when the top inch of soil is dry."
        )

        // When UserPlantDetailUiState is updated from view model
        scenario.onActivity {
            uiStateLiveData.value = uiState
        }
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // And user clicks on delete plant button
        onView(withId(R.id.btn_delete_plant)).perform(scrollTo(), click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment should request view model to delete plant
        verify(exactly = 1) { mockViewModel.deletePlant() }
    }

    @Test
    fun showConfirmationDialog_AndReturnToGardenScreen_WhenPlantDeleted() {
        // Given a created fragment with navigation
        val navController = TestNavHostController(context)
        scenario.onActivity { activity ->
            val fragment = activity.supportFragmentManager.fragments.first()!!
            navController.setGraph(R.navigation.user_garden_nav_graph)
            // Navigate to detail destination
            navController.setCurrentDestination(R.id.userPlantDetailFragment)
            Navigation.setViewNavController(fragment.requireView(), navController)
        }
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // When view model update UserPlantDeleteUiState.Success for plant deletion
        scenario.onActivity {
            deleteUiStateLiveData.value = UserPlantDeleteUiState.Success
        }
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment should show a alert dialog, with message that plant was deleted
        onView(withText(R.string.message_plant_deleted))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))

        // When user clicks ok in the dialog window(or close it)
        onView(withText(R.string.label_ok)).perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment should navigate back to garden screen
        assertThat(navController.currentDestination?.id).isEqualTo(R.id.userGardenFragment)
    }

    @Test
    fun showError_WhenPlantDeleteFail() {
        // Given a created fragment
        val error = UserGardenError.Internal(RuntimeException("Delete failed"))

        // When view model update UserPlantDeleteUiState.Error for plant deletion fail
        scenario.onActivity {
            deleteUiStateLiveData.value = UserPlantDeleteUiState.Error(error)
        }
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        
        // Then fragment should show snackbar, with error_message_internal_error from strings.xml
        onView(withText(R.string.error_message_internal_error))
            .check(matches(isDisplayed()))
    }

    @Test
    fun navBackToGardeScreen_WhenUserPressBackButton() {
        // Given a created fragment with navigation
        val navController = TestNavHostController(context)
        scenario.onActivity { activity ->
            val fragment = activity.supportFragmentManager.fragments.first()!!
            navController.setLifecycleOwner(fragment.viewLifecycleOwner)
            navController.setOnBackPressedDispatcher(activity.onBackPressedDispatcher)
            navController.setGraph(R.navigation.user_garden_nav_graph)
            navController.setCurrentDestination(R.id.userGardenFragment)
            navController.navigate(R.id.userPlantDetailFragment)
            Navigation.setViewNavController(fragment.requireView(), navController)
        }
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // When user press back button
        Espresso.pressBack()
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment should navigate back to garden screen
        assertThat(navController.currentDestination?.id).isEqualTo(R.id.userGardenFragment)
    }

    @Test
    fun disableDeleteButton_WhilePlantDetailIsLoading() {
        // Given created fragment and plant detail ui state

        // Then delete button should be disabled by default
        onView(withId(R.id.btn_delete_plant)).check(matches(not(isEnabled())))

        // When UserPlantDetailUiState is updated from view model
        scenario.onActivity {
            uiStateLiveData.value = UserPlantDetailUiState(id = 1, commonName = "Aloe")
        }
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then delete button should be enabled
        onView(withId(R.id.btn_delete_plant)).check(matches(isEnabled()))
    }

    @Test
    fun disableDeleteButton_WhilePlantIsDeleted() {
        // Given a created fragment and UserPlantDeleteUiState states

        // When view model update UserPlantDeleteUiState.Loading for plant deletion
        scenario.onActivity {
            deleteUiStateLiveData.value = UserPlantDeleteUiState.Loading
        }
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then delete button should be disabled
        onView(withId(R.id.btn_delete_plant)).check(matches(not(isEnabled())))

        // When view model update UserPlantDeleteUiState.Success for plant deletion
        scenario.onActivity {
            deleteUiStateLiveData.value = UserPlantDeleteUiState.Success
        }
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then delete button should be enabled
        onView(withId(R.id.btn_delete_plant)).check(matches(isEnabled()))
    }
}
