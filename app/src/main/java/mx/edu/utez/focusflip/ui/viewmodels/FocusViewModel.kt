package mx.edu.utez.focusflip.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mx.edu.utez.focusflip.data.local.UserPreferencesRepository
import mx.edu.utez.focusflip.data.local.UserSessionPrefs
import mx.edu.utez.focusflip.data.local.entity.FocusSessionEntity
import mx.edu.utez.focusflip.data.remote.RetrofitClient
import mx.edu.utez.focusflip.data.repository.FocusRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

data class FocusUiState(
    val selectedSubject: String = "Desarrollo de Apps Móviles",
    val subjects: List<String> = listOf(
        "Desarrollo de Apps Móviles",
        "Estructura de Datos",
        "Ética Profesional",
        "Inglés IV",
        "Base de Datos en la Nube"
    ),
    val elapsedSeconds: Int = 0, // Inicia en 00:00 para medir el tiempo real de enfoque
    val isTimerRunning: Boolean = false,
    val lastMeasuredMinutes: Int = 25,
    val activeSessionToEdit: FocusSessionEntity? = null,
    val activeSessionToDelete: FocusSessionEntity? = null,
    val cloudStatusMessage: String = "Base de datos local Room + API REST lista",
    val isSyncing: Boolean = false
) {
    val formattedTimer: String
        get() {
            val mins = elapsedSeconds / 60
            val secs = elapsedSeconds % 60
            return "%02d:%02d".format(mins, secs)
        }

    val formattedPausedSummary: String
        get() {
            val mins = elapsedSeconds / 60
            val secs = elapsedSeconds % 60
            return "${mins}m ${secs}s"
        }

    val measuredMinutesFromTimer: Int
        get() = ((elapsedSeconds + 59) / 60).coerceAtLeast(1)
}

class FocusViewModel(
    private val focusRepository: FocusRepository,
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(FocusUiState())
    val uiState: StateFlow<FocusUiState> = _uiState.asStateFlow()

    val sessions: StateFlow<List<FocusSessionEntity>> = focusRepository.observeAllSessions()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userPrefs: StateFlow<UserSessionPrefs> = preferencesRepository.userPrefsFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserSessionPrefs())

    // Suma exacta de los minutos de todas las sesiones en la Bitácora
    val totalMinutesFlow: StateFlow<Int> = sessions
        .combine(_uiState) { list, _ ->
            list.sumOf { it.duracionMinutos }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 105)

    val todayMinutesFlow: StateFlow<Int> = sessions
        .combine(_uiState) { list, _ ->
            list.filter { it.fecha.contains("Hoy", ignoreCase = true) }
                .sumOf { it.duracionMinutos }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 25)

    init {
        viewModelScope.launch {
            focusRepository.ensureSampleSessionsSeeded()
            preferencesRepository.userPrefsFlow.collect { prefs ->
                RetrofitClient.updateBaseUrl(prefs.oracleBaseUrl)
            }
        }
    }

    fun selectSubject(subject: String) {
        _uiState.update { it.copy(selectedSubject = subject) }
    }

    fun startFocusTimer(resetIfZero: Boolean = false) {
        _uiState.update {
            val startSecs = if (resetIfZero && it.elapsedSeconds <= 0) 0 else it.elapsedSeconds
            it.copy(isTimerRunning = true, elapsedSeconds = startSecs)
        }
    }

    fun resetAndStartTimer() {
        _uiState.update {
            it.copy(elapsedSeconds = 0, isTimerRunning = true)
        }
    }

    fun addDemoMinutes(minutes: Int = 5) {
        _uiState.update {
            it.copy(elapsedSeconds = (it.elapsedSeconds + minutes * 60).coerceAtLeast(0))
        }
    }

    fun tickOneSecond() {
        _uiState.update {
            if (it.isTimerRunning) {
                it.copy(elapsedSeconds = it.elapsedSeconds + 1)
            } else {
                it
            }
        }
    }

    fun pauseFocusTimer() {
        _uiState.update {
            it.copy(
                isTimerRunning = false,
                lastMeasuredMinutes = ((it.elapsedSeconds + 59) / 60).coerceAtLeast(1)
            )
        }
    }

    fun finishFocusTimerAndPrepareCreate() {
        _uiState.update {
            val mins = ((it.elapsedSeconds + 59) / 60).coerceAtLeast(1)
            it.copy(
                isTimerRunning = false,
                lastMeasuredMinutes = mins
            )
        }
    }

    // CREATE
    fun createSession(
        asignatura: String,
        duracionMinutos: Int,
        notas: String,
        currentLux: Float,
        sensorModo: String,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            val formatter = DateTimeFormatter.ofPattern("dd MMM yyyy", Locale.forLanguageTag("es-MX"))
            val todayStr = "Hoy • ${LocalDate.now().format(formatter)}"
            val matricula = userPrefs.value.matricula.ifBlank { "20264A001" }
            focusRepository.createSession(
                userMatricula = matricula,
                asignatura = asignatura,
                duracionMinutos = duracionMinutos,
                notas = notas,
                fecha = todayStr,
                promedioLux = currentLux,
                sensorModo = sensorModo
            )
            _uiState.update {
                it.copy(
                    cloudStatusMessage = "Sesión guardada (${duracionMinutos} min)",
                    elapsedSeconds = 0
                )
            }
            onComplete()
        }
    }

    // PREPARE UPDATE
    fun selectSessionForEdit(session: FocusSessionEntity) {
        _uiState.update { it.copy(activeSessionToEdit = session) }
    }

    // UPDATE SESSION
    fun updateSession(
        sessionId: Int,
        asignatura: String,
        duracionMinutos: Int,
        notas: String,
        onComplete: () -> Unit
    ) {
        viewModelScope.launch {
            focusRepository.updateSession(sessionId, asignatura, duracionMinutos, notas)
            _uiState.update {
                it.copy(
                    activeSessionToEdit = null,
                    cloudStatusMessage = "Registro actualizado correctamente"
                )
            }
            onComplete()
        }
    }

    // UPDATE DAILY GOAL (Editar meta del día)
    fun updateDailyGoal(goalMinutes: Int) {
        viewModelScope.launch {
            val matricula = userPrefs.value.matricula.ifBlank { "20264A001" }
            focusRepository.updateDailyGoal(matricula, goalMinutes)
            _uiState.update {
                it.copy(cloudStatusMessage = "Meta del día actualizada a $goalMinutes min")
            }
        }
    }

    // PREPARE DELETE
    fun selectSessionForDelete(session: FocusSessionEntity) {
        _uiState.update { it.copy(activeSessionToDelete = session) }
    }

    // DELETE
    fun deleteSelectedSession(onComplete: () -> Unit) {
        val target = _uiState.value.activeSessionToDelete ?: sessions.value.firstOrNull()
        viewModelScope.launch {
            if (target != null) {
                focusRepository.deleteSession(target.id)
            }
            _uiState.update {
                it.copy(
                    activeSessionToDelete = null,
                    cloudStatusMessage = "Sesión ${target?.ociCode ?: "#SES-01"} eliminada"
                )
            }
            onComplete()
        }
    }

    // SENSOR SETTINGS
    fun saveSensorSettings(
        gyroSensitivity: Float,
        useProximity: Boolean,
        useLight: Boolean,
        soundAlerts: Boolean
    ) {
        viewModelScope.launch {
            preferencesRepository.updateSensorSettings(
                gyroSensitivity = gyroSensitivity,
                useProximity = useProximity,
                useLight = useLight,
                soundAlerts = soundAlerts
            )
        }
    }

    // API REST URL & SYNC
    fun updateOracleUrlAndSync(newUrl: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isSyncing = true, cloudStatusMessage = "Conectando a servidor API REST...") }
            preferencesRepository.updateOracleBaseUrl(newUrl)
            RetrofitClient.updateBaseUrl(newUrl)
            focusRepository.refreshFromOracleCloud()
                .onSuccess { count ->
                    _uiState.update {
                        it.copy(
                            isSyncing = false,
                            cloudStatusMessage = "API REST Online: $count sesiones sincronizadas"
                        )
                    }
                }
                .onFailure {
                    _uiState.update {
                        it.copy(
                            isSyncing = false,
                            cloudStatusMessage = "Respaldo local Room activo (${sessions.value.size} sesiones)"
                        )
                    }
                }
        }
    }
}
