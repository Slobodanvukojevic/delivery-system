package com.delivery.client.utils

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import kotlin.math.sqrt

class ShakeDetector(
    context: Context,
    private val onShake: () -> Unit
) : SensorEventListener {

    private val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)

    private var lastShakeTime: Long = 0

    companion object {
        private const val SHAKE_THRESHOLD = 15.0f
        private const val MIN_TIME_BETWEEN_SHAKES = 1500L  // 1.5 sec
    }

    fun start() {
        accelerometer?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun stop() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        event ?: return

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]

        val acceleration = sqrt((x * x + y * y + z * z).toDouble()).toFloat()
        val delta = acceleration - SensorManager.GRAVITY_EARTH

        if (delta > SHAKE_THRESHOLD) {
            val now = System.currentTimeMillis()
            if (now - lastShakeTime > MIN_TIME_BETWEEN_SHAKES) {
                lastShakeTime = now
                onShake()
            }
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }
}