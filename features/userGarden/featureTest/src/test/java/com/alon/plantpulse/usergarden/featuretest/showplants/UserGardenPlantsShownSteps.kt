package com.alon.plantpulse.usergarden.featuretest.showplants

import android.os.Looper
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.hasDescendant
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import com.alon.plantpulse.plantsdetail.ui.R
import com.alon.plantpulse.usergarden.data.local.PlantDao
import com.alon.plantpulse.usergarden.data.local.UserPlant
import com.alon.plantpulse.usergarden.data.local.UserPlantDao
import com.alon.plantpulse.usergarden.featuretest.util.atPosition
import com.alon.plantpulse.usergarden.featuretest.util.createPlant
import com.alon.plantpulse.usergarden.featuretest.util.hasItemCount
import com.alon.plantpulse.usergarden.ui.HiltTestActivity
import com.alon.plantpulse.usergarden.ui.controller.UserGardenFragment
import com.alon.plantpulse.usergarden.ui.controller.UserPlantsAdapter
import com.alon.plantpulse.usergarden.ui.launchFragmentInHiltContainer
import com.alon.plantpulse.usergarden.ui.model.UserPlantUiState
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import kotlinx.coroutines.test.runTest
import org.robolectric.Shadows

class UserGardenPlantsShownSteps(private val plantDao: PlantDao,
                                 private val userPlantDao: UserPlantDao) : GreenCoffeeSteps() {

    private lateinit var scenario: ActivityScenario<HiltTestActivity>
    private lateinit var expectedUserPlants: MutableList<UserPlantUiState>

    @Given("^user has plants in garden$")
    fun userHasPlantsInGarden() = runTest {
        val plants = listOf(
            createPlant(1, "Monstera Deliciosa", "Monstera deliciosa", "image_url_1"),
            createPlant(2, "Snake Plant", "Sansevieria trifasciata", "image_url_2")
        )
        val userPlants = listOf(
            UserPlant(1),
            UserPlant(2)
        )

        expectedUserPlants = plants.map {  UserPlantUiState(it.id,it.commonName,it.scientificName,it.imageUrl,"") }.toMutableList()

        plantDao.insertAll(plants)
        userPlantDao.add(userPlants[0])
        userPlantDao.add(userPlants[1])
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @When("^he open garden screen$")
    fun heOpenGardenScreen() {
        scenario = launchFragmentInHiltContainer<UserGardenFragment>()
        Shadows.shadowOf(Looper.getMainLooper()).idle()
        Thread.sleep(2000)
    }

    @Then("^app should show all his garden plants$")
    fun appShouldShowAllHisGardenPlants() {
        onView(withId(R.id.user_plants_recycler_view))
            .check(matches(hasItemCount(expectedUserPlants.size)))

        expectedUserPlants.forEachIndexed { index, item ->
            onView(withId(R.id.user_plants_recycler_view))
                .perform(RecyclerViewActions.scrollToPosition<UserPlantsAdapter.PlantViewHolder>(index))
            Shadows.shadowOf(Looper.getMainLooper()).idle()

            onView(atPosition(R.id.user_plants_recycler_view, index))
                .check(matches(hasDescendant(withText(item.commonName))))
                .check(matches(hasDescendant(withText(item.scientificName))))
                .check(matches(hasDescendant(withText(item.category))))
            Shadows.shadowOf(Looper.getMainLooper()).idle()
        }
    }
}
