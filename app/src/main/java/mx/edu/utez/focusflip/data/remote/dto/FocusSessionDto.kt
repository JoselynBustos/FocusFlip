package mx.edu.utez.focusflip.data.remote.dto

import com.google.gson.annotations.SerializedName

data class FocusSessionDto(
    val id: Int,
    @SerializedName("oci_code") val ociCode: String,
    @SerializedName("user_matricula") val userMatricula: String,
    val asignatura: String,
    @SerializedName("duracion_minutos") val duracionMinutos: Int,
    val notas: String,
    val fecha: String,
    @SerializedName("promedio_lux") val promedioLux: Float = 10f,
    @SerializedName("sensor_modo") val sensorModo: String = "Giroscopio 180° + Luz OLED"
)

data class CreateSessionRequest(
    @SerializedName("user_matricula") val userMatricula: String,
    val asignatura: String,
    @SerializedName("duracion_minutos") val duracionMinutos: Int,
    val notas: String,
    val fecha: String,
    @SerializedName("promedio_lux") val promedioLux: Float,
    @SerializedName("sensor_modo") val sensorModo: String
)

data class UpdateSessionRequest(
    val asignatura: String,
    @SerializedName("duracion_minutos") val duracionMinutos: Int,
    val notas: String
)

data class UpdateDailyGoalRequest(
    @SerializedName("user_matricula") val userMatricula: String,
    @SerializedName("daily_goal_minutes") val dailyGoalMinutes: Int
)
