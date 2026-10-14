package com.alon.plantpulse.plantcare.featuretest.lightmeter

import android.hardware.Sensor
import android.hardware.SensorEvent
import io.mockk.mockk

fun createSensorEvent(value: Float, lightSensor: Sensor): SensorEvent {
    val event = mockk<SensorEvent>()
    val sensorField = SensorEvent::class.java.getField("sensor")
    sensorField.isAccessible = true
    sensorField.set(event, lightSensor)

    val valuesField = SensorEvent::class.java.getField("values")
    valuesField.isAccessible = true
    valuesField.set(event, floatArrayOf(value))

    return event
}