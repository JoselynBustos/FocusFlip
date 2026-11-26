package mx.edu.utez.focusflip.data.remote.dto

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val correo: String,
    val password: String
)

data class RegisterRequest(
    val matricula: String,
    @SerializedName("nombre_completo") val nombreCompleto: String,
    val correo: String,
    val password: String
)

data class UserDto(
    val matricula: String,
    @SerializedName("nombre_completo") val nombreCompleto: String,
    val correo: String,
    @SerializedName("daily_goal_minutes") val dailyGoalMinutes: Int = 120,
    @SerializedName("created_at") val createdAt: String? = null
)

data class AuthResponse(
    val success: Boolean,
    val message: String,
    val user: UserDto? = null
)
