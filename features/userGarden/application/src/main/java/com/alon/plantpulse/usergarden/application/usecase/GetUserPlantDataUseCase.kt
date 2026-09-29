package com.alon.plantpulse.usergarden.application.usecase

import com.alon.plantpulse.usergarden.application.interfaces.PlantRepository
import com.alon.plantpulse.usergarden.application.model.Result
import com.alon.plantpulse.usergarden.application.model.UserGardenError
import com.alon.plantpulse.usergarden.application.model.UserPlantDetailDto
import com.alon.plantpulse.usergarden.application.model.toUserPlantDetailDto
import javax.inject.Inject

class GetUserPlantDataUseCase @Inject constructor(private val plantRepo: PlantRepository) {

    suspend operator fun invoke(id: Int): Result<UserPlantDetailDto, UserGardenError> {
        return when(val res = plantRepo.getUserPlant(id)) {
            is Result.Success -> Result.Success(res.data.toUserPlantDetailDto())
            is Result.Failure -> Result.Failure(res.error)
        }
    }
}