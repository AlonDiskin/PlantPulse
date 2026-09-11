package com.alon.plantpulse.usergarden.featuretest.showplants

import android.os.Looper
import android.view.View
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.testing.asSnapshot
import androidx.test.core.app.ActivityScenario
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import com.alon.plantpulse.plantsdetail.ui.R
import com.alon.plantpulse.usergarden.data.local.UserPlantDao
import com.alon.plantpulse.usergarden.ui.HiltTestActivity
import com.alon.plantpulse.usergarden.ui.controller.UserGardenFragment
import com.alon.plantpulse.usergarden.ui.controller.startAtmosphericPulse
import com.alon.plantpulse.usergarden.ui.launchFragmentInHiltContainer
import com.google.common.truth.Truth.assertThat
import com.mauriciotogneri.greencoffee.GreenCoffeeSteps
import com.mauriciotogneri.greencoffee.annotations.Given
import com.mauriciotogneri.greencoffee.annotations.Then
import com.mauriciotogneri.greencoffee.annotations.When
import io.mockk.every
import io.mockk.mockkStatic
import kotlinx.coroutines.test.runTest
import org.robolectric.Shadows

class EmptyGardenIndicatedSteps(private val userPlantDao: UserPlantDao) : GreenCoffeeSteps() {

    private lateinit var scenario: ActivityScenario<HiltTestActivity>

    @Given("^user has no plants in garden$")
    fun userHasNoPlantsInGarden() = runTest {
        val pagingFlow = Pager(
            config = PagingConfig(pageSize = 10)
        ) {
            userPlantDao.getAll()
        }.flow
        val actualPlants = pagingFlow.asSnapshot()

        assertThat(actualPlants.size).isEqualTo(0)
    }

    @When("^he open garden screen$")
    fun heOpenGardenScreen() {
        mockkStatic("com.alon.plantpulse.usergarden.ui.controller.AnimationUtilKt")
        every { any<View>().startAtmosphericPulse() } returns Unit
        scenario = launchFragmentInHiltContainer<UserGardenFragment>()
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Then("^app should show a ui indication that his garden is empty$")
    fun appShouldShowUiIndicationThatGardenIsEmpty() {
        onView(withId(R.id.empty_garden_layout))
            .check(matches(isDisplayed()))
        onView(withId(R.id.empty_pot_icon))
            .check(matches(isDisplayed()))
    }
}
