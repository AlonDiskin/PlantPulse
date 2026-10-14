package com.alon.plantpulse.journey

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
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
import com.mauriciotogneri.greencoffee.annotations.And
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import kotlinx.coroutines.test.runTest

class DiagnosePlantLightJourney(
    private val plantDao: PlantDao,
    private val userPlantDao: UserPlantDao,
    private val mockSensorManager: SensorManager
) : GreenCoffeeSteps() {

    private val testPlant = Plant(
        id = 1,
        commonName = "Snake Plant",
        scientificName = "Sansevieria trifasciata",
        imageUrl = "url",
        category = PlantCategory.HERB,
        subcategory = "Succulent",
        daysToGermination = null,
        daysToMaturity = null,
        germinationSoilTemp = null,
        matureHeight = null,
        matureWidth = null,
        bloomSeason = BloomingSeason.WARM,
        rowSpacing = null,
        sunCare = PlantSunCare.SHADE,
        waterCare = PlantWaterCare.LOW,
        directions = "Test directions"
    )

    private val listenerSlot = slot<SensorEventListener>()
    private val lightSensor = mockk<Sensor>()

    init {
        every { lightSensor.type } returns Sensor.TYPE_LIGHT
        every { mockSensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) } returns lightSensor
        every { mockSensorManager.registerListener(capture(listenerSlot), lightSensor, any()) } returns true
    }

    @Given("^user has plants in his garden$")
    fun userHasPlantsInHisGarden() = runTest {
        plantDao.insertAll(listOf(testPlant))
        userPlantDao.add(UserPlant(testPlant.id))
    }

    @When("^he open app from device home$")
    fun heOpenAppFromDeviceHome() {
        DeviceUtil.launchApp()
    }

    @And("^open garden screen$")
    fun openGardenScreen() {
        Thread.sleep(2000)
        onView(withId(R.id.user_plants_recycler_view))
            .check(matches(isDisplayed()))
    }

    @And("^select to view first listed plant detail$")
    fun selectToViewFirstListedPlantDetail() {
        onView(withId(R.id.user_plants_recycler_view))
            .perform(RecyclerViewActions.actionOnItemAtPosition<UserPlantsAdapter.PlantViewHolder>(0, click()))
    }

    @When("^he select to use plant light meter$")
    fun heSelectToUsePlantLightMeter() {
        Thread.sleep(2000)
        // 1. Click the Care FAB in Plant Detail
        onView(withId(R.id.fab_care)).perform(click())

        Thread.sleep(2000)
        // 2. Click the Light Meter button in the Care Dialog
        onView(withId(com.alon.plantpulse.plantcare.ui.R.id.btn_light_meter)).perform(click())
    }

    @Then("^app should open light meter screen for his viewed plant$")
    fun appShouldOpenLightMeterScreenForHisViewedPlant() {
        Thread.sleep(2000)
        onView(withId(com.alon.plantpulse.plantcare.ui.R.id.pb_lux_meter)).check(matches(isDisplayed()))
        onView(withId(com.alon.plantpulse.plantcare.ui.R.id.tv_plant_name)).check(matches(withText(testPlant.commonName)))
    }

    @And("^display the current measured light level for plant$")
    fun displayTheCurrentMeasuredLightLevelForPlant() {
        val luxValue = 1200f

        if (listenerSlot.isCaptured) {
            val event = createSensorEvent(luxValue)
            listenerSlot.captured.onSensorChanged(event)
        }

        Thread.sleep(2000)
        onView(withId(com.alon.plantpulse.plantcare.ui.R.id.tv_current_lux)).check(matches(withText("1,200")))
        onView(withId(com.alon.plantpulse.plantcare.ui.R.id.tv_light_status)).check(matches(withText("Sufficient")))
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
