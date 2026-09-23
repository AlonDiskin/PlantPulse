package com.alon.plantpulse.usergarden.ui.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.alon.plantpulse.plantsdetail.ui.R
import com.alon.plantpulse.plantsdetail.ui.databinding.DialogUserPlantsFilterBinding
import com.alon.plantpulse.usergarden.domain.BloomingSeason
import com.alon.plantpulse.usergarden.domain.PlantCategory
import com.alon.plantpulse.usergarden.domain.PlantSunCare
import com.alon.plantpulse.usergarden.domain.PlantWaterCare
import com.alon.plantpulse.usergarden.ui.viewmodel.GardenPlantsFiltersViewModel
import dagger.hilt.android.AndroidEntryPoint
import dagger.hilt.android.migration.OptionalInject

/**
 * A [DialogFragment] that provides a user interface for selecting various plant filters.
 *
 * This dialog allows users to filter their garden collection by category, blooming season,
 * sunlight requirements, and watering needs. It synchronizes its internal selection state
 * with [GardenPlantsFiltersViewModel] and passes the final selection back to the calling
 * fragment via the navigation component's [SavedStateHandle].
 */
@OptionalInject
@AndroidEntryPoint
class GardenPlantsFiltersDialog : DialogFragment() {

    private var _binding: DialogUserPlantsFilterBinding? = null
    private val binding get() = _binding!!
    private val viewModel: GardenPlantsFiltersViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogUserPlantsFilterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setCurrentFilters()
        setFiltersClickListeners()
        setupButtons()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    /**
     * Initializes the UI chips based on the current filter state in the ViewModel.
     */
    private fun setCurrentFilters() {
        val filters = viewModel.filters

        setCategoryFilters(filters.categoryFilters)
        setSeasonFilters(filters.seasonFilters)
        setSunCareFilters(filters.sunCareFilters)
        setWaterCareFilters(filters.waterCareFilters)
    }

    private fun setCategoryFilters(set: Set<PlantCategory>) {
        set.forEach { category ->
            when (category) {
                PlantCategory.VEGETABLE -> binding.cgCategory.check(R.id.filter_vegetable)
                PlantCategory.HERB -> binding.cgCategory.check(R.id.filter_herb)
                PlantCategory.FRUIT -> binding.cgCategory.check(R.id.filter_fruit)
                PlantCategory.FLOWER -> binding.cgCategory.check(R.id.filter_flower)
                PlantCategory.BERRY -> binding.cgCategory.check(R.id.filter_berry)
                PlantCategory.COVER_CROP -> binding.cgCategory.check(R.id.filter_cover_crop)
            }
        }
    }

    private fun setSeasonFilters(set: Set<BloomingSeason>) {
        set.forEach { season ->
            when (season) {
                BloomingSeason.PERENNIAL -> binding.cgSeason.check(R.id.filter_Perennial)
                BloomingSeason.COOL -> binding.cgSeason.check(R.id.filter_cool)
                BloomingSeason.WARM -> binding.cgSeason.check(R.id.filter_warm)
            }
        }
    }

    private fun setSunCareFilters(set: Set<PlantSunCare>) {
        set.forEach { sunCare ->
            when (sunCare) {
                PlantSunCare.FULL -> binding.cgSunCare.check(R.id.filter_full_sun)
                PlantSunCare.PARTIAL -> binding.cgSunCare.check(R.id.filter_partial_sun)
                PlantSunCare.SHADE -> binding.cgSunCare.check(R.id.filter_shade)
            }
        }
    }

    private fun setWaterCareFilters(set: Set<PlantWaterCare>) {
        set.forEach { waterCare ->
            when (waterCare) {
                PlantWaterCare.LOW -> binding.cgWaterCare.check(R.id.filter_low_water)
                PlantWaterCare.MODERATE -> binding.cgWaterCare.check(R.id.filter_moderate_water)
                PlantWaterCare.HIGH -> binding.cgWaterCare.check(R.id.filter_hige_water)
            }
        }
    }

    /**
     * Configures check state change listeners for all filter chip groups.
     */
    private fun setFiltersClickListeners() {
        setCategoryFiltersClickListeners()
        setSeasonFiltersClickListeners()
        setSunCareFiltersClickListeners()
        setWaterCareFiltersClickListeners()
    }

    private fun setCategoryFiltersClickListeners() {
        binding.cgCategory.setOnCheckedStateChangeListener { _, checkedIds ->
            val categories = mutableSetOf<PlantCategory>()

            for (id in checkedIds) {
                when (id) {
                    R.id.filter_vegetable -> categories.add(PlantCategory.VEGETABLE)
                    R.id.filter_herb -> categories.add(PlantCategory.HERB)
                    R.id.filter_fruit -> categories.add(PlantCategory.FRUIT)
                }
            }
            viewModel.setPlantCategoryFilters(categories)
        }
    }

    private fun setSeasonFiltersClickListeners() {
        binding.cgSeason.setOnCheckedStateChangeListener { _, checkedIds ->
            val seasons = mutableSetOf<BloomingSeason>()

            for (id in checkedIds) {
                when (id) {
                    R.id.filter_Perennial -> seasons.add(BloomingSeason.PERENNIAL)
                    R.id.filter_cool -> seasons.add(BloomingSeason.COOL)
                    R.id.filter_warm -> seasons.add(BloomingSeason.WARM)
                }
            }
            viewModel.setPlantSeasonFilters(seasons)
        }
    }

    private fun setSunCareFiltersClickListeners() {
        binding.cgSunCare.setOnCheckedStateChangeListener { _, checkedIds ->
            val sunCare = mutableSetOf<PlantSunCare>()

            for (id in checkedIds) {
                when (id) {
                    R.id.filter_full_sun -> sunCare.add(PlantSunCare.FULL)
                    R.id.filter_partial_sun -> sunCare.add(PlantSunCare.PARTIAL)
                    R.id.filter_shade -> sunCare.add(PlantSunCare.SHADE)
                }
            }
            viewModel.setPlantSunCareFilters(sunCare)
        }
    }

    private fun setWaterCareFiltersClickListeners() {
        binding.cgWaterCare.setOnCheckedStateChangeListener { _, checkedIds ->
            val waterCare = mutableSetOf<PlantWaterCare>()

            for (id in checkedIds) {
                when (id) {
                    R.id.filter_low_water -> waterCare.add(PlantWaterCare.LOW)
                    R.id.filter_moderate_water -> waterCare.add(PlantWaterCare.MODERATE)
                    R.id.filter_hige_water -> waterCare.add(PlantWaterCare.HIGH)
                }
            }
            viewModel.setPlantWaterCareFilters(waterCare)
        }
    }

    /**
     * Sets up click listeners for the action buttons (Apply and Reset).
     */
    private fun setupButtons() {
        binding.btnReset.setOnClickListener {
            clearUiFilters()
            viewModel.clearFilters()
        }

        binding.btnApply.setOnClickListener {
            val filters = viewModel.filters
            findNavController().previousBackStackEntry?.savedStateHandle?.set("filters", filters)
            findNavController().popBackStack()
        }
    }

    /**
     * Clears all checked states from the UI chip groups.
     */
    private fun clearUiFilters() {
        binding.cgCategory.clearCheck()
        binding.cgSeason.clearCheck()
        binding.cgSunCare.clearCheck()
        binding.cgWaterCare.clearCheck()
    }
}
