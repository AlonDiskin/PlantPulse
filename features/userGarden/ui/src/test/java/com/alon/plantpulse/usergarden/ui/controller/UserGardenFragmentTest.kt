package com.alon.plantpulse.usergarden.ui.controller

import android.content.Context
import android.os.Looper
import android.view.View
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelLazy
import androidx.lifecycle.ViewModelStore
import androidx.navigation.testing.TestNavHostController
import androidx.paging.LoadState
import androidx.paging.LoadStates
import androidx.paging.PagingData
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.Visibility
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alon.plantpulse.plantsdetail.ui.R
import com.alon.plantpulse.usergarden.application.model.UserGardenError
import com.alon.plantpulse.usergarden.domain.PlantCategory
import com.alon.plantpulse.usergarden.ui.HiltTestActivity
import com.alon.plantpulse.usergarden.ui.launchFragmentInHiltContainer
import com.alon.plantpulse.usergarden.ui.model.UserPlantUiState
import com.alon.plantpulse.usergarden.ui.model.UserPlantsFiltersUiState
import com.alon.plantpulse.usergarden.ui.util.atPosition
import com.alon.plantpulse.usergarden.ui.util.hasItemCount
import com.alon.plantpulse.usergarden.ui.viewmodel.UserGardenViewModel
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkConstructor
import io.mockk.mockkStatic
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
class UserGardenFragmentTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    // Activity scenario to host fragment under test.
    private lateinit var scenario: ActivityScenario<HiltTestActivity>

    // Collaborators
    private val mockViewModel: UserGardenViewModel = mockk(relaxed = true)
    private val userPlantsLiveData = MutableLiveData<PagingData<UserPlantUiState>>()
    private val filtersLiveData = MutableLiveData<UserPlantsFiltersUiState>()
    private val navController = TestNavHostController(ApplicationProvider.getApplicationContext()).apply {
        setViewModelStore(ViewModelStore())
        setGraph(R.navigation.user_garden_nav_graph)
        setCurrentDestination(R.id.userGardenFragment)
    }

    @Before
    fun setUp() {
        // Stub view model creation with test mock
        mockkConstructor(ViewModelLazy::class)
        every { anyConstructed<ViewModelLazy<ViewModel>>().value } returns mockViewModel

        // Stub search results
        every { mockViewModel.userPlants } returns userPlantsLiveData
        every { mockViewModel.filters } returns filtersLiveData

        // Launch fragment under test
        scenario = launchFragmentInHiltContainer<UserGardenFragment>(navController = navController)
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Test
    fun displayUserGardenPlants_WhenFragmentShown() {
        // Given user has a collection of garden plants
        val plants = listOf(
            UserPlantUiState(1,
                "Monstera Deliciosa",
                "Monstera deliciosa",
                "image_url_1",
                "aroids"),
            UserPlantUiState(2,
                "Snake Plant",
                "Sansevieria trifasciata",
                "image_url_2",
                "succulent"),
            UserPlantUiState(3,
                "Swiss Cheese Plant",
                "Monstera adansonii",
                "image_url_3",
                "aroids")
        )
        val pagingData = PagingData.from(plants)
        userPlantsLiveData.value = pagingData
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // When fragment is shown

        // Then fragment should display the garden plants
        onView(withId(R.id.user_plants_recycler_view))
            .check(matches(hasItemCount(plants.size)))

        Thread.sleep(2000)

        plants.forEachIndexed { index, item ->
            onView(withId(R.id.user_plants_recycler_view))
                .perform(RecyclerViewActions.scrollToPosition<UserPlantsAdapter.PlantViewHolder>(index))
            Shadows.shadowOf(Looper.getMainLooper()).idle()

            onView(atPosition(R.id.user_plants_recycler_view, index))
                .check(matches(hasDescendant(withText(item.commonName))))
                .check(matches(hasDescendant(withText(item.scientificName))))
                .check(matches(hasDescendant(withText(item.category))))
            Shadows.shadowOf(Looper.getMainLooper()).idle()
        }
    }

    @Test
    fun openSearchScreen_WhenUserSelectToAddPlant() {
        // Given a shown fragment

        // When user click on add plant button
        onView(withId(R.id.add_plant_fab))
            .perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment should navigate to search screen
        assertThat(navController.currentDestination?.id).isEqualTo(R.id.plantsSearchFragment)
    }

    @Test
    fun showProgressBar_WhenUserPlantsLoaded() {
        // Given
        val loadingStates = LoadStates(
            refresh = LoadState.Loading,
            prepend = LoadState.NotLoading(false),
            append = LoadState.Loading
        )

        val pagingData = PagingData.from(
            data = emptyList<UserPlantUiState>(),
            sourceLoadStates = loadingStates
        )

        // When paged user plants are loading
        userPlantsLiveData.value = pagingData
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment should show progress bar
        onView(withId(R.id.loading_indicator))
            .check(matches(isDisplayed()))
    }

    @Test
    fun hideProgressBar_WhenUserPlantsLoaded() {
        // Given
        val loadingStates = LoadStates(
            refresh = LoadState.NotLoading(false),
            prepend = LoadState.NotLoading(false),
            append = LoadState.NotLoading(false)
        )

        val pagingData = PagingData.from(
            data = emptyList<UserPlantUiState>(),
            sourceLoadStates = loadingStates
        )

        // When paged user plants are loaded
        userPlantsLiveData.value = pagingData
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment should hide progress bar
        onView(withId(R.id.loading_indicator))
            .check(matches(not(isDisplayed())))
    }

    @Test
    fun showUiNotification_WhenUserPlantsCollectionLoadingFail() {
            // Given
            val context = ApplicationProvider.getApplicationContext<Context>()
            val expectedErrorMessage = context.getString(R.string.error_message_internal_error)
            val errorPagingData = PagingData.from(emptyList<UserPlantUiState>(),
                LoadStates(LoadState.Error(UserGardenError.Internal(mockk())),
                    LoadState.NotLoading(false),
                    LoadState.NotLoading(false)))

            // When paged user plants loading fail
            userPlantsLiveData.value = errorPagingData
            Shadows.shadowOf(Looper.getMainLooper()).idle()

            // Then fragment should show error notification
            onView(withText(expectedErrorMessage))
                .check(matches(isDisplayed()))
    }

    @Test
    fun showEmptyGardenIndication_WhenUserPlantsCollectionIsEmpty() {
        // Given user has empty collection of garden plants
        mockkStatic("com.alon.plantpulse.usergarden.ui.controller.AnimationUtilKt")
        every { any<View>().startAtmosphericPulse() } returns Unit
        val emptyPagingData = PagingData.from(emptyList<UserPlantUiState>(),
            LoadStates(LoadState.NotLoading(true),
                LoadState.NotLoading(false),
                LoadState.NotLoading(false)))

        // When view model updates the plants paging data
        userPlantsLiveData.value = emptyPagingData
        //Shadows.shadowOf(Looper.getMainLooper()).idleFor(java.time.Duration.ofMillis(100))
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then
        // Verify the empty state container is visible
        onView(withId(R.id.empty_garden_layout))
            .check(matches(isDisplayed()))

        // Verify the illustration is visible
        onView(withId(R.id.empty_pot_icon))
            .check(matches(isDisplayed()))

        // Verify animation was started on the illustration
        verify { any<View>().startAtmosphericPulse() }
    }

    @Test
    fun doNotShowEmptyGardenIndication_WhenUserPlantsCollectionIsNotEmpty() {
        // Given user has a collection of garden plants
        val plants = listOf(
            UserPlantUiState(1,
                "Monstera Deliciosa",
                "Monstera deliciosa",
                "image_url_1",
                "aroids"),
            UserPlantUiState(2,
                "Snake Plant",
                "Sansevieria trifasciata",
                "image_url_2",
                "succulent"),
            UserPlantUiState(3,
                "Swiss Cheese Plant",
                "Monstera adansonii",
                "image_url_3",
                "aroids")
        )
        val pagingData = PagingData.from(plants)

        // When view model updates the plants paging data
        userPlantsLiveData.value = pagingData
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        Thread.sleep(2000)

        // Then fragment should not show empty garden animation
        // Verify the empty state container is not visible
        onView(withId(R.id.empty_garden_layout))
            .check(matches(not(isDisplayed())))

        // Verify the illustration is not visible
        onView(withId(R.id.empty_pot_icon))
            .check(matches(not(isDisplayed())))
    }

    @Test
    fun showPlantDetail_WhenUserSelectListedPlant() {
        // Given a created fragment and a collection of garden plants
        val plants = listOf(
            UserPlantUiState(42, "Aloe", "Aloe Vera", "url", "Succulent")
        )

        // When plants are updated from view model
        userPlantsLiveData.value = PagingData.from(plants)
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // And user click on first shown plant
        onView(withId(R.id.user_plants_recycler_view))
            .perform(RecyclerViewActions.actionOnItemAtPosition<UserPlantsAdapter.PlantViewHolder>(0, click()))
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment should navigate to detail screen with plant id
        assertThat(navController.currentDestination?.id).isEqualTo(R.id.userPlantDetailFragment)
        assertThat(navController.backStack.last().arguments?.getInt("plantId")).isEqualTo(42)
    }

    @Test
    fun openGardenPlantsSearchScreen_WhenUserClickOnAppBarSearchMenuButton() {
        // Given a created fragment

        // When user click on search menu button, from fragment's managed app bar menu
        onView(withId(R.id.menu_search)).perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment should navigate to user garden search screen
        assertThat(navController.currentDestination?.id).isEqualTo(R.id.gardenPlantsSearchFragment)
    }

    @Test
    fun showCurrentFilters_WhenFiltersSelected() {
        // Give filters state and shown fragment
        val selectedFilters = UserPlantsFiltersUiState(
            categoryFilters = setOf(PlantCategory.VEGETABLE)
        )
        val expectedFilterName = PlantCategory.VEGETABLE.name.lowercase()

        // When view model update UserPlantsFiltersUiState
        scenario.onActivity {
            filtersLiveData.value = selectedFilters
        }
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment should show filters ui by dynamically add them to view as chips
        onView(withId(R.id.cg_filters))
            .check(matches(isDisplayed()))
        onView(withText(expectedFilterName))
            .check(matches(isDisplayed()))
    }

    @Test
    fun hideFilters_WhenNoFiltersSelected() {
        // Given empty filters state an shown fragment
        val filters = UserPlantsFiltersUiState()

        // When view model update filters state
        scenario.onActivity {
            filtersLiveData.value = filters
        }
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment should not show filters ui
        onView(withId(R.id.cg_filters))
            .check(matches(withEffectiveVisibility(Visibility.GONE)))
    }

    @Test
    fun openFiltersDialog_WhenUserClickOnAppBarFilterMenuButton() {
        // Given a shown fragment with a current filter state
        val currentFilters = UserPlantsFiltersUiState(categoryFilters = setOf(PlantCategory.HERB))

        scenario.onActivity {
            // Simulate the ViewModel holding some current filters
            filtersLiveData.value = currentFilters
        }
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // When user click on the 'filter-list' menu button, from fragment's managed app bar menu
        onView(withId(R.id.menu_filter)).perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment should open filters dialog
        assertThat(navController.currentDestination?.id).isEqualTo(R.id.gardenPlantsFiltersDialog)

        // And pass it the current filters state from view model
        val passedFilters = navController.backStack.last().arguments?.getSerializable("filters")
        assertThat(passedFilters).isEqualTo(currentFilters)
    }

    @Test
    fun setFilters_WhenReceiveSelectFiltersFromFiltersDialog() {
        // Given a shown fragment and filters state
        val selectedFilters = UserPlantsFiltersUiState(categoryFilters = setOf(PlantCategory.FRUIT))

        // When fragment get result from dialog with selected filters, via navigation component
        scenario.onActivity {
            navController.currentBackStackEntry?.savedStateHandle?.set("filters", selectedFilters)
        }
        Shadows.shadowOf(Looper.getMainLooper()).idle()

        // Then fragment should update view model filters state with selected filters
        verify(exactly = 1) { mockViewModel.setFilters(selectedFilters) }
    }
}
