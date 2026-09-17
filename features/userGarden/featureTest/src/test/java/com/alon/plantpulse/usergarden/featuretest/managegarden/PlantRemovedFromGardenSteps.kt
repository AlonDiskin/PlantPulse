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
import androidx.test.espresso.matcher.RootMatchers.isDialog
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
import com.google.common.truth.Truth.assertThat
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.And
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.robolectric.Shadows

class PlantRemovedFromGardenSteps(
    private val plantDao: PlantDao,
    private val userPlantDao: UserPlantDao
) : GreenCoffeeSteps() {

    private lateinit var scenario: ActivityScenario<HiltTestActivity>
    private val navController = TestNavHostController(ApplicationProvider.getApplicationContext())
    private val plant = createPlant(1, "Snake Plant", "Sansevieria trifasciata", "url_snake")

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
                    activity.supportFragmentManager.executePendingTransactions()
                    detailFragment.view?.let {
                        Navigation.setViewNavController(it, navController)
                    }
                }
            } else if (destination.id == R.id.userGardenFragment) {
                if (::scenario.isInitialized) {
                    // TODO currently this code is not working due to sync issue between robolectric and fragments apis
//                    scenario.onActivity { activity ->
//                        if (activity.supportFragmentManager.backStackEntryCount > 0) {
//                            //activity.supportFragmentManager.popBackStackImmediate()
//                        }
//                    }
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

    @Then("^app should open plant detail screen$")
    fun appShouldOpenPlantDetailScreen() {
        onView(withId(R.id.tv_common_name)).check(matches(withText(plant.commonName)))
    }

    @When("^he select to delete plant via plant detail screen option$")
    fun heSelectToDeletePlantViaPlantDetailScreenOption() {
        onView(withId(R.id.btn_delete_plant)).perform(scrollTo(), click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @And("^confirm delete operation$")
    fun confirmDeleteOperation() {
        onView(withText(R.string.label_ok)).inRoot(isDialog()).perform(click())
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Then("^app should remove plant from garden$")
    fun appShouldRemovePlantFromGarden() = runTest {
        val ids = userPlantDao.getAllIds().first()
        assertThat(ids).doesNotContain(plant.id)
    }

    @Then("^return to garden screen$")
    fun returnToGardenScreen() {
        assertThat(navController.currentDestination?.id).isEqualTo(R.id.userGardenFragment)
        // TODO currently due to inability of poping back to garden fragment, this code could not be verified
//        onView(withId(R.id.user_plants_recycler_view)).check(matches(isDisplayed()))
//        onView(withId(R.id.user_plants_recycler_view)).check(matches(hasItemCount(0)))
    }
}
