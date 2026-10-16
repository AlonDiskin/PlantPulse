package com.alon.plantpulse.plantcare.featuretest.lightmeter

import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Looper
import androidx.lifecycle.ViewModelStore
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.intent.matcher.IntentMatchers.hasExtra
import androidx.test.espresso.intent.matcher.IntentMatchers.hasType
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.alon.plantpulse.common.ui.HiltTestActivity
import com.alon.plantpulse.common.ui.launchFragmentInHiltContainer
import com.alon.plantpulse.plantcare.api.PlantCareData
import com.alon.plantpulse.plantcare.api.PlantCareDataProvider
import com.alon.plantpulse.plantcare.api.PlantLight
import com.alon.plantpulse.plantcare.api.PlantWatering
import com.alon.plantpulse.plantcare.ui.R
import com.alon.plantpulse.plantcare.ui.controller.LightMeterFragment
import com.google.android.material.button.MaterialButton
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.hamcrest.Matchers.allOf
import org.robolectric.Shadows

class LightDataSharedSteps(
    private val mockProvider: PlantCareDataProvider,
    private val mockSensorManager: SensorManager
) : GreenCoffeeSteps() {

    private val plantId = 101
    private val plantName = "Aloe Vera"
    private val currentLuxValue = 1200f
    
    private val navController = TestNavHostController(ApplicationProvider.getApplicationContext()).apply {
        setViewModelStore(ViewModelStore())
        setGraph(R.navigation.plant_care_nav_graph)
    }
    private lateinit var scenario: ActivityScenario<HiltTestActivity>

    private val listenerSlot = slot<SensorEventListener>()
    private val lightSensor = mockk<Sensor>()

    init {
        every { lightSensor.type } returns Sensor.TYPE_LIGHT
        every { mockSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) } returns lightSensor
        every { mockSensorManager.registerListener(capture(listenerSlot), lightSensor, any()) } returns true
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
        Intents.init()
        val args = Bundle().apply { putInt("plantId", plantId) }
        scenario = launchFragmentInHiltContainer<LightMeterFragment>(
            fragmentArgs = args,
            navController = navController
        )
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        if (listenerSlot.isCaptured) {
            val sensorEvent = createSensorEvent(currentLuxValue,lightSensor)
            listenerSlot.captured.onSensorChanged(sensorEvent)
            Shadows.shadowOf(Looper.getMainLooper()).idle()
        }
    }

    @When("^select to share the measured light data$")
    fun selectToShareTheMeasuredLightData() {
        scenario.onActivity { activity ->
            activity.findViewById<MaterialButton>(R.id.btn_share).performClick()
            Shadows.shadowOf(Looper.getMainLooper()).idle()
        }
    }

    @Then("^app should open device sharing menu$")
    fun appShouldOpenDeviceSharingMenu() {
        intended(allOf(
            hasAction(Intent.ACTION_CHOOSER),
            hasExtra(Intent.EXTRA_INTENT, allOf(
                hasAction(Intent.ACTION_SEND),
                hasType("text/plain")
            ))
        ))
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        Intents.release()
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }
}
