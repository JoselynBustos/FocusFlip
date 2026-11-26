package mx.edu.utez.focusflip.data

data class SensorData(
    val accelX: Float = 0f,
    val accelY: Float = 0f,
    val accelZ: Float = 9.8f,
    val gyroX: Float = 0f,
    val gyroY: Float = 0f,
    val gyroZ: Float = 0f,
    val proximityCm: Float = 5f,
    val isProximityNear: Boolean = false,
    val lux: Float = 180f,
    val isFaceDownHardware: Boolean = false,
    val isSimulatedFaceDown: Boolean = false,
    val hasGyroscope: Boolean = false,
    val hasProximity: Boolean = false,
    val hasLightSensor: Boolean = false
) {
    val isEffectiveFaceDown: Boolean
        get() = isFaceDownHardware || isSimulatedFaceDown

    val inclinationDegrees: Int
        get() = if (isEffectiveFaceDown) 180 else {
            val normalized = ((-accelZ + 9.8f) / 19.6f).coerceIn(0f, 1f)
            (normalized * 180f).toInt()
        }

    val lightEnvironmentLabel: String
        get() = when {
            lux <= 15f -> "Oscuro / Mesa (OLED Activo)"
            lux <= 150f -> "Luz Interior Moderada"
            else -> "Iluminación Óptima de Estudio"
        }
}
