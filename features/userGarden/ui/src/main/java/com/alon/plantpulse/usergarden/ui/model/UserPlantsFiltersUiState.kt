package com.alon.plantpulse.usergarden.ui.model

import com.alon.plantpulse.usergarden.domain.BloomingSeason
import com.alon.plantpulse.usergarden.domain.PlantCategory
import com.alon.plantpulse.usergarden.domain.PlantSunCare
import com.alon.plantpulse.usergarden.domain.PlantWaterCare
import java.io.Serializable

data class UserPlantsFiltersUiState(val categoryFilters: Set<PlantCategory> = emptySet(),
                                    val seasonFilters: Set<BloomingSeason> = emptySet(),
                                    val sunCareFilters: Set<PlantSunCare> = emptySet(),
                                    val waterCareFilters: Set<PlantWaterCare> = emptySet()) :
    Serializable {
    val isEmpty: Boolean
        get() = categoryFilters.isEmpty() &&
                seasonFilters.isEmpty() &&
                sunCareFilters.isEmpty() &&
                waterCareFilters.isEmpty()
}