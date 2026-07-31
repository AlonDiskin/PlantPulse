package com.alon.plantpulse.usergarden.ui.util

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.matcher.BoundedMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher

fun atPosition(recyclerViewId: Int, position: Int): Matcher<View> {
    return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
        override fun describeTo(description: Description) {
            description.appendText("has item at position $position in RecyclerView with id $recyclerViewId")
        }

        override fun matchesSafely(recyclerView: RecyclerView): Boolean {
            if (recyclerView.id != recyclerViewId) return false
            val viewHolder = recyclerView.findViewHolderForAdapterPosition(position)
                ?: return false // View is not bound/visible yet
            return true
        }
    }
}

fun hasItemCount(count: Int): Matcher<View> {
    return object : BoundedMatcher<View, RecyclerView>(RecyclerView::class.java) {
        override fun describeTo(description: Description) {
            description.appendText("RecyclerView with item count: $count")
        }

        override fun matchesSafely(recyclerView: RecyclerView): Boolean {
            return recyclerView.adapter?.itemCount == count
        }
    }
}