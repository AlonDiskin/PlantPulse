package com.alon.plantpulse.usergarden.featuretest.addplant

import android.os.Looper
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.pressImeActionButton
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isEnabled
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.alon.plantpulse.plantsdetail.ui.R
import com.alon.plantpulse.usergarden.data.local.Plant
import com.alon.plantpulse.usergarden.data.local.PlantDao
import com.alon.plantpulse.usergarden.data.local.UserPlant
import com.alon.plantpulse.usergarden.data.local.UserPlantDao
import com.alon.plantpulse.usergarden.featuretest.util.createPlant
import com.alon.plantpulse.usergarden.ui.HiltTestActivity
import com.alon.plantpulse.usergarden.ui.controller.PlantsSearchFragment
import com.alon.plantpulse.usergarden.ui.launchFragmentInHiltContainer
import com.google.android.material.search.SearchView
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import kotlinx.coroutines.test.runTest
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not
import org.robolectric.Shadows

class AddingExistingPlantSteps(private val plantDao: PlantDao,
                               private val userPlantDao: UserPlantDao) : GreenCoffeeSteps() {

    private lateinit var scenario: ActivityScenario<HiltTestActivity>
    private lateinit var plant: Plant
    private lateinit var query: String

    @Given("^user has Monstera Deliciosa plant in garden collection$")
    fun userHasMonsteraDeliciosaPlantInGardenCollection() = runTest {
        plant = createPlant(10, "Monstera", "Monstera Deliciosa", "image_url_2")
        query = plant.commonName

        plantDao.insertAll(listOf(plant))
        userPlantDao.add(UserPlant(plant.id))
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @When("^he open garden screen$")
    fun heOpenGardenScreen() {
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

    @When("^select to add this plant$")
    fun selectToAddThisPlant() {
        onView(withId(R.id.add_plant_button))
            .perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Then("^app should deny his selection$")
    fun appShouldDenyHisSelection() {
        onView(withId(R.id.add_plant_button))
            .check(matches(
                allOf(
                    withId(R.id.add_plant_button),
                    not(isEnabled()),
                    withText(R.string.label_added)
                )
            ))
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }
}
