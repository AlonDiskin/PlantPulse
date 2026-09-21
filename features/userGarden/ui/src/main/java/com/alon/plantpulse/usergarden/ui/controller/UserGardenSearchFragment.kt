package com.alon.plantpulse.usergarden.ui.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import com.alon.plantpulse.plantsdetail.ui.R
import com.alon.plantpulse.plantsdetail.ui.databinding.FragmentGardenSearchBinding
import com.alon.plantpulse.usergarden.application.model.UserGardenError
import com.alon.plantpulse.usergarden.ui.viewmodel.UserGardenSearchViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.migration.OptionalInject

@OptionalInject
@AndroidEntryPoint
class UserGardenSearchFragment : Fragment() {

    private val viewModel: UserGardenSearchViewModel by viewModels()
    private var _binding: FragmentGardenSearchBinding? = null
    private val binding get() = _binding!!
    private val adapter = UserPlantsAdapter(::handlePlantClick)
    private var errorSnackbar: Snackbar? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGardenSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearchInteractions()
        observeSearchResults()
        handleSearchLoadState()
    }

    override fun onDestroyView() {
        // Dismiss snackbar to ensure it doesn't hold onto the view hierarchy
        errorSnackbar?.dismiss()
        errorSnackbar = null
        // Explicitly clear the adapter to break the view -> fragment cycle immediately
        binding.plantsRecyclerView.adapter = null

        super.onDestroyView()

        // Clear the binding reference
        _binding = null
    }

    private fun setupRecyclerView() {
        binding.plantsRecyclerView.adapter = adapter
    }

    private fun setupSearchInteractions() {
        // Feature: Execute plants search when user performs search using the UI
        binding.searchView.editText.setOnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = textView.text.toString()
                if (query.isNotBlank()) {
                    // Perform search
                    viewModel.searchUserPlants(query)
                    // Clear any prev messages in snackbar
                    errorSnackbar?.dismiss()
                    // UI Feedback: Sync SearchBar text and hide SearchView
                    binding.searchBar.setText(query)
                    binding.searchView.hide()
                }
                true
            } else {
                false
            }
        }
    }

    private fun observeSearchResults() {
        viewModel.searchResults.observe(viewLifecycleOwner) { pagingData ->
            adapter.submitData(viewLifecycleOwner.lifecycle, pagingData)
        }
    }

    private fun handlePlantClick(plantId: Int) {
        navigateToPlantDetail(plantId)
    }

    private fun handleSearchLoadState() {
        adapter.addLoadStateListener { state ->
            // Resolve load state and map to ui function
            when (state.refresh) {
                is LoadState.Loading -> {
                    // Hide existing empty result message
                    binding.noResultsText.visibility = View.GONE
                    // Show loading indicator
                    binding.loadingIndicator.visibility = View.VISIBLE
                }
                is LoadState.NotLoading -> binding.loadingIndicator.visibility = View.GONE
                is LoadState.Error -> showErrorNotification((state.refresh as LoadState.Error))
            }

            when (state.append) {
                is LoadState.Loading -> {
                    // Show loading indicator
                    binding.loadingIndicator.visibility = View.VISIBLE
                }
                is LoadState.NotLoading -> binding.loadingIndicator.visibility = View.GONE
                is LoadState.Error -> showErrorNotification((state.append as LoadState.Error))
            }

            checkEmptySearch(state)
        }
    }

    private fun showErrorNotification(error: LoadState.Error) {
        when(error.error) {
            is UserGardenError.EmptySearchQuery -> handleEmptyQueryError()
            is UserGardenError.Internal -> handleInternalFeatureError()
        }
    }

    private fun handleEmptyQueryError() {
        errorSnackbar = Snackbar.make(binding.root,
            getString(R.string.error_message_empty_search_query), Snackbar.LENGTH_INDEFINITE)
        errorSnackbar?.show()
    }

    private fun handleInternalFeatureError() {
        errorSnackbar = Snackbar.make(binding.root,
            getString(R.string.error_message_internal_error), Snackbar.LENGTH_INDEFINITE)
        errorSnackbar?.show()
    }

    private fun checkEmptySearch(loadStates: CombinedLoadStates) {
        val isRefreshDone = loadStates.refresh is LoadState.NotLoading
        val isListEmpty = adapter.itemCount == 0

        if(isRefreshDone && isListEmpty) {
            binding.noResultsText.visibility = View.VISIBLE
        }
    }

    private fun navigateToPlantDetail(plantId: Int) {
        val bundle = bundleOf("plantId" to plantId)
        findNavController().navigate(R.id.action_gardenPlantsSearchFragment_to_userPlantDetailFragment, bundle)
    }
}