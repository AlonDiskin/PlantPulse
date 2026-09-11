package com.alon.plantpulse.usergarden.ui.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import com.alon.plantpulse.plantsdetail.ui.R
import com.alon.plantpulse.plantsdetail.ui.databinding.FragmentUserGardenBinding
import com.alon.plantpulse.usergarden.application.model.UserGardenError
import com.alon.plantpulse.usergarden.ui.viewmodel.UserGardenViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.migration.OptionalInject
import kotlin.getValue

/**
 * A fragment that displays the user's personal garden of plants.
 *
 * This fragment manages the UI for listing plants belonging to the user,
 * handling loading states, and providing entry points for adding new plants.
 */
@OptionalInject
@AndroidEntryPoint
class UserGardenFragment : Fragment() {

    private val viewModel: UserGardenViewModel by viewModels()
    private var _binding: FragmentUserGardenBinding? = null
    private val binding get() = _binding!!
    private val adapter = UserPlantsAdapter()
    private var errorSnackbar: Snackbar? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserGardenBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleFabClick()
        setupRecyclerView()
        handleSearchResultLoadState()
        observeUserPlants()
    }

    override fun onDestroyView() {
        // Dismiss snackbar to ensure it doesn't hold onto the view hierarchy
        errorSnackbar?.dismiss()
        errorSnackbar = null
        // Explicitly clear the adapter to break the view -> fragment cycle immediately
        binding.userPlantsRecyclerView.adapter = null

        super.onDestroyView()

        // Clear the binding reference
        _binding = null
    }

    private fun handleFabClick() {
        // Listen to fab clicks
        binding.addPlantFab.setOnClickListener {
            findNavController().navigate(R.id.action_userGardenFragment_to_plantsSearchFragment)
        }
    }

    private fun setupRecyclerView() {
        binding.userPlantsRecyclerView.adapter = adapter
    }

    private fun observeUserPlants() {
        viewModel.userPlants.observe(viewLifecycleOwner) { pagingData ->
            adapter.submitData(viewLifecycleOwner.lifecycle, pagingData)
        }
    }

    private fun handleSearchResultLoadState() {
        adapter.addLoadStateListener { state ->
            // Resolve load state and map to ui function

            when (state.refresh) {
                is LoadState.Loading -> {
                    // Clear any existing error message
                    errorSnackbar?.dismiss()
                    // Show loading indicator
                    binding.loadingIndicator.visibility = View.VISIBLE
                }
                is LoadState.NotLoading -> binding.loadingIndicator.visibility = View.GONE
                is LoadState.Error -> showErrorNotification((state.refresh as LoadState.Error))
            }

            when (state.append) {
                is LoadState.Loading -> {
                    // Clear any existing error message
                    errorSnackbar?.dismiss()
                    // Show loading indicator
                    binding.loadingIndicator.visibility = View.VISIBLE
                }
                is LoadState.NotLoading -> binding.loadingIndicator.visibility = View.GONE
                is LoadState.Error -> showErrorNotification((state.append as LoadState.Error))
            }

            checkEmptyGardenListing(state)
        }
    }

    private fun showErrorNotification(error: LoadState.Error) {
        when(error.error) {
            is UserGardenError.Internal -> {
                errorSnackbar = Snackbar.make(binding.root,
                    getString(R.string.error_message_internal_error), Snackbar.LENGTH_INDEFINITE)
                errorSnackbar?.show()
            }
        }
    }

    private fun checkEmptyGardenListing(loadStates: CombinedLoadStates) {
        val isRefreshDone = loadStates.refresh is LoadState.NotLoading
        val isListEmpty = adapter.itemCount == 0

        if(isRefreshDone && isListEmpty) {
            startEmptyGardenAnimation()
        } else {
            stopEmptyGardenAnimation()
        }
    }

    private fun startEmptyGardenAnimation() {
        // Show the layout and start the breathing effect
        binding.emptyGardenLayout.visibility = View.VISIBLE
        binding.emptyPotIcon.startAtmosphericPulse()
    }

    private fun stopEmptyGardenAnimation() {
        // Hide the layout
        binding.emptyGardenLayout.visibility = View.GONE

        // Kill the animation on the specific icon
        // clearAnimation() stops View animations, but for Property Animators
        // it's safest to simply clear the animation and let the View rest.
        binding.emptyPotIcon.clearAnimation()

        // Reset the scale/translation to default
        // to prevent the view from "freezing" in a half-grown state.
        binding.emptyPotIcon.scaleX = 1f
        binding.emptyPotIcon.scaleY = 1f
        binding.emptyPotIcon.translationY = 0f
    }
}
