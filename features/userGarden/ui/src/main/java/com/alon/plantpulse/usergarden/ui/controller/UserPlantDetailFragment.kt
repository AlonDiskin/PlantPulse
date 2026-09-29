package com.alon.plantpulse.usergarden.ui.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.alon.plantpulse.plantsdetail.ui.R
import com.alon.plantpulse.plantsdetail.ui.databinding.FragmentPlantDetailBinding
import com.alon.plantpulse.usergarden.application.model.UserGardenError
import com.alon.plantpulse.usergarden.ui.model.UserPlantDeleteUiState
import com.alon.plantpulse.usergarden.ui.viewmodel.UserPlantDetailViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.migration.OptionalInject

/**
 * A fragment that displays the detailed information of a specific plant in the user's garden.
 */
@OptionalInject
@AndroidEntryPoint
class UserPlantDetailFragment : Fragment() {

    private val viewModel: UserPlantDetailViewModel by viewModels()
    private var _binding: FragmentPlantDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlantDetailBinding.inflate(inflater, container, false)
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Disable delete button by default until data is loaded
        binding.btnDeletePlant.isEnabled = false
        
        setupObservers()
        setupListeners()
    }

    private fun setupObservers() {
        viewModel.uiState.observe(viewLifecycleOwner) { state ->
            state.error?.let { error ->
                showError(error)
            } ?: run {
                binding.plant = state
                // Enable delete button once data is successfully loaded
                binding.btnDeletePlant.isEnabled = true
            }
        }

        viewModel.deleteUiState.observe(viewLifecycleOwner) { state ->
            binding.btnDeletePlant.isEnabled = state !is UserPlantDeleteUiState.Loading

            when (state) {
                is UserPlantDeleteUiState.Success -> showDeletionSuccessDialog()
                is UserPlantDeleteUiState.Error -> showError(state.error)
                else -> { /* No-op for Idle */ }
            }
        }
    }

    private fun showDeletionSuccessDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setMessage(R.string.message_plant_deleted)
            .setPositiveButton(R.string.label_ok) { _, _ ->
                try {
                    findNavController().popBackStack()
                } catch (e: Exception) {
                    println("Error navigating back: ${e.message}")
                }

            }
            .setCancelable(false)
            .show()
    }

    private fun setupListeners() {
        binding.onDeleteClickListener = View.OnClickListener {
            viewModel.deletePlant()
        }
    }

    private fun showError(error: UserGardenError) {
        val messageResId = when (error) {
            is UserGardenError.Internal -> R.string.error_message_internal_error
            is UserGardenError.EmptySearchQuery -> R.string.error_message_empty_search_query
        }
        Snackbar.make(binding.root, messageResId, Snackbar.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
