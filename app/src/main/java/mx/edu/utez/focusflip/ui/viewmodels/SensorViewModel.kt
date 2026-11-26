package mx.edu.utez.focusflip.ui.viewmodels

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import mx.edu.utez.focusflip.R
import mx.edu.utez.focusflip.data.SensorData

class SensorViewModel : ViewModel(), SensorEventListener {
    private var sensorManager: SensorManager? = null
    private var accelerometerSensor: Sensor? = null
    private var gyroscopeSensor: Sensor? = null
    private var proximitySensor: Sensor? = null
    private var lightSensor: Sensor? = null

    private var mediaPlayer: MediaPlayer? = null

    private val _sensorData = MutableStateFlow(SensorData())
    val sensorData: StateFlow<SensorData> = _sensorData.asStateFlow()

    // Parámetros configurables desde la Vista 11 (Ajustes de Sensores)
    private var zThreshold: Float = -6.8f
    private var useProximity: Boolean = true
    private var useLight: Boolean = true
    private var soundEnabled: Boolean = true

    fun startListening(context: Context) {
        if (sensorManager == null) {
            sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager
            accelerometerSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
            gyroscopeSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_GYROSCOPE)
            proximitySensor = sensorManager?.getDefaultSensor(Sensor.TYPE_PROXIMITY)
            lightSensor = sensorManager?.getDefaultSensor(Sensor.TYPE_LIGHT)

            _sensorData.update {
                it.copy(
                    hasGyroscope = gyroscopeSensor != null || accelerometerSensor != null,
                    hasProximity = proximitySensor != null,
                    hasLightSensor = lightSensor != null
                )
            }
        }

        accelerometerSensor?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        gyroscopeSensor?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        proximitySensor?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        lightSensor?.let {
            sensorManager?.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun stopListening() {
        sensorManager?.unregisterListener(this)
    }

    fun applySettings(
        sensitivity: Float,
        enableProximity: Boolean,
        enableLight: Boolean,
        enableSound: Boolean
    ) {
        // Entre mayor sensibilidad (0.3..1.0), más fácil detecta la inclinación boca abajo
        zThreshold = -9.0f + (sensitivity.coerceIn(0.3f, 1.0f) * 3.0f)
        useProximity = enableProximity
        useLight = enableLight
        soundEnabled = enableSound
        recalculateFaceDownState()
    }

    override fun onSensorChanged(event: SensorEvent?) {
        val currentEvent = event ?: return
        when (currentEvent.sensor.type) {
            Sensor.TYPE_ACCELEROMETER -> {
                val x = currentEvent.values.getOrElse(0) { 0f }
                val y = currentEvent.values.getOrElse(1) { 0f }
                val z = currentEvent.values.getOrElse(2) { 9.8f }
                _sensorData.update {
                    it.copy(accelX = x, accelY = y, accelZ = z)
                }
                recalculateFaceDownState()
            }

            Sensor.TYPE_GYROSCOPE -> {
                val gx = currentEvent.values.getOrElse(0) { 0f }
                val gy = currentEvent.values.getOrElse(1) { 0f }
                val gz = currentEvent.values.getOrElse(2) { 0f }
                _sensorData.update {
                    it.copy(gyroX = gx, gyroY = gy, gyroZ = gz)
                }
            }

            Sensor.TYPE_PROXIMITY -> {
                val cm = currentEvent.values.getOrElse(0) { 5f }
                val maxRange = proximitySensor?.maximumRange ?: 5f
                val isNear = cm < maxRange && cm <= 2.5f
                _sensorData.update {
                    it.copy(proximityCm = cm, isProximityNear = isNear)
                }
                recalculateFaceDownState()
            }

            Sensor.TYPE_LIGHT -> {
                val luxValue = currentEvent.values.getOrElse(0) { 180f }
                _sensorData.update {
                    it.copy(lux = luxValue)
                }
                recalculateFaceDownState()
            }
        }
    }

    private fun recalculateFaceDownState() {
        val current = _sensorData.value
        val tiltedFaceDown = current.accelZ <= zThreshold
        val coveredOnTable = (useProximity && current.isProximityNear) &&
                (!useLight || current.lux <= 25f)
        val isHardwareDown = tiltedFaceDown || coveredOnTable

        if (current.isFaceDownHardware != isHardwareDown) {
            _sensorData.update {
                it.copy(isFaceDownHardware = isHardwareDown)
            }
        }
    }

    fun setSimulatedFaceDown(simulated: Boolean, context: Context? = null) {
        _sensorData.update {
            it.copy(
                isSimulatedFaceDown = simulated,
                lux = if (simulated) 4.0f else if (it.lux < 10f) 185.0f else it.lux,
                isProximityNear = if (simulated) true else it.isProximityNear
            )
        }
        if (context != null && soundEnabled) {
            playSound(context, if (simulated) R.raw.noche else R.raw.manana)
        }
    }

    fun calibrateSensors() {
        _sensorData.update {
            it.copy(
                gyroX = 0f,
                gyroY = 0f,
                gyroZ = 0f
            )
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // Nada
    }

    fun playSound(context: Context, soundResId: Int) {
        if (!soundEnabled) return
        try {
            stopSound()
            mediaPlayer = MediaPlayer.create(context, soundResId)?.apply {
                setOnCompletionListener {
                    releasePlayer()
                }
                start()
            }
        } catch (_: Exception) {
            releasePlayer()
        }
    }

    fun stopSound() {
        try {
            mediaPlayer?.let {
                if (it.isPlaying) {
                    it.stop()
                }
                releasePlayer()
            }
        } catch (_: Exception) {
            releasePlayer()
        }
    }

    private fun releasePlayer() {
        try {
            mediaPlayer?.release()
        } catch (_: Exception) {
        }
        mediaPlayer = null
    }

    override fun onCleared() {
        super.onCleared()
        stopListening()
        releasePlayer()
    }
}
