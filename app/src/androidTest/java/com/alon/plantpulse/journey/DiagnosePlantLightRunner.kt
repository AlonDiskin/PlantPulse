package com.alon.plantpulse.journey

import android.hardware.SensorManager
import androidx.test.filters.LargeTest
import com.alon.plantpulse.di.AppDataModule
import com.alon.plantpulse.util.TestDatabase
import com.mauriciotogneri.greencoffee.GreenCoffeeConfig
import com.mauriciotogneri.greencoffee.GreenCoffeeTest
import com.mauriciotogneri.greencoffee.ScenarioConfig
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.UninstallModules
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.Parameterized
import javax.inject.Inject

@UninstallModules(AppDataModule::class)
@HiltAndroidTest
@RunWith(Parameterized::class)
@LargeTest
class DiagnosePlantLightRunner(scenario: ScenarioConfig) :  GreenCoffeeTest(scenario) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun scenarios(): Iterable<ScenarioConfig> {
            return GreenCoffeeConfig()
                .withFeatureFromAssets("assets/feature/diagnose_plant_light_journey.feature")
                .scenarios()
        }
    }

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var db: TestDatabase
    @Inject
    lateinit var mockSensorManager: SensorManager

    @Test
    fun test() {
        hiltRule.inject()
        start(DiagnosePlantLightJourney(db.plantDao(), db.userPlantDao(), mockSensorManager))
    }
}