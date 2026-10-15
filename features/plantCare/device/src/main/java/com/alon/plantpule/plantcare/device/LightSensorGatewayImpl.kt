package com.alon.plantpule.plantcare.device

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import com.alon.plantpulse.common.application.Result
import com.alon.plantpulse.plantcare.application.interfaces.LightSensorGateway
import com.alon.plantpulse.plantcare.application.model.PlantCareError
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import javax.inject.Inject

/**
 * Implementation of [LightSensorGateway] that provides real-time light intensity data
 * using the Android Hardware Sensor API.
 *
 * This class wraps the [SensorManager] and converts sensor events into a reactive [Flow].
 * It handles cases where the light sensor might be missing and ensures proper resource
 * management by unregistering the sensor listener when the flow is no longer collected.
 *
 * @property sensorManager The Android system service used to access hardware sensors.
 * @property defaultDispatcher The CoroutineDispatcher on which the sensor flow will be executed.
 */
class LightSensorGatewayImpl @Inject constructor(
    private val sensorManager: SensorManager,
    private val defaultDispatcher: CoroutineDispatcher) : LightSensorGateway {

    override fun getLightLuxFlow(): Flow<Result<Float, PlantCareError>> {
        return callbackFlow {
            // Error Case 1: Device doesn't have a light sensor
            val lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)
            if (lightSensor == null) {
                trySend(Result.Failure(PlantCareError.LightSensorNotExist))
                close()
                return@callbackFlow
            }

            val listener = object : SensorEventListener {
                override fun onSensorChanged(event: SensorEvent) {
                    if (event.sensor.type == Sensor.TYPE_LIGHT) {
                        trySend(Result.Success(event.values[0]))
                    }
                }

                override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
                    // Optional: handle accuracy shifts if needed
                }
            }

            // Error Case 2: Sensor registration fails
            val isRegistered = sensorManager.registerListener(
                listener,
                lightSensor,
                SensorManager.SENSOR_DELAY_UI
            )

            if (!isRegistered) {
                trySend(Result.Failure(PlantCareError.Internal(Exception("Sensor registration failed"))))
                close()
                return@callbackFlow
            }

            // Automatically unregister listener when the flow collection is cancelled
            awaitClose {
                sensorManager.unregisterListener(listener)
            }
        }.flowOn(defaultDispatcher)
    }
}
