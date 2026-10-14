package com.alon.plantpule.plantcare.device

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import app.cash.turbine.test
import com.alon.plantpulse.common.application.Result
import com.alon.plantpulse.plantcare.application.model.PlantCareError
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.LooperMode

/**
 * Unit tests for [LightSensorGatewayImpl] using Robolectric.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
@LooperMode(LooperMode.Mode.PAUSED)
class LightSensorGatewayImplTest {

    private val sensorManager: SensorManager = mockk(relaxed = true)
    private val lightSensor: Sensor = mockk()
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var gateway: LightSensorGatewayImpl

    @Before
    fun setUp() {
        gateway = LightSensorGatewayImpl(sensorManager, testDispatcher)
        every { lightSensor.type } returns Sensor.TYPE_LIGHT
    }

    @Test
    fun whenRequestingLightLuxFlow_WithNoSensor_ShouldEmitSensorNotExistError() = runTest {
        // Given
        every { sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) } returns null

        // When
        val result = gateway.getLightLuxFlow().first()

        // Then
        assertThat(result).isInstanceOf(Result.Failure::class.java)
        assertThat((result as Result.Failure).error).isEqualTo(PlantCareError.LightSensorNotExist)
    }

    @Test
    fun whenRequestingLightLuxFlow_WithFailedRegistration_ShouldEmitInternalError() = runTest {
        // Given
        every { sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) } returns lightSensor
        every { sensorManager.registerListener(any(), lightSensor, any()) } returns false

        // When
        val result = gateway.getLightLuxFlow().first()

        // Then
        assertThat(result).isInstanceOf(Result.Failure::class.java)
        assertThat((result as Result.Failure).error).isInstanceOf(PlantCareError.Internal::class.java)
    }

    @Test
    fun whenRequestingLightLuxFlow_AndDeviceHasSensor_AndRegistrationSucceeds_ShouldEmitLuxValues() = runTest {
        // Given
        val listenerSlot = slot<SensorEventListener>()
        every { sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) } returns lightSensor
        every { sensorManager.registerListener(capture(listenerSlot), lightSensor, any()) } returns true

        gateway.getLightLuxFlow().test {
            // When
            val sensorEvent = createSensorEvent(100f)
            listenerSlot.captured.onSensorChanged(sensorEvent)

            // Then
            val firstValue = awaitItem()
            assertThat(firstValue).isInstanceOf(Result.Success::class.java)
            assertThat((firstValue as Result.Success).data).isEqualTo(100f)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun whenFlowCancelled_ShouldUnregisterListener() = runTest {
        // Given
        val listenerSlot = slot<SensorEventListener>()
        every { sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT) } returns lightSensor
        every { sensorManager.registerListener(capture(listenerSlot), lightSensor, any()) } returns true

        // When
        gateway.getLightLuxFlow().test {
            cancelAndIgnoreRemainingEvents()
        }

        // Then
        verify { sensorManager.unregisterListener(listenerSlot.captured) }
    }

    /**
     * Helper to create a [SensorEvent] with a specific value.
     * Uses reflection because SensorEvent has no public constructor and its fields
     * are not mockable via MockK's 'every' block.
     */
    private fun createSensorEvent(value: Float): SensorEvent {
        val event = mockk<SensorEvent>()
        
        // Set 'sensor' field via reflection
        val sensorField = SensorEvent::class.java.getField("sensor")
        sensorField.isAccessible = true
        sensorField.set(event, lightSensor)

        // Set 'values' field via reflection
        val valuesField = SensorEvent::class.java.getField("values")
        valuesField.isAccessible = true
        valuesField.set(event, floatArrayOf(value))

        return event
    }
}
