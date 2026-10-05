package com.alon.plantpulse.usergarden.data.impl

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.alon.plantpulse.plantcare.api.PlantLight
import com.alon.plantpulse.usergarden.data.local.PlantDao
import com.alon.plantpulse.usergarden.data.util.createPlant
import com.alon.plantpulse.usergarden.domain.PlantSunCare
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.LooperMode

/**
 * Unit tests for [PlantCareDataProviderImpl].
 */
@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
class PlantCareDataProviderImplTest {

    private val plantDao: PlantDao = mockk()
    private lateinit var dataProvider: PlantCareDataProviderImpl

    @Before
    fun setUp() {
        dataProvider = PlantCareDataProviderImpl(plantDao)
    }

    @Test
    fun whenProvidingPlantCareData_WithValidId_ShouldReturnCorrectPlantCareData() = runTest {
        // Given
        val plantId = 1
        val plantName = "Aloe Vera"
        val plant = createPlant(plantId, plantName, "Aloe barbadensis", "url").copy(
            sunCare = PlantSunCare.FULL
        )
        coEvery { plantDao.getById(plantId) } returns plant

        // When
        val result = dataProvider.providePlantCareData(plantId)

        // Then
        assertThat(result.id).isEqualTo(plantId)
        assertThat(result.commonName).isEqualTo(plantName)
        assertThat(result.light).isEqualTo(PlantLight.FULL)
    }

    @Test(expected = RuntimeException::class)
    fun whenProvidingPlantCareData_AndDaoThrowsException_ShouldPropagateException() = runTest {
        // Given
        val plantId = 5
        val exception = RuntimeException("Database error")
        coEvery { plantDao.getById(plantId) } throws exception

        // When/Then
        dataProvider.providePlantCareData(plantId)
    }
}
