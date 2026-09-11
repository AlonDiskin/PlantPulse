package com.alon.plantpulse.journey

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
class AddPlantJourneyRunner(scenario: ScenarioConfig) :  GreenCoffeeTest(scenario) {

    companion object {
        @JvmStatic
        @Parameterized.Parameters(name = "{0}")
        fun scenarios(): Iterable<ScenarioConfig> {
            return GreenCoffeeConfig()
                .withFeatureFromAssets("assets/feature/add_plant_to_garden_journey.feature")
                .scenarios()
        }
    }

    @get:Rule
    val hiltRule = HiltAndroidRule(this)

    @Inject
    lateinit var db: TestDatabase

    @Test
    fun test() {
        hiltRule.inject()
        start(AddPlantJourney(db.plantDao()))
    }
}