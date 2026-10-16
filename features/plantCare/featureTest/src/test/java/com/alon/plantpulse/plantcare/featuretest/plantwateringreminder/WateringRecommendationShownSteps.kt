package com.alon.plantpulse.plantcare.featuretest.plantwateringreminder

import android.os.Bundle
import android.os.Looper
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.alon.plantpulse.common.ui.HiltTestActivity
import com.alon.plantpulse.common.ui.launchFragmentInHiltContainer
import com.alon.plantpulse.plantcare.api.PlantCareData
import com.alon.plantpulse.plantcare.api.PlantCareDataProvider
import com.alon.plantpulse.plantcare.api.PlantLight
import com.alon.plantpulse.plantcare.api.PlantWatering
import com.alon.plantpulse.plantcare.ui.R
import com.alon.plantpulse.plantcare.ui.controller.WateringReminderFragment
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import io.mockk.coEvery
import org.robolectric.Shadows

class WateringRecommendationShownSteps(private val mockProvider: PlantCareDataProvider) : GreenCoffeeSteps() {

    private val plantId = 101
    private val watering = PlantWatering.MODERATE
    private lateinit var scenario: ActivityScenario<HiltTestActivity>

    @Given("^user has plant in his garden$")
    fun userHasPlantInHisGarden() {
        coEvery { mockProvider.providePlantCareData(plantId) } returns PlantCareData(
            id = plantId,
            commonName = "Aloe Vera",
            light = PlantLight.SHADE,
            watering = watering
        )
    }

    @When("^he open water reminder scheduler screen for plant$")
    fun heOpenWaterReminderSchedulerForPlant() {
        val args = Bundle().apply { putInt("plantId", plantId) }
        scenario = launchFragmentInHiltContainer<WateringReminderFragment>(fragmentArgs = args)
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Then("^app should show the recommended watering for plant$")
    fun appShouldShowTheRecommendedWateringForPlant() {
        onView(withId(R.id.tv_recommended_watering))
            .check(matches(withText("Recommended: Every 7-14 days")))
    }
}
