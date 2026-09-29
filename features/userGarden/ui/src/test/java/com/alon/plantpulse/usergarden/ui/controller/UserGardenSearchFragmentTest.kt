package com.alon.plantpulse.usergarden.ui.controller

import android.content.Context
import android.os.Looper
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.pressImeActionButton
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alon.plantpulse.plantsdetail.ui.R
import com.alon.plantpulse.usergarden.application.model.UserGardenError
import com.alon.plantpulse.usergarden.ui.HiltTestActivity
import com.alon.plantpulse.usergarden.ui.launchFragmentInHiltContainer
import com.alon.plantpulse.usergarden.ui.model.UserPlantUiState
import com.alon.plantpulse.usergarden.ui.util.hasItemCount
import com.alon.plantpulse.usergarden.ui.viewmodel.UserGardenSearchViewModel
import com.google.android.material.search.SearchView
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
class UserGardenSearchFragmentTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // Activity scenario to host fragment under test.
    private lateinit var scenario: ActivityScenario<HiltTestActivity>

    // Collaborators
    private val mockViewModel: UserGardenSearchViewModel = mockk(relaxed = true)
    private val searchResultsLiveData = MutableLiveData<PagingData<UserPlantUiState>>()

    @Before
    fun setUp() {
        // Stub view model creation with test mock
        mockkConstructor(ViewModelLazy::class)
        every { anyConstructed<ViewModelLazy<ViewModel>>().value } returns mockViewModel
        
        // Stub search results
        every { mockViewModel.searchResults } returns searchResultsLiveData

        // Launch fragment under test
        scenario = launchFragmentInHiltContainer<UserGardenSearchFragment>()
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Test
    fun performSearchForUserPlants_WhenUserSubmitQuery() {
        // Given a created fragment and a query
        val query = "Aloe"

        // When user open search view
        onView(withId(R.id.search_bar)).perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        scenario.onActivity { activity ->
            val searchView = activity.findViewById<SearchView>(R.id.search_view)

            // Manually trigger expansion to skip animation issues
            searchView.show()

            // Force a layout pass so the width/height are not 0
            searchView.measure(320, 470)
            searchView.layout(0, 0, 320, 470)
        }
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // And types query in search bar
        // And submits query
        onView(withId(com.google.android.material.R.id.open_search_view_edit_text))
            .perform(typeText(query), pressImeActionButton())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment should invoke view model to perform search with typed query
        verify { mockViewModel.searchUserPlants(query) }
    }

    @Test
    fun showLoadingProgress_WhileSearchIsExecuting() {
        // Given a created fragment
        val loadingStates = LoadStates(
            refresh = LoadState.Loading,
            prepend = LoadState.NotLoading(false),
            append = LoadState.Loading
        )
        val pagingData = PagingData.from(
            data = emptyList<UserPlantUiState>(),
            sourceLoadStates = loadingStates
        )

        // When view model updates search results paging is loading
        scenario.onActivity {
            searchResultsLiveData.value = pagingData
        }
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment should show loading progress
        onView(withId(R.id.loading_indicator)).check(matches(isDisplayed()))
    }

    @Test
    fun hideLoadingProgress_WhenSearchIsDone() {
        // Given a created fragment
        val loadingStates = LoadStates(
            refresh = LoadState.NotLoading(false),
            prepend = LoadState.NotLoading(false),
            append = LoadState.NotLoading(false)
        )
        val pagingData = PagingData.from(
            data = emptyList<UserPlantUiState>(),
            sourceLoadStates = loadingStates
        )

        // When view model updates search results paging is done loading
        scenario.onActivity {
            searchResultsLiveData.value = pagingData
        }
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment should hide loading progress
        onView(withId(R.id.loading_indicator)).check(matches(not(isDisplayed())))
    }

    @Test
    fun showSearchResults_WhenSearchCompletedWithResults() {
        // Given a created fragment and a paging of search results 
        val plants = listOf(
            UserPlantUiState(1, "Aloe Vera", "Aloe barbadensis", "url1", "Succulent"),
            UserPlantUiState(2, "Snake Plant", "Sansevieria", "url2", "Succulent")
        )
        val pagingData = PagingData.from(plants)

        // When view model updates search results paging
        scenario.onActivity {
            searchResultsLiveData.value = pagingData
        }
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment should show search results
        onView(withId(R.id.plants_recycler_view))
            .check(matches(hasItemCount(plants.size)))

        Thread.sleep(1000)

        onView(withId(R.id.plants_recycler_view))
            .check(matches(hasDescendant(withText("Aloe Vera"))))
        onView(withId(R.id.plants_recycler_view))
            .check(matches(hasDescendant(withText("Snake Plant"))))
    }

    @Test
    fun showEmptySearchMessage_WhenSearchCompletedWithoutResults() {
        // Given a created fragment and a paging of empty search results
        val emptyResultsPagingData = PagingData.from(emptyList<UserPlantUiState>(),
            LoadStates(LoadState.NotLoading(true),
                LoadState.NotLoading(false),
                LoadState.NotLoading(false)))

        // When view model updates search results
        searchResultsLiveData.value = emptyResultsPagingData
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment should show empty search message
        onView(withId(R.id.no_results_text))
            .check(matches(isDisplayed()))
    }

    @Test
    fun showErrorMessage_WhenSearchFailDueToInvalidQueryError() {
        // Given a created fragment and empty paging with UserGardenError.EmptySearchQuery state
        val error = UserGardenError.EmptySearchQuery()
        val errorPagingData = PagingData.from(
            emptyList<UserPlantUiState>(),
            LoadStates(
                refresh = LoadState.Error(error),
                prepend = LoadState.NotLoading(false),
                append = LoadState.NotLoading(false)
            )
        )

        // When view model updates search results paging
        scenario.onActivity {
            searchResultsLiveData.value = errorPagingData
        }
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment should show snakebar with R.string.error_message_empty_search_query message
        onView(withText(R.string.error_message_empty_search_query))
            .check(matches(isDisplayed()))
    }

    @Test
    fun showErrorMessage_WhenSearchFailDueToInternalFeatureError() {
        // Given a created fragment and empty paging with UserGardenError.Internal state
        val error = UserGardenError.Internal(mockk())
        val errorPagingData = PagingData.from(
            emptyList<UserPlantUiState>(),
            LoadStates(
                refresh = LoadState.Error(error),
                prepend = LoadState.NotLoading(false),
                append = LoadState.NotLoading(false)
            )
        )

        // When view model updates search results paging
        scenario.onActivity {
            searchResultsLiveData.value = errorPagingData
        }
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment should show snakebar with R.string.error_message_internal_error message
        onView(withText(R.string.error_message_internal_error))
            .check(matches(isDisplayed()))
    }

    @Test
    fun openUserPlantDetailScreen_WhenUserClicksOnPlantFromSearchResults() {
        // Given a created fragment and a paging of search results
        val context = ApplicationProvider.getApplicationContext<Context>()
        val navController = TestNavHostController(context)
        val plants = listOf(
            UserPlantUiState(101, "Monstera", "Monstera deliciosa", "url", "Aroid")
        )

        scenario.onActivity { activity ->
            val fragment = activity.supportFragmentManager.fragments.first()!!
            navController.setGraph(R.navigation.user_garden_nav_graph)
            navController.setCurrentDestination(R.id.gardenPlantsSearchFragment)
            Navigation.setViewNavController(fragment.requireView(), navController)
        }
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // When view model updates search results paging
        scenario.onActivity {
            searchResultsLiveData.value = PagingData.from(plants)
        }
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // And user clicks on first shown plant
        onView(withId(R.id.plants_recycler_view))
            .perform(RecyclerViewActions.actionOnItemAtPosition<UserPlantsAdapter.PlantViewHolder>(0, click()))
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment should navigate to plant detail screen
        assertThat(navController.currentDestination?.id).isEqualTo(R.id.userPlantDetailFragment)
        assertThat(navController.backStack.last().arguments?.getInt("plantId")).isEqualTo(101)
    }
}
