package com.alon.plantpulse.usergarden.ui.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.CombinedLoadStates
import androidx.paging.LoadState
import com.alon.plantpulse.plantsdetail.ui.R
import com.alon.plantpulse.plantsdetail.ui.databinding.FragmentUserGardenBinding
import com.alon.plantpulse.usergarden.application.model.UserGardenError
import com.alon.plantpulse.usergarden.ui.model.UserPlantsFiltersUiState
import com.alon.plantpulse.usergarden.ui.viewmodel.UserGardenViewModel
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.migration.OptionalInject
import kotlin.getValue

/**
 * A fragment that displays the user's personal garden collection.
 *
 * Responsibilities include:
 * - Displaying a paginated list of plants belonging to the user.
 * - Providing search and filtering capabilities via the App Bar.
 * - Visualizing active filters using dynamic [Chip] elements.
 * - Handling empty collection states with atmospheric animations.
 * - Navigating to plant details and external search screens.
 * - Managing result data from the [GardenPlantsFiltersDialog].
 */
@OptionalInject
@AndroidEntryPoint
class UserGardenFragment : Fragment() {

    private val viewModel: UserGardenViewModel by viewModels()
    private var _binding: FragmentUserGardenBinding? = null
    private val binding get() = _binding!!
    
    private val adapter = UserPlantsAdapter(::navigateToPlantDetail)
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
        setupMenu()
        handleFabClick()
        setupRecyclerView()
        handlePlantsLoadState()
        observeUserPlants()
        observePlantsFilters()
        observeReturnedDialogFilters()
    }

    override fun onDestroyView() {
        errorSnackbar?.dismiss()
        errorSnackbar = null
        binding.userPlantsRecyclerView.adapter = null
        super.onDestroyView()
        _binding = null
    }

    /**
     * Configures the [MenuProvider] for the fragment, enabling search and filter icons in the App Bar.
     */
    private fun setupMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.user_garden_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.menu_search -> {
                        navigateToUserPlantSearch()
                        true
                    }
                    R.id.menu_filter -> {
                        navigateToFiltersDialog()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    /**
     * Sets up the listener for the [R.id.add_plant_fab] to navigate to the global plant search screen.
     */
    private fun handleFabClick() {
        binding.addPlantFab.setOnClickListener {
            findNavController().navigate(R.id.action_userGardenFragment_to_plantsSearchFragment)
        }
    }

    /**
     * Attaches the [UserPlantsAdapter] to the [RecyclerView].
     */
    private fun setupRecyclerView() {
        binding.userPlantsRecyclerView.adapter = adapter
    }

    /**
     * Observes the paginated user plant data from the [UserGardenViewModel].
     */
    private fun observeUserPlants() {
        viewModel.userPlants.observe(viewLifecycleOwner) { pagingData ->
            adapter.submitData(viewLifecycleOwner.lifecycle, pagingData)
        }
    }

    /**
     * Monitors the Paging [CombinedLoadStates] to manage the loading indicator,
     * error notifications, and the empty collection UI.
     */
    private fun handlePlantsLoadState() {
        adapter.addLoadStateListener { state ->
            when (state.refresh) {
                is LoadState.Loading -> {
                    errorSnackbar?.dismiss()
                    binding.loadingIndicator.visibility = View.VISIBLE
                }
                is LoadState.NotLoading -> binding.loadingIndicator.visibility = View.GONE
                is LoadState.Error -> showErrorNotification((state.refresh as LoadState.Error))
            }

            when (state.append) {
                is LoadState.Loading -> {
                    errorSnackbar?.dismiss()
                    binding.loadingIndicator.visibility = View.VISIBLE
                }
                is LoadState.NotLoading -> binding.loadingIndicator.visibility = View.GONE
                is LoadState.Error -> showErrorNotification((state.append as LoadState.Error))
            }

            checkEmptyGardenListing(state)
        }
    }

    /**
     * Displays a [Snackbar] with an appropriate error message when data loading fails.
     */
    private fun showErrorNotification(error: LoadState.Error) {
        when(error.error) {
            is UserGardenError.Internal -> {
                errorSnackbar = Snackbar.make(binding.root,
                    getString(R.string.error_message_internal_error), Snackbar.LENGTH_INDEFINITE)
                errorSnackbar?.show()
            }
        }
    }

    /**
     * Determines whether to display the "empty garden" UI based on the current load state and list count.
     */
    private fun checkEmptyGardenListing(loadStates: CombinedLoadStates) {
        val isRefreshDone = loadStates.refresh is LoadState.NotLoading
        val isListEmpty = adapter.itemCount == 0

        if(isRefreshDone && isListEmpty) {
            startEmptyGardenAnimation()
        } else {
            stopEmptyGardenAnimation()
        }
    }

    /**
     * Shows the empty collection placeholder and triggers the atmospheric pulse animation.
     */
    private fun startEmptyGardenAnimation() {
        binding.emptyGardenLayout.visibility = View.VISIBLE
        binding.emptyPotIcon.startAtmosphericPulse()
    }

    /**
     * Hides the empty collection placeholder and resets any active animations.
     */
    private fun stopEmptyGardenAnimation() {
        binding.emptyGardenLayout.visibility = View.GONE
        binding.emptyPotIcon.clearAnimation()
        binding.emptyPotIcon.scaleX = 1f
        binding.emptyPotIcon.scaleY = 1f
        binding.emptyPotIcon.translationY = 0f
    }

    /**
     * Navigates to the plant detail screen for the specified [plantId].
     */
    private fun navigateToPlantDetail(plantId: Int) {
        val bundle = bundleOf("plantId" to plantId)
        findNavController().navigate(R.id.action_userGardenFragment_to_userPlantDetailFragment, bundle)
    }

    /**
     * Navigates to the garden-specific search screen.
     */
    private fun navigateToUserPlantSearch() {
        findNavController().navigate(R.id.action_userGardenFragment_to_gardenPlantsSearchFragment)
    }

    /**
     * Opens the filter selection dialog, passing the current filter state.
     */
    private fun navigateToFiltersDialog() {
        val currentFilters = viewModel.filters.value ?: UserPlantsFiltersUiState()
        val bundle = bundleOf("filters" to currentFilters)

        findNavController().navigate(R.id.action_userGardenFragment_to_gardenPlantsFiltersDialog, bundle)
    }

    /**
     * Observes active filter criteria from the [UserGardenViewModel].
     */
    private fun observePlantsFilters() {
        viewModel.filters.observe(viewLifecycleOwner, ::setUiFilters)
    }

    /**
     * Updates the UI [ChipGroup] to reflect the currently active filters.
     */
    private fun setUiFilters(filters: UserPlantsFiltersUiState) {
        binding.cgFilters.removeAllViews()

        filters.categoryFilters.forEach { category ->
            addFilterChip(category.name.lowercase())
        }
        filters.seasonFilters.forEach { season ->
            addFilterChip(season.name.lowercase())
        }
        filters.sunCareFilters.forEach { sunCare ->
            addFilterChip(sunCare.name.lowercase())
        }
        filters.waterCareFilters.forEach { waterCare ->
            addFilterChip(waterCare.name.lowercase())
        }

        binding.cgFilters.visibility = if (filters.isEmpty) View.GONE else View.VISIBLE
    }

    /**
     * Dynamically creates and adds a filter [Chip] to the layout.
     */
    private fun addFilterChip(filterName: String) {
        val chip = Chip(requireContext()).apply {
            text = filterName
            setEnsureMinTouchTargetSize(true)
        }
        binding.cgFilters.addView(chip)
    }

    /**
     * Listens for filter updates returned from the [GardenPlantsFiltersDialog]
     * via the navigation [SavedStateHandle].
     */
    private fun observeReturnedDialogFilters() {
        findNavController().currentBackStackEntry
            ?.savedStateHandle
            ?.getLiveData<UserPlantsFiltersUiState>("filters")
            ?.observe(viewLifecycleOwner) { state ->
                viewModel.setFilters(state)

                findNavController().currentBackStackEntry
                    ?.savedStateHandle
                    ?.remove<UserPlantsFiltersUiState>("filters")
            }
    }
}
