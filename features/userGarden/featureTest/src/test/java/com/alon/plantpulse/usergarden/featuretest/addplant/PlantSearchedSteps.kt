package com.alon.plantpulse.usergarden.featuretest.addplant

import android.os.Looper
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.pressImeActionButton
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.BoundedMatcher
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.alon.plantpulse.plantsdetail.ui.R
import com.alon.plantpulse.usergarden.data.local.Plant
import com.alon.plantpulse.usergarden.data.local.PlantDao
import com.alon.plantpulse.usergarden.featuretest.util.atPosition
import com.alon.plantpulse.usergarden.featuretest.util.createPlant
import com.alon.plantpulse.usergarden.featuretest.util.hasItemCount
import com.alon.plantpulse.usergarden.ui.HiltTestActivity
import com.alon.plantpulse.usergarden.ui.controller.PlantsSearchAdapter
import com.alon.plantpulse.usergarden.ui.controller.PlantsSearchFragment
import com.alon.plantpulse.usergarden.ui.launchFragmentInHiltContainer
import com.alon.plantpulse.usergarden.ui.model.PlantUiState
import com.google.android.material.search.SearchView
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import kotlinx.coroutines.test.runTest
import org.hamcrest.Description
import org.hamcrest.Matcher
import org.robolectric.Shadows
import kotlin.io.appendText

class PlantSearchedSteps(private val plantDao: PlantDao) : GreenCoffeeSteps() {

    private lateinit var scenario: ActivityScenario<HiltTestActivity>
    private val plants = listOf(
        createPlant(1, "Monstera Deliciosa", "Monstera deliciosa", "image_url_1"),
        createPlant(2, "Snake Plant", "Sansevieria trifasciata", "image_url_2"),
        createPlant(3, "Swiss Cheese Plant", "Monstera adansonii", "image_url_3")
    )
    private val query = "monstera"
    private val expectedResults: List<PlantUiState> = listOf(
        PlantUiState(1, "Monstera Deliciosa", "Monstera deliciosa", "image_url_1"),
        PlantUiState(3, "Swiss Cheese Plant", "Monstera adansonii", "image_url_3")
    )

    @Given("^user want to add Monstera plant to garden$")
    fun userWantToAddAPlant() = runTest {
        plantDao.insertAll(plants)
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @When("^he open plants search screen$")
    fun openPlantsSearchScreen() {
        scenario = launchFragmentInHiltContainer<PlantsSearchFragment>()
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @When("^perform search for this plant$")
    fun performSearchForThisPlant() {
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
        Thread.sleep(2000)
    }

    @Then("^app should list all matching results$")
    fun appShouldListAllMatchingResults() {
        expectedResults.forEachIndexed { index, item ->
            onView(withId(R.id.plants_recycler_view))
                .perform(RecyclerViewActions.scrollToPosition<PlantsSearchAdapter.PlantViewHolder>(index))
            Shadows.shadowOf(Looper.getMainLooper()).idle()

            onView(atPosition(R.id.plants_recycler_view, index))
                .check(matches(hasDescendant(withText(item.commonName))))
                .check(matches(hasDescendant(withText(item.scientificName))))
            Shadows.shadowOf(Looper.getMainLooper()).idle()
        }
    }

    @When("^user perform search for unknown plant$")
    fun userPerformSearchForUnknownPlant() {
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
            .perform(typeText("abracadabra unknown plant"), pressImeActionButton())

        Shadows.shadowOf(Looper.getMainLooper()).idle()
        Thread.sleep(2000)
    }
    @Then("^app should list no results, and show \"No results found\" message$")
    fun appShouldListNoResultsAndShowNoResultsFoundMessage() {
        onView(withId(R.id.plants_recycler_view))
            .check(matches(hasItemCount(0)))
        onView(withId(R.id.no_results_text))
            .check(matches(isDisplayed()))
    }
}
