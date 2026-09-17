package com.alon.plantpulse.usergarden.featuretest.managegarden

import android.os.Looper
import android.view.View
import androidx.navigation.Navigation
import androidx.navigation.testing.TestNavHostController
import androidx.recyclerview.widget.RecyclerView
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.scrollTo
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.alon.plantpulse.plantsdetail.ui.R
import com.alon.plantpulse.usergarden.data.local.PlantDao
import com.alon.plantpulse.usergarden.data.local.UserPlant
import com.alon.plantpulse.usergarden.data.local.UserPlantDao
import com.alon.plantpulse.usergarden.featuretest.util.createPlant
import com.alon.plantpulse.usergarden.ui.HiltTestActivity
import com.alon.plantpulse.usergarden.ui.controller.UserGardenFragment
import com.alon.plantpulse.usergarden.ui.controller.UserPlantDetailFragment
import com.alon.plantpulse.usergarden.ui.launchFragmentInHiltContainer
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import kotlinx.coroutines.test.runTest
import org.robolectric.Shadows

class PlantDetailShownSteps(
    private val plantDao: PlantDao,
    private val userPlantDao: UserPlantDao
) : GreenCoffeeSteps() {

    private lateinit var scenario: ActivityScenario<HiltTestActivity>
    private val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
    private val plant = createPlant(1, "Monstera Deliciosa", "Monstera deliciosa", "image_url_1").copy(
        directions = "Keep in bright indirect light. Water when the top inch of soil is dry."
    )

    init {
        navController.setGraph(R.navigation.user_garden_nav_graph)
        navController.addOnDestinationChangedListener { _, destination, arguments ->
            if (destination.id == R.id.userPlantDetailFragment) {
                scenario.onActivity { activity ->
                    val fragment = activity.supportFragmentManager.fragments.first()
                    val containerId = (fragment.view?.parent as View).id
                    
                    val detailFragment = UserPlantDetailFragment().apply {
                        this.arguments = arguments
                    }
                    
                    activity.supportFragmentManager.beginTransaction()
                        .replace(containerId, detailFragment)
                        .addToBackStack(null)
                        .commit()
                    Shadows.shadowOf(Looper.getMainLooper()).idle()
                }
            }
        }
    }

    @Given("^user has plants in garden$")
    fun userHasPlantsInGarden() = runTest {
        plantDao.insertAll(listOf(plant))
        userPlantDao.add(UserPlant(plant.id))
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @When("^he open garden screen$")
    fun heOpenGardenScreen() {
        scenario = launchFragmentInHiltContainer<UserGardenFragment>()
        scenario.onActivity { activity ->
            val fragment = activity.supportFragmentManager.fragments.first()!!
            navController.setCurrentDestination(R.id.userGardenFragment)
            Navigation.setViewNavController(fragment.requireView(), navController)
        }
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @When("^select to view first plant detail$")
    fun selectToViewFirstPlantDetail() {
        onView(withId(R.id.user_plants_recycler_view))
            .perform(RecyclerViewActions.actionOnItemAtPosition<RecyclerView.ViewHolder>(0, click()))
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Then("^app should show plant data in plant detail screen$")
    fun appShouldShowPlantDataInPlantDetailScreen() {
        onView(withId(R.id.tv_common_name)).check(matches(withText(plant.commonName)))
        onView(withId(R.id.tv_scientific_name)).check(matches(withText(plant.scientificName)))
        onView(withId(R.id.tv_directions)).perform(scrollTo()).check(matches(withText(plant.directions)))
    }
}
