package com.alon.plantpulse.plantcare.featuretest.lightmeter

import android.hardware.Sensor
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Looper
import androidx.lifecycle.ViewModelStore
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.alon.plantpulse.common.ui.launchFragmentInHiltContainer
import com.alon.plantpulse.plantcare.api.PlantCareData
import com.alon.plantpulse.plantcare.api.PlantCareDataProvider
import com.alon.plantpulse.plantcare.api.PlantLight
import com.alon.plantpulse.plantcare.api.PlantWatering
import com.alon.plantpulse.plantcare.ui.R
import com.alon.plantpulse.plantcare.ui.controller.LightMeterFragment
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import org.robolectric.Shadows

class InstructionsProvidedSteps(
    private val mockProvider: PlantCareDataProvider,
    private val mockSensorManager: SensorManager
) : GreenCoffeeSteps() {

    private val plantId = 101
    private val plantName = "Aloe Vera"
    private val lightSensor = mockk<Sensor>()

    private val navController = TestNavHostController(ApplicationProvider.getApplicationContext()).apply {
        setViewModelStore(ViewModelStore())
        setGraph(R.navigation.plant_care_nav_graph)
    }

    init {
        every { lightSensor.type } returns Sensor.TYPE_LIGHT
        every { mockSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) } returns lightSensor
        every { mockSensorManager.registerListener(any<SensorEventListener>(), any(), any()) } returns true
    }

    @Given("^user has a plant in garden$")
    fun userHasAPlantInGarden() {
        coEvery { mockProvider.providePlantCareData(plantId) } returns PlantCareData(
            id = plantId,
            commonName = plantName,
            light = PlantLight.SHADE,
            watering = PlantWatering.MODERATE
        )
    }

    @When("^he open plant light meter screen$")
    fun heOpenPlantLightMeterScreen() {
        val args = Bundle().apply { putInt("plantId", plantId) }
        launchFragmentInHiltContainer<LightMeterFragment>(
            fragmentArgs = args,
            navController = navController
        )
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Then("^app should show user instructions for light meter usage$")
    fun appShouldShowUserInstructionsForLightMeterUsage() {
        onView(withId(R.id.tv_instruction_label))
            .check(matches(isDisplayed()))
            .check(matches(withText(R.string.instructions_light_meter)))
    }
}
