package com.alon.plantpulse.journey

import androidx.test.espresso.Espresso
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.pressImeActionButton
import androidx.test.espresso.action.ViewActions.typeText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import com.alon.plantpulse.plantsdetail.ui.R
import com.alon.plantpulse.usergarden.data.local.Plant
import com.alon.plantpulse.usergarden.data.local.PlantDao
import com.alon.plantpulse.usergarden.domain.PlantCategory
import com.alon.plantpulse.util.DeviceUtil
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import kotlinx.coroutines.test.runTest
import org.hamcrest.Matchers.allOf

class AddPlantJourney(private val plantDao: PlantDao) : GreenCoffeeSteps() {

    private lateinit var plant: Plant
    private lateinit var query: String

    @Given("^user open app from device home$")
    fun userOpenAppFromDeviceHome() = runTest {
        // Add test plants to db
        plant = createPlant(
            1,
            "Monstera",
            "Monstera Deliciosa",
            PlantCategory.HERB,
            "image_url_2"
        )
        query = plant.commonName

        plantDao.insertAll(listOf(plant))
        // Launch app
        DeviceUtil.launchApp()
    }

    @When("^he select to add new plant to his garden$")
    fun heSelectToAddNewPlantToHisGarden() {
        onView(withId(R.id.add_plant_fab))
            .perform(click())
    }

    @Then("^plants search screen should open$")
    fun plantsSearchScreenShouldOpen() {
        onView(withId(R.id.search_bar))
            .check(matches(isDisplayed()))
    }

    @When("^he enters a query for monstera plant species$")
    fun heEntersAQueryForMonsteraPlantSpecies() {
        // Click the SearchBar to expose the inner EditText
        val searchBar = DeviceUtil.getDevice().wait(
            Until.findObject(By.text("Search plants")),
            2000
        )
        // Call click safely after verifying non-null
        searchBar?.click() ?: throw AssertionError("SearchBar view was not found on screen.")

        onView(withId(com.google.android.material.R.id.open_search_view_edit_text))
            //.check(matches(isDisplayed()))
            .perform(typeText(query), pressImeActionButton())
        Thread.sleep(2000)
    }

    @Then("^app should list all known monstera species plants$")
    fun appShouldListAllKnownMonsteraSpeciesPlants() {
        onView(withId(R.id.plant_common_name))
            .check(matches(allOf(isDisplayed(), withText(plant.commonName))))
    }

    @When("^user select the monstera plant he wants to add$")
    fun userSelectTheMonsteraPlantHeWantsToAdd() {
        onView(withId(R.id.add_plant_button))
            .perform(click())
    }

    @Then("^app should confirm plant added$")
    fun appShouldConfirmPlantAdded() {
        Thread.sleep(2000)
        onView(withText(R.string.message_plant_added))
            .check(matches(isDisplayed()))
    }

    @When("^user navigates to garden screen$")
    fun userNavigatesToGardenScreen() {
        Espresso.pressBack()
    }

    @Then("^app should list his new added monstera$")
    fun appShouldListHisNewAddedMonstera() {
        Thread.sleep(2000)
        onView(withId(R.id.plant_common_name))
            .check(matches(allOf(isDisplayed(), withText(plant.commonName))))
        onView(withId(R.id.plant_scientific_name))
            .check(matches(allOf(isDisplayed(), withText(plant.scientificName))))
        onView(withId(R.id.plant_category))
            .check(matches(allOf(isDisplayed(), withText(plant.category?.name?.lowercase()))))
    }

    fun createPlant(
        id: Int,
        commonName: String,
        scientificName: String,
        category: PlantCategory,
        imageUrl: String
    ): Plant {
        return Plant(
            id = id,
            commonName = commonName,
            scientificName = scientificName,
            imageUrl = imageUrl,
            category = category,
            subcategory = "",
            daysToGermination = null,
            daysToMaturity = null,
            germinationSoilTemp = null,
            matureHeight = null,
            matureWidth = null,
            bloomSeason = null,
            rowSpacing = null,
            sunCare = null,
            waterCare = null,
            directions = ""
        )
    }
}
