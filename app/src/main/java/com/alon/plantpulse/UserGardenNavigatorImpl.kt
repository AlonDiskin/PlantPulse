package com.alon.plantpulse

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import androidx.core.os.bundleOf
import androidx.navigation.findNavController
import com.alon.plantpulse.usergarden.ui.controller.UserGardenNavigator
import javax.inject.Inject

class UserGardenNavigatorImpl @Inject constructor(
    private val activity: Activity
) : UserGardenNavigator {
    override fun navigateToPlantCare(plantId: Int) {
        val navController = (activity as AppCompatActivity)
            .findNavController(com.alon.plantpulse.home.ui.R.id.nav_host_fragment) // Your NavHostFragment ID

        val args = bundleOf("plantId" to plantId)
        // Navigate to the start destination of the included graph
        navController.navigate(com.alon.plantpulse.plantcare.ui.R.id.plant_care_nav_graph, args)
    }
}