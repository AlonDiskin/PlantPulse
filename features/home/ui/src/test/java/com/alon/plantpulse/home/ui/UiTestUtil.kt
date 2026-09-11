package com.alon.plantpulse.home.ui

import android.view.View
import androidx.test.espresso.matcher.BoundedMatcher
import com.google.android.material.appbar.MaterialToolbar
import org.hamcrest.Description
import org.hamcrest.Matcher

fun withToolbarTitle(expectedTitle: String): Matcher<View> {
    return object : BoundedMatcher<View, MaterialToolbar>(MaterialToolbar::class.java) {

        override fun describeTo(description: Description) {
            description.appendText("with toolbar title: $expectedTitle")
        }

        override fun matchesSafely(toolbar: MaterialToolbar): Boolean {
            return expectedTitle == toolbar.title.toString()
        }
    }
}