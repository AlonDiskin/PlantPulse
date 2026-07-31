package com.alon.plantpulse.usergarden.application.usecase

import com.alon.plantpulse.usergarden.application.interfaces.PlantRepository
import com.alon.plantpulse.usergarden.application.model.Result
import com.alon.plantpulse.usergarden.application.model.UserGardenError
import javax.inject.Inject

/**
 * Encapsulates the logic for adding a new plant to the user's personal garden collection.
 *
 * This use case coordinates the interaction between the UI layer and the repository to
 * ensure that plants are correctly persisted in the user's garden.
 *
 * @property repository The [PlantRepository] used to persist the plant data.
 */
class AddUserPlantUseCase @Inject constructor(private val repository: PlantRepository) {

    /**
     * Executes the addition of a plant to the user's garden.
     *
     * @param id The unique identifier of the plant to be added.
     * @return A [Result] indicating the outcome of the operation: [Result.Success] if the plant 
     * was added, or [Result.Failure] containing a [UserGardenError] if the operation failed.
     */
    suspend operator fun invoke(id: Int): Result<Unit, UserGardenError> {
        return repository.addUserPlant(id)
    }
}
