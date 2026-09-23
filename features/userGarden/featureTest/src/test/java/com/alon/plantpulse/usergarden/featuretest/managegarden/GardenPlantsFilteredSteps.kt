package com.alon.plantpulse.usergarden.featuretest.managegarden

import android.os.Looper
import androidx.fragment.app.DialogFragment
import androidx.lifecycle.ViewModelStore
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.alon.plantpulse.plantsdetail.ui.R
import com.alon.plantpulse.usergarden.data.local.PlantDao
import com.alon.plantpulse.usergarden.data.local.UserPlant
import com.alon.plantpulse.usergarden.data.local.UserPlantDao
import com.alon.plantpulse.usergarden.domain.BloomingSeason
import com.alon.plantpulse.usergarden.domain.PlantCategory
import com.alon.plantpulse.usergarden.domain.PlantSunCare
import com.alon.plantpulse.usergarden.domain.PlantWaterCare
import com.alon.plantpulse.usergarden.featuretest.util.createPlant
import com.alon.plantpulse.usergarden.featuretest.util.hasItemCount
import com.alon.plantpulse.usergarden.ui.HiltTestActivity
import com.alon.plantpulse.usergarden.ui.controller.GardenPlantsFiltersDialog
import com.alon.plantpulse.usergarden.ui.controller.UserGardenFragment
import com.alon.plantpulse.usergarden.ui.launchFragmentInHiltContainer
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.And
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import kotlinx.coroutines.test.runTest
import org.robolectric.Shadows

class GardenPlantsFilteredSteps(
    private val plantDao: PlantDao,
    private val userPlantDao: UserPlantDao
) : GreenCoffeeSteps() {

    private lateinit var scenario: ActivityScenario<HiltTestActivity>
    private val navController = TestNavHostController(ApplicationProvider.getApplicationContext()).apply {
        setViewModelStore(ViewModelStore())
        setGraph(R.navigation.user_garden_nav_graph)
        setCurrentDestination(R.id.userGardenFragment)
    }

    init {
        navController.addOnDestinationChangedListener { _, destination, arguments ->
            if (destination.id == R.id.gardenPlantsFiltersDialog) {
                scenario.onActivity { activity ->
                    val dialog = GardenPlantsFiltersDialog().apply {
                        this.arguments = arguments
                    }
                    dialog.show(activity.supportFragmentManager,"filters dialog")
                    activity.supportFragmentManager.executePendingTransactions()
                    dialog.view?.let { Navigation.setViewNavController(it, navController) }
                }
                Shadows.shadowOf(Looper.getMainLooper()).idle()
            }

            if (destination.id == R.id.userGardenFragment) {
                if (::scenario.isInitialized) {
                    scenario.onActivity { activity ->
                        val dialogFragment = activity.supportFragmentManager
                            .findFragmentByTag("filters dialog") as? DialogFragment
                        dialogFragment?.dismiss()
                    }
                    Shadows.shadowOf(Looper.getMainLooper()).idle()
                }
            }
        }
    }

    @Given("^user has plants in garden$")
    fun userHasPlantsInGarden() = runTest {
        val plants = listOf(
            createPlant(1, "Herb Warm Full Low", "Scientific 1", "url1").copy(
                category = PlantCategory.HERB,
                bloomSeason = BloomingSeason.WARM,
                sunCare = PlantSunCare.FULL,
                waterCare = PlantWaterCare.LOW
            ),
            createPlant(2, "Fruit Cool Partial High", "Scientific 2", "url2").copy(
                category = PlantCategory.FRUIT,
                bloomSeason = BloomingSeason.COOL,
                sunCare = PlantSunCare.PARTIAL,
                waterCare = PlantWaterCare.HIGH
            ),
            createPlant(3, "Other", "Scientific 3", "url3").copy(
                category = PlantCategory.VEGETABLE,
                bloomSeason = BloomingSeason.PERENNIAL,
                sunCare = PlantSunCare.SHADE,
                waterCare = PlantWaterCare.MODERATE
            )
        )
        plantDao.insertAll(plants)
        userPlantDao.add(UserPlant(1))
        userPlantDao.add(UserPlant(2))
        userPlantDao.add(UserPlant(3))
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @When("^he open user garden screen$")
    fun heOpenUserGardenScreen() {
        scenario = launchFragmentInHiltContainer<UserGardenFragment>(navController = navController)
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }
    
    @And("^open filters screen$")
    fun openFiltersScreen() {
        onView(withId(R.id.menu_filter)).perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()        
    }

    @When("^apply filters for \"([^\"]*)\" category, \"([^\"]*)\" season, \"([^\"]*)\" sun care, \"([^\"]*)\" water care$")
    fun applyFilters(category: String, season: String, sunCare: String, waterCare: String) {
        val categoryId = when(category.lowercase()) {
            "herb" -> R.id.filter_herb
            "fruit" -> R.id.filter_fruit
            "vegetable" -> R.id.filter_vegetable
            "flower" -> R.id.filter_flower
            else -> throw IllegalArgumentException("Unknown category: $category")
        }
        
        val seasonId = when(season.lowercase()) {
            "warm" -> R.id.filter_warm
            "cool" -> R.id.filter_cool
            "perennial" -> R.id.filter_Perennial
            else -> throw IllegalArgumentException("Unknown season: $season")
        }
        
        val sunCareId = when(sunCare.lowercase()) {
            "full" -> R.id.filter_full_sun
            "partial" -> R.id.filter_partial_sun
            "shade" -> R.id.filter_shade
            else -> throw IllegalArgumentException("Unknown sun care: $sunCare")
        }
        
        val waterCareId = when(waterCare.lowercase()) {
            "low" -> R.id.filter_low_water
            "moderate" -> R.id.filter_moderate_water
            "high" -> R.id.filter_hige_water
            else -> throw IllegalArgumentException("Unknown water care: $waterCare")
        }

        onView(withId(categoryId)).inRoot(isDialog()).perform(scrollTo(), click())
        onView(withId(seasonId)).inRoot(isDialog()).perform(scrollTo(), click())
        onView(withId(sunCareId)).inRoot(isDialog()).perform(scrollTo(), click())
        onView(withId(waterCareId)).inRoot(isDialog()).perform(scrollTo(), click())
        
        onView(withId(R.id.btn_apply)).inRoot(isDialog()).perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Then("^app should show only those plants that match filters$")
    fun appShouldShowOnlyThosePlantsThatMatchFilters() {
        onView(withId(R.id.user_plants_recycler_view))
            .check(matches(hasItemCount(1)))
    }
}
