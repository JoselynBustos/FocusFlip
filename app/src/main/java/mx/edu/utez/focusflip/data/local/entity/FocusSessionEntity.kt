package mx.edu.utez.focusflip.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "focus_sessions")
data class FocusSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "oci_code")
    val ociCode: String,
    @ColumnInfo(name = "user_matricula")
    val userMatricula: String,
    val asignatura: String,
    @ColumnInfo(name = "duracion_minutos")
    val duracionMinutos: Int,
    val notas: String,
    val fecha: String,
    @ColumnInfo(name = "promedio_lux")
    val promedioLux: Float = 12f,
    @ColumnInfo(name = "sensor_modo")
    val sensorModo: String = "Giroscopio 180° + Luz OLED",
    @ColumnInfo(name = "is_synced")
    val isSynced: Boolean = true,
    @ColumnInfo(name = "remote_id")
    val remoteId: Int? = null
)
