package com.alon.plantpulse.usergarden.data.impl

import androidx.paging.PagingData
import com.alon.plantpulse.usergarden.data.local.LocalPlantsStore
import com.alon.plantpulse.usergarden.domain.PlantEntity
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import org.junit.Test

class PlantRepositoryImplTest {

    private val localStore: LocalPlantsStore = mockk()
    private val repository = PlantRepositoryImpl(localStore)

    @Test
    fun whenRepoSearchedForPlant_shouldDelegateToLocalPlantsStore() {
        // Given
        val query = "rose"
        val expectedResult = flowOf(PagingData.from(listOf<PlantEntity>()))
        every { localStore.search(query) } returns expectedResult

        // When
        val result = repository.search(query)

        // Then
        assertThat(result).isEqualTo(expectedResult)
        verify { localStore.search(query) }
    }
}
