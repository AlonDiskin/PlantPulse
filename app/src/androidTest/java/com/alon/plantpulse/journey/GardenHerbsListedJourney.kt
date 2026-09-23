package com.alon.plantpulse.journey

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import com.alon.plantpulse.plantsdetail.ui.R
import com.alon.plantpulse.usergarden.data.local.Plant
import com.alon.plantpulse.usergarden.data.local.PlantDao
import com.alon.plantpulse.usergarden.data.local.UserPlant
import com.alon.plantpulse.usergarden.data.local.UserPlantDao
import com.alon.plantpulse.usergarden.domain.BloomingSeason
import com.alon.plantpulse.usergarden.domain.PlantCategory
import com.alon.plantpulse.usergarden.domain.PlantSunCare
import com.alon.plantpulse.usergarden.domain.PlantWaterCare
import com.alon.plantpulse.usergarden.ui.controller.UserPlantsAdapter
import com.alon.plantpulse.util.DeviceUtil
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import kotlinx.coroutines.test.runTest
import org.hamcrest.Matchers.allOf
import org.hamcrest.Matchers.not

class GardenHerbsListedJourney(
    private val plantDao: PlantDao,
    private val userPlantDao: UserPlantDao
) : GreenCoffeeSteps() {

    private val herbPlant = createPlant(10, "Basil", "Ocimum basilicum", PlantCategory.HERB)
    private val vegetablePlant = createPlant(11, "Tomato", "Solanum lycopersicum", PlantCategory.VEGETABLE)

    @Given("^user open app from device home$")
    fun userOpenAppFromDeviceHome() {
        DeviceUtil.launchApp()
    }

    @Given("^he has plants in his garden$")
    fun heHasPlantsInHisGarden() = runTest {
        plantDao.insertAll(listOf(herbPlant, vegetablePlant))
        userPlantDao.add(UserPlant(herbPlant.id))
        userPlantDao.add(UserPlant(vegetablePlant.id))
    }

    @When("^he open garden screen$")
    fun heOpenGardenScreen() {
        // App typically starts at Home, assuming there's a way to navigate to Garden if not default.
        // If Garden is a bottom nav item, we'd click it. 
        // Based on user_garden_nav_graph.xml, userGardenFragment is start destination of that graph.
        // Assuming the app is configured to show this or we navigate to it.
        // For E2E, we use the UI as a user would.
    }

    @Then("^all his garden plants should be listed$")
    fun allHisGardenPlantsShouldBeListed() {
        Thread.sleep(2000)
        onView(withId(R.id.user_plants_recycler_view))
            .check(matches(hasDescendant(withText(herbPlant.commonName))))
        onView(withId(R.id.user_plants_recycler_view))
            .check(matches(hasDescendant(withText(vegetablePlant.commonName))))
    }

    @When("^he select to filter only plants from the herbs category$")
    fun heSelectToFilterOnlyPlantsFromTheHerbsCategory() {
        // 1. Open filter dialog
        onView(withId(R.id.menu_filter)).perform(click())
        
        // 2. Select Herb chip
        onView(withId(R.id.filter_herb)).perform(scrollTo(), click())
        
        // 3. Apply filters
        onView(withId(R.id.btn_apply)).perform(click())
    }

    @Then("^app should list only herbs frob garden plants$")
    fun appShouldListOnlyHerbsFrobGardenPlants() {
        Thread.sleep(2000)
        // Verify herb is present
        onView(withId(R.id.user_plants_recycler_view))
            .check(matches(hasDescendant(withText(herbPlant.commonName))))
        
        // Verify vegetable is NOT present
        onView(withId(R.id.user_plants_recycler_view))
            .check(matches(not(hasDescendant(withText(vegetablePlant.commonName)))))
    }

    @When("^user select to view first listed herb$")
    fun userSelectToViewFirstListedHerb() {
        onView(withId(R.id.user_plants_recycler_view))
            .perform(RecyclerViewActions.actionOnItemAtPosition<UserPlantsAdapter.PlantViewHolder>(0, click()))
    }

    @Then("^app should open plant detail screen$")
    fun appShouldOpenPlantDetailScreen() {
        onView(withId(R.id.tv_common_name))
            .check(matches(isDisplayed()))
    }

    @Then("^show plant data$")
    fun showPlantData() {
        onView(withId(R.id.tv_common_name))
            .check(matches(withText(herbPlant.commonName)))
        onView(withId(R.id.tv_scientific_name))
            .check(matches(withText(herbPlant.scientificName)))
    }

    private fun createPlant(
        id: Int,
        commonName: String,
        scientificName: String,
        category: PlantCategory
    ): Plant {
        return Plant(
            id = id,
            commonName = commonName,
            scientificName = scientificName,
            imageUrl = "url_$id",
            category = category,
            subcategory = "Sub_$id",
            daysToGermination = null,
            daysToMaturity = null,
            germinationSoilTemp = null,
            matureHeight = null,
            matureWidth = null,
            bloomSeason = BloomingSeason.COOL,
            rowSpacing = null,
            sunCare = PlantSunCare.FULL,
            waterCare = PlantWaterCare.LOW,
            directions = "Directions for $commonName"
        )
    }
}
