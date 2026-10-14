package com.alon.plantpulse.plantcare.featuretest.lightmeter

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Looper
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelStore
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.alon.plantpulse.common.ui.HiltTestActivity
import com.alon.plantpulse.common.ui.launchDialogInHiltContainer
import com.alon.plantpulse.plantcare.api.PlantCareData
import com.alon.plantpulse.plantcare.api.PlantCareDataProvider
import com.alon.plantpulse.plantcare.api.PlantLight
import com.alon.plantpulse.plantcare.ui.R
import com.alon.plantpulse.plantcare.ui.controller.LightMeterFragment
import com.alon.plantpulse.plantcare.ui.controller.PlantCareDialog
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.And
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import org.robolectric.Shadows

class PlantLightMeasuredSteps(
    private val mockProvider: PlantCareDataProvider,
    private val mockSensorManager: SensorManager
) : GreenCoffeeSteps() {

    private val plantId = 101
    private val plantName = "Aloe Vera"
    private val recommendedRangeText = "500 ~ 1500 Lux"
    private val currentLuxValue = 1200f
    private val currentLuxFormatted = "1,200"

    private val navController = TestNavHostController(ApplicationProvider.getApplicationContext()).apply {
        setViewModelStore(ViewModelStore())
        setGraph(R.navigation.plant_care_nav_graph)
        setCurrentDestination(R.id.plantCareDialog)
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
            light = PlantLight.SHADE
        )
    }

    @When("^he open plant care options screen$")
    fun heOpenPlantCareOptionsScreen() {
        val args = Bundle().apply { putInt("plantId", plantId) }
        launchDialogInHiltContainer<PlantCareDialog>(dialogArgs = args) {
            PlantCareDialog()
        }.onActivity { activity ->
            val dialog = activity.supportFragmentManager.findFragmentByTag(PlantCareDialog::class.java.name) as DialogFragment
            Navigation.setViewNavController(dialog.requireView(), navController)

            navController.addOnDestinationChangedListener { _, destination, arguments ->
                if (destination.id == R.id.lightMeterFragment) {
                    val lightMeterFragment = LightMeterFragment().apply {
                        this.arguments = arguments
                    }
                    activity.supportFragmentManager.beginTransaction()
                        .replace(android.R.id.content, lightMeterFragment)
                        .commitNow()

                    Navigation.setViewNavController(lightMeterFragment.requireView(), navController)
                }
            }
        }
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @And("^select to use plant light meter$")
    fun selectToUsePlantLightMeter() {
        onView(withId(R.id.btn_light_meter))
            .inRoot(isDialog())
            .perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Then("^app should open light meter screen$")
    fun appShouldOpenLightMeterScreen() {
        onView(withId(R.id.pb_lux_meter)).check(matches(isDisplayed()))
    }

    @And("^display current light measure, with plant recommended scale$")
    fun displayCurrentLightMeasureWithPlantRecommendedScale() {
        if (listenerSlot.isCaptured) {
            val sensorEvent = createSensorEvent(currentLuxValue)
            listenerSlot.captured.onSensorChanged(sensorEvent)
            Shadows.shadowOf(Looper.getMainLooper()).idle()
        }

        onView(withId(R.id.tv_plant_name)).check(matches(withText(plantName)))
        onView(withId(R.id.tv_recommended_range)).check(matches(withText(recommendedRangeText)))
        onView(withId(R.id.tv_current_lux)).check(matches(withText(currentLuxFormatted)))
    }

    @And("^light meter should conclude if light amount is sufficient for viewed plant$")
    fun lightMeterShouldConcludeIfLightAmountIsSufficientForViewedPlant() {
        onView(withId(R.id.tv_light_status)).check(matches(withText("Sufficient")))
    }

    private fun createSensorEvent(value: Float): SensorEvent {
        val event = mockk<SensorEvent>()
        val sensorField = SensorEvent::class.java.getField("sensor")
        sensorField.isAccessible = true
        sensorField.set(event, lightSensor)

        val valuesField = SensorEvent::class.java.getField("values")
        valuesField.isAccessible = true
        valuesField.set(event, floatArrayOf(value))

        return event
    }
}
