package com.alon.plantpulse.plantcare.ui.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.alon.plantpulse.plantcare.ui.R
import com.alon.plantpulse.plantcare.ui.databinding.FragmentWateringReminderBinding
import com.alon.plantpulse.plantcare.ui.viewmodel.WateringReminderViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.migration.OptionalInject

/**
 * A fragment that allows users to schedule and manage watering reminders for a specific plant.
 */
@OptionalInject
@AndroidEntryPoint
class WateringReminderFragment : Fragment() {

    private val viewModel: WateringReminderViewModel by viewModels()
    private var _binding: FragmentWateringReminderBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWateringReminderBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupObservers()
    }

    private fun setupObservers() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            // Current partial implementation: Update plant name and recommended frequency
            binding.uiState = state
            
            // Handle error state
            state.error?.let {
                Snackbar.make(
                    binding.root,
                    R.string.error_message_internal_error,
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
