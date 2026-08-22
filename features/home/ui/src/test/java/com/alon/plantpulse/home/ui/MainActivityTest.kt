package com.alon.plantpulse.home.ui

import android.content.Context
import android.os.Looper
import androidx.navigation.fragment.findNavController
import androidx.test.core.app.ApplicationProvider
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import dagger.hilt.android.testing.HiltAndroidRule
import dagger.hilt.android.testing.HiltAndroidTest
import dagger.hilt.android.testing.HiltTestApplication
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withContentDescription
import com.google.common.truth.Truth.assertThat
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Shadows
import org.robolectric.annotation.Config
import org.robolectric.annotation.LooperMode

@HiltAndroidTest
@Config(application = HiltTestApplication::class)
@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
class MainActivityTest {

    @get:Rule(order = 0)
    var hiltRule = HiltAndroidRule(this)

    @get:Rule(order = 1)
    var activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Before
    fun init() {
        hiltRule.inject()
        Shadows.shadowOf(Looper.getMainLooper()).idle()
    }

    @Test
    fun displayAppTitleInAppBar_WhenActivityShown() {
        // Given
        val context = ApplicationProvider.getApplicationContext<Context>()
        val expectedTitle = context.getString(R.string.app_name)

        // Then
        onView(withId(R.id.toolbar))
            .check(matches(withToolbarTitle(expectedTitle)))
    }

    @Test
    fun displayAppHomeFeatureGraph_WhenActivityShown() {
        // Given

        // Then
        activityRule.scenario.onActivity { activity ->
            val fragment = activity.supportFragmentManager.fragments.first()!!
            val navController = fragment.findNavController()

            assertThat(navController.currentDestination?.id).isEqualTo(R.id.fakeHomeFeatureFragment)
        }
    }

    @Test
    fun provideUpNavigation_WhenAppHomeFeatureChangeDestinations() {
        // Given

        activityRule.scenario.onActivity { activity ->
            val fragment = activity.supportFragmentManager.fragments.first()!!
            val navController = fragment.findNavController()

            // When
            navController.navigate(R.id.action_fakeHomeFeatureFragment_to_fakeHomeFeatureDestFragment)

            Shadows.shadowOf(Looper.getMainLooper()).idle()

            // Then
            assertThat(navController.currentDestination?.id).isEqualTo(R.id.fakeHomeFeatureDestFragment)
            onView(withContentDescription(androidx.appcompat.R.string.abc_action_bar_up_description))
                .check(matches(isDisplayed()))
        }
    }
}