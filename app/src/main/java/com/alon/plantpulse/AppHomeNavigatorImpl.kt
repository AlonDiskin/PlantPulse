package com.alon.plantpulse

import com.alon.plantpulse.home.ui.AppHomeNavigator
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppHomeNavigatorImpl @Inject constructor() : AppHomeNavigator {

    override fun getAppNavGraphId(): Int {
        return R.navigation.app_nav_graph
    }
}