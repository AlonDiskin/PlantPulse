package com.alon.plantpulse.usergarden.ui.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import com.alon.plantpulse.usergarden.application.model.PlantsDetailError
import com.alon.plantpulse.plantsdetail.ui.R
import com.alon.plantpulse.plantsdetail.ui.databinding.FragmentPlantsSearchBinding
import com.alon.plantpulse.usergarden.ui.viewmodel.PlantsSearchViewModel
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.migration.OptionalInject

/**
 * Fragment that handles the search UI for plants.
 * Delegates search execution to [PlantsSearchViewModel] and displays paginated results.
 */
@OptionalInject
@AndroidEntryPoint
class PlantsSearchFragment : Fragment() {

    private val viewModel: PlantsSearchViewModel by viewModels()
    private var _binding: FragmentPlantsSearchBinding? = null
    private val binding get() = _binding!!
    private val adapter = PlantsSearchAdapter()
    private var errorSnackbar: Snackbar? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlantsSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearchInteractions()
        observeSearchResults()
        handleSearchResultLoadState()
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

    private fun observeSearchResults() {
        viewModel.searchResults.observe(viewLifecycleOwner) { pagingData ->
            adapter.submitData(viewLifecycleOwner.lifecycle, pagingData)
        }
    }

    private fun setupSearchInteractions() {
        // Feature: Execute plants search when user performs search using the UI
        binding.searchView.editText.setOnEditorActionListener { textView, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val query = textView.text.toString()
                if (query.isNotBlank()) {
                    viewModel.searchPlants(query)
                    
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

    private fun handleSearchResultLoadState() {
        adapter.addLoadStateListener { state ->
            // Resolve load state and map to ui function

            when (state.refresh) {
                is LoadState.Loading -> {
                    // Clear any existing error message
                    errorSnackbar?.dismiss()
                    // Hide existing empty result message
                    binding.noResultsText.visibility = View.GONE
                    // Show loading indicator
                    binding.loadingIndicator.visibility = View.VISIBLE
                }
                is LoadState.NotLoading -> {
                    // Hide loading indicator
                    binding.loadingIndicator.visibility = View.GONE
                }
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

            checkEmptySearch(state)
        }
    }

    private fun showErrorNotification(error: LoadState.Error) {
        when(error.error) {
            is PlantsDetailError.EmptySearchQuery -> handleEmptyQueryError()
            is PlantsDetailError.DeviceConnection -> handleDeviceConnectionError()
            is PlantsDetailError.RemoteServer -> handleRemoteServerError()
            is PlantsDetailError.Internal -> handleInternalFeatureError()
        }
    }

    private fun handleEmptyQueryError() {
        errorSnackbar = Snackbar.make(binding.root,
            getString(R.string.error_message_empty_search_query), Snackbar.LENGTH_INDEFINITE)
        errorSnackbar?.show()
    }

    private fun handleDeviceConnectionError() {
        errorSnackbar = Snackbar.make(binding.root,
            getString(R.string.error_message_network_connection), Snackbar.LENGTH_INDEFINITE)
            .setAction(getString(R.string.button_retry)) { adapter.retry() }
        errorSnackbar?.show()
    }

    private fun handleRemoteServerError() {
        errorSnackbar = Snackbar.make(binding.root,
            getString(R.string.error_message_remote_server), Snackbar.LENGTH_INDEFINITE)
            .setAction(getString(R.string.button_retry)) { adapter.retry() }
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
}
