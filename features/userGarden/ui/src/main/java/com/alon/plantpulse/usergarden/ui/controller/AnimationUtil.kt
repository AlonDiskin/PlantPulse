package com.alon.plantpulse.usergarden.ui.controller

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator

fun View.startAtmosphericPulse() {
    this.clearAnimation()

    // 1. Setup the breathing values (Scale and subtle lift)
    val scaleX = ObjectAnimator.ofFloat(this, View.SCALE_X, 1f, 1.06f)
    val scaleY = ObjectAnimator.ofFloat(this, View.SCALE_Y, 1f, 1.06f)
    val lift = ObjectAnimator.ofFloat(this, View.TRANSLATION_Y, 0f, -15f)

    AnimatorSet().apply {
        playTogether(scaleX, scaleY, lift)
        duration = 3000 // Samsung-style slow pace
        interpolator = AccelerateDecelerateInterpolator()

        addListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                // Reverse for the "Exhale"
                val exhaleX = ObjectAnimator.ofFloat(this@startAtmosphericPulse, View.SCALE_X, 1.06f, 1f)
                val exhaleY = ObjectAnimator.ofFloat(this@startAtmosphericPulse, View.SCALE_Y, 1.06f, 1f)
                val drop = ObjectAnimator.ofFloat(this@startAtmosphericPulse, View.TRANSLATION_Y, -15f, 0f)

                AnimatorSet().apply {
                    playTogether(exhaleX, exhaleY, drop)
                    duration = 3000
                    interpolator = AccelerateDecelerateInterpolator()
                    addListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            startAtmosphericPulse() // Loop
                        }
                    })
                    start()
                }
            }
        })
        start()
    }
}