package com.alon.plantpulse.usergarden.application.usecase

import com.alon.plantpulse.usergarden.application.interfaces.PlantRepository
import com.alon.plantpulse.usergarden.application.model.Result
import com.alon.plantpulse.usergarden.application.model.UserGardenError
import com.alon.plantpulse.usergarden.application.model.UserPlantDetailDto
import javax.inject.Inject

class DeleteUserPlantUseCase @Inject constructor(private val plantRepo: PlantRepository) {

    suspend operator fun invoke(id: Int): Result<Unit, UserGardenError> {
        return plantRepo.deleteUserPlant(id)
    }
}