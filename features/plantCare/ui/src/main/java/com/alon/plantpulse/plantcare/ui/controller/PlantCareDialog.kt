package com.alon.plantpulse.plantcare.ui.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.navigation.fragment.findNavController
import com.alon.plantpulse.plantcare.ui.R
import com.alon.plantpulse.plantcare.ui.databinding.DialogPlantCareOptionsBinding
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.migration.OptionalInject

/**
 * A dialog fragment that presents various plant care options to the user.
 *
 * Current options include:
 * - Light Meter: Navigates to the real-time light measurement screen.
 * - Water Reminder: Navigates to the watering schedule configuration screen.
 */
@OptionalInject
@AndroidEntryPoint
class PlantCareDialog : DialogFragment() {

    private var _binding: DialogPlantCareOptionsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogPlantCareOptionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupListeners()
    }

    private fun setupListeners() {
        // Retrieve plantId from arguments to pass it forward to specific care screens
        val plantId = arguments?.getInt("plantId") ?: -1

        binding.onLightMeterClick = View.OnClickListener {
            if (plantId != -1) {
                navigateToLightMeter(plantId)
            }
            dismiss()
        }

        binding.onWaterReminderClick = View.OnClickListener {
            if (plantId != -1) {
                navigateToWateringReminder(plantId)
            }
            dismiss()
        }

        binding.onCloseClick = View.OnClickListener {
            dismiss()
        }
    }

    /**
     * Navigates to the Light Meter screen for the specific plant.
     */
    private fun navigateToLightMeter(plantId: Int) {
        val bundle = bundleOf("plantId" to plantId)
        findNavController().navigate(R.id.lightMeterFragment, bundle)
    }

    /**
     * Navigates to the Watering Reminder screen for the specific plant.
     */
    private fun navigateToWateringReminder(plantId: Int) {
        val bundle = bundleOf("plantId" to plantId)
        findNavController().navigate(R.id.wateringReminderFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
