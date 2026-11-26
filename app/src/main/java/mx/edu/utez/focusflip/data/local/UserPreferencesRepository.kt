package mx.edu.utez.focusflip.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "focusflip_auth_prefs_v2")

data class UserSessionPrefs(
    val matricula: String = "",
    val nombreCompleto: String = "",
    val correo: String = "",
    val isLoggedIn: Boolean = false,
    val dailyGoalMinutes: Int = 120,
    val gyroSensitivity: Float = 0.85f,
    val useProximitySensor: Boolean = true,
    val useLightSensor: Boolean = true,
    val soundAlertsEnabled: Boolean = true,
    val oracleBaseUrl: String = "http://192.168.1.100:5000/"
)

class UserPreferencesRepository(private val context: Context) {
    private object Keys {
        val MATRICULA = stringPreferencesKey("matricula")
        val NOMBRE_COMPLETO = stringPreferencesKey("nombre_completo")
        val CORREO = stringPreferencesKey("correo")
        val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
        val DAILY_GOAL_MINUTES = intPreferencesKey("daily_goal_minutes")
        val GYRO_SENSITIVITY = floatPreferencesKey("gyro_sensitivity")
        val USE_PROXIMITY = booleanPreferencesKey("use_proximity")
        val USE_LIGHT = booleanPreferencesKey("use_light")
        val SOUND_ALERTS = booleanPreferencesKey("sound_alerts")
        val ORACLE_BASE_URL = stringPreferencesKey("oracle_base_url")
    }

    val userPrefsFlow: Flow<UserSessionPrefs> = context.dataStore.data.map { prefs ->
        UserSessionPrefs(
            matricula = prefs[Keys.MATRICULA] ?: "",
            nombreCompleto = prefs[Keys.NOMBRE_COMPLETO] ?: "",
            correo = prefs[Keys.CORREO] ?: "",
            isLoggedIn = prefs[Keys.IS_LOGGED_IN] ?: false,
            dailyGoalMinutes = prefs[Keys.DAILY_GOAL_MINUTES] ?: 120,
            gyroSensitivity = prefs[Keys.GYRO_SENSITIVITY] ?: 0.85f,
            useProximitySensor = prefs[Keys.USE_PROXIMITY] ?: true,
            useLightSensor = prefs[Keys.USE_LIGHT] ?: true,
            soundAlertsEnabled = prefs[Keys.SOUND_ALERTS] ?: true,
            oracleBaseUrl = prefs[Keys.ORACLE_BASE_URL] ?: "http://192.168.1.100:5000/"
        )
    }

    suspend fun saveSession(matricula: String, nombreCompleto: String, correo: String) {
        context.dataStore.edit { prefs ->
            prefs[Keys.MATRICULA] = matricula
            prefs[Keys.NOMBRE_COMPLETO] = nombreCompleto
            prefs[Keys.CORREO] = correo
            prefs[Keys.IS_LOGGED_IN] = true
        }
    }

    suspend fun updateDailyGoal(minutes: Int) {
        context.dataStore.edit { prefs ->
            prefs[Keys.DAILY_GOAL_MINUTES] = minutes.coerceIn(15, 720)
        }
    }

    suspend fun updateSensorSettings(
        gyroSensitivity: Float,
        useProximity: Boolean,
        useLight: Boolean,
        soundAlerts: Boolean
    ) {
        context.dataStore.edit { prefs ->
            prefs[Keys.GYRO_SENSITIVITY] = gyroSensitivity
            prefs[Keys.USE_PROXIMITY] = useProximity
            prefs[Keys.USE_LIGHT] = useLight
            prefs[Keys.SOUND_ALERTS] = soundAlerts
        }
    }

    suspend fun updateOracleBaseUrl(url: String) {
        val cleanUrl = if (url.trim().endsWith("/")) url.trim() else "${url.trim()}/"
        context.dataStore.edit { prefs ->
            prefs[Keys.ORACLE_BASE_URL] = cleanUrl
        }
    }

    suspend fun clearSession() {
        context.dataStore.edit { prefs ->
            prefs[Keys.IS_LOGGED_IN] = false
        }
    }
}
