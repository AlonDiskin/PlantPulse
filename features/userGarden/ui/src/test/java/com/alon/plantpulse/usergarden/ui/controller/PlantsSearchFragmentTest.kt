package com.alon.plantpulse.usergarden.ui.controller

import android.content.Context
import android.os.Looper
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
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
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alon.plantpulse.plantsdetail.ui.R
import com.alon.plantpulse.usergarden.application.model.UserGardenError
import com.alon.plantpulse.usergarden.ui.HiltTestActivity
import com.alon.plantpulse.usergarden.ui.launchFragmentInHiltContainer
import com.alon.plantpulse.usergarden.ui.model.AddPlantUiState
import com.alon.plantpulse.usergarden.ui.model.PlantUiState
import com.alon.plantpulse.usergarden.ui.viewmodel.PlantsSearchViewModel
import com.google.android.material.search.SearchView
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows
import org.robolectric.annotation.LooperMode

@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
class PlantsSearchFragmentTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()
    private val context: Context = ApplicationProvider.getApplicationContext()

    // Activity scenario to host fragment under test.
    private lateinit var scenario: ActivityScenario<HiltTestActivity>

    // Collaborators
    private val mockViewModel: PlantsSearchViewModel = mockk(relaxed = true)
    private val searchResultsLiveData = MutableLiveData<PagingData<PlantUiState>>()
    private val addPlantUiStateLiveData = MutableLiveData<AddPlantUiState>()

    @Before
    fun setUp() {
        // Stub view model creation with test mock
        mockkConstructor(ViewModelLazy::class)
        every { anyConstructed<ViewModelLazy<ViewModel>>().value } returns mockViewModel
        
        // Stub search results
        every { mockViewModel.searchResults } returns searchResultsLiveData
        every { mockViewModel.addPlantUiState } returns addPlantUiStateLiveData

        // Launch fragment under test
        scenario = launchFragmentInHiltContainer<PlantsSearchFragment>()
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Test
    fun whenPerformingSearch_WithUserQuery_ShouldExecutePlantsSearch() {
        // Given a query
        val query = "rose"

        // When user submit search with query
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

        onView(withId(com.google.android.material.R.id.open_search_view_edit_text))
            .check(matches(isDisplayed()))
            .perform(typeText(query), pressImeActionButton())

        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment should request view model to perform search with query
        verify { mockViewModel.searchPlants(query) }
    }

    @Test
    fun whenSearchResultsAreAvailable_ShouldPresentResultsAsPagedList() {
        // Given a paged data of plants search results
        val plants = listOf(
            PlantUiState(1, "Rose", "Rosa", "url1"),
            PlantUiState(2, "Tulip", "Tulipa", "url2")
        )
        val pagingData = PagingData.from(plants)

        // When results are available from view model
        scenario.onActivity {
            searchResultsLiveData.value = pagingData
        }
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment should update UI with results
        onView(withId(R.id.plants_recycler_view))
            .check(matches(hasDescendant(withText("Rose"))))
        onView(withId(R.id.plants_recycler_view))
            .check(matches(hasDescendant(withText("Tulip"))))
    }

    @Test
    fun showErrorNotification_WhenSearchFails_WithEmptyQueryError() {
        // Given
        val expectedErrorMessage = context.getString(R.string.error_message_empty_search_query)
        val errorPagingData = PagingData.from(emptyList<PlantUiState>(),
            LoadStates(LoadState.Error(UserGardenError.EmptySearchQuery()),
                LoadState.NotLoading(false),
                LoadState.NotLoading(false)))

        // When
        scenario.onActivity {
            searchResultsLiveData.value = errorPagingData
        }
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then
        onView(withText(expectedErrorMessage))
            .check(matches(isDisplayed()))
    }

    @Test
    fun showErrorNotification_WhenSearchFails_WithInternalError() {
        // Given
        val expectedErrorMessage = context.getString(R.string.error_message_internal_error)
        val errorPagingData = PagingData.from(emptyList<PlantUiState>(),
            LoadStates(LoadState.Error(UserGardenError.Internal(mockk())),
                LoadState.NotLoading(false),
                LoadState.NotLoading(false)))

        // When
        scenario.onActivity {
            searchResultsLiveData.value = errorPagingData
        }
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then
        onView(withText(expectedErrorMessage))
            .check(matches(isDisplayed()))
    }

    @Test
    fun showUiMessage_WhenSearchResultsAreEmpty() {
        // Given an empty paged data of plants search results, with load state of 'not loading'
        val emptyResultsPagingData = PagingData.from(emptyList<PlantUiState>(),
            LoadStates(LoadState.NotLoading(true),
                LoadState.NotLoading(false),
                LoadState.NotLoading(false)))

        // When results are updated from view model
        searchResultsLiveData.value = emptyResultsPagingData
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment should show 'No results found' message in middle of screen
        onView(withId(R.id.no_results_text))
            .check(matches(isDisplayed()))
    }

    @Test
    fun addPlantToGarden_WhenUserSelectToAddPlantFromSearchResults() {
        // Given fragment has a list of plants search results
        val plants = listOf(
            PlantUiState(1, "Rose", "Rosa", "url1")
        )
        val pagingData = PagingData.from(plants)

        searchResultsLiveData.value = pagingData
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        every { mockViewModel.addPlantToGarden(any()) } returns Unit

        // When user clicks on add plant button for a plant
        onView(withId(R.id.add_plant_button))
            .perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment should request view model to add plant to garden
        verify(exactly = 1) { mockViewModel.addPlantToGarden(plants[0]) }
    }

    @Test
    fun notifyUi_WhenPlantWasAddedToGardenSuccessfully() {
        // Given
        val uiState = AddPlantUiState.Success
        val expectedMessage = context.getString(R.string.message_plant_added)

        // When plant is added to garden
        addPlantUiStateLiveData.value = uiState
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment should show success message
        onView(withText(expectedMessage))
            .check(matches(isDisplayed()))
    }

    @Test
    fun notifyUi_WhenPlantAddingToGardenFails() {
        // Given
        val uiState = AddPlantUiState.Error(UserGardenError.Internal(mockk()))
        val expectedErrorMessage = context.getString(R.string.error_message_internal_error)

        // When plant is added to garden
        addPlantUiStateLiveData.value = uiState
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment should show success message
        onView(withText(expectedErrorMessage))
            .check(matches(isDisplayed()))
    }
}
