package com.alon.plantpulse.plantcare.ui.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.alon.plantpulse.plantcare.application.model.PlantCareError
import com.alon.plantpulse.plantcare.ui.R
import com.alon.plantpulse.plantcare.ui.databinding.FragmentLightMeterBinding
import com.alon.plantpulse.plantcare.ui.viewmodel.LightMeterViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.migration.OptionalInject

/**
 * A fragment that provides real-time light intensity monitoring for a specific plant.
 *
 * This fragment uses the device's light sensor to measure lux values and compares them
 * against the plant's requirements to provide diagnostic feedback.
 */
@OptionalInject
@AndroidEntryPoint
class LightMeterFragment : Fragment() {

    private val viewModel: LightMeterViewModel by viewModels()
    private var _binding: FragmentLightMeterBinding? = null
    private val binding get() = _binding!!
    private var errorSnackbar: Snackbar? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLightMeterBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
    }

    private fun setupObservers() {
        // Observe static plant info (Name, Range)
        viewModel.plantDiagnostic.observe(viewLifecycleOwner) { state ->
            binding.plantDiagnostic = state
            state.error?.let { showErrorNotification(it) }
        }

        // Observe real-time light status (Lux, Status)
        viewModel.meterStatus.observe(viewLifecycleOwner) { state ->
            binding.meterStatus = state
            
            // Update the gauge progress
            // Parsing the luxValue back to Int for the ProgressBar
            val lux = state.luxValue.replace(",", "").toIntOrNull() ?: 0
            binding.pbLuxMeter.progress = lux

            state.error?.let { showErrorNotification(it) }
        }
    }

    private fun showErrorNotification(error: PlantCareError) {
        val message = when (error) {
            is PlantCareError.Internal -> getString(R.string.error_message_internal_error)
            is PlantCareError.LightSensorNotExist -> getString(R.string.error_message_light_sensor_not_exist)
        }

        if (errorSnackbar?.isShown == true) return

        errorSnackbar = Snackbar.make(binding.root, message, Snackbar.LENGTH_INDEFINITE)
        errorSnackbar?.show()
    }

    override fun onDestroyView() {
        errorSnackbar?.dismiss()
        errorSnackbar = null
        super.onDestroyView()
        _binding = null
    }
}
