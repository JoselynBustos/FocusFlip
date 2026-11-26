package mx.edu.utez.focusflip.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val matricula: String,
    val nombreCompleto: String,
    val correo: String,
    @ColumnInfo(name = "password_hash")
    val passwordHash: String,
    @ColumnInfo(name = "daily_goal_minutes")
    val dailyGoalMinutes: Int = 120,
    @ColumnInfo(name = "created_at")
    val createdAt: String
)
