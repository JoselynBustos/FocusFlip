package mx.edu.utez.focusflip.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mx.edu.utez.focusflip.data.local.UserSessionPrefs
import mx.edu.utez.focusflip.data.repository.AuthRepository

data class AuthUiState(
    val nombreCompleto: String = "",
    val matricula: String = "",
    val correo: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

sealed interface AuthEvent {
    data object NavigateToDashboard : AuthEvent
    data object NavigateToLogin : AuthEvent
}

class AuthViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    private val _events = MutableSharedFlow<AuthEvent>()
    val events: SharedFlow<AuthEvent> = _events.asSharedFlow()

    val sessionState: StateFlow<UserSessionPrefs> = authRepository.sessionFlow
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), UserSessionPrefs())

    fun onNombreChange(value: String) {
        _uiState.update { it.copy(nombreCompleto = value, errorMessage = null) }
    }

    fun onMatriculaChange(value: String) {
        _uiState.update { it.copy(matricula = value, errorMessage = null) }
    }

    fun onCorreoChange(value: String) {
        _uiState.update { it.copy(correo = value, errorMessage = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, errorMessage = null) }
    }

    fun clearMessages() {
        _uiState.update { it.copy(errorMessage = null) }
    }

    fun login() {
        val state = _uiState.value
        val identifier = state.correo.trim()
        val pass = state.password

        if (identifier.isBlank() || pass.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Ingresa tu correo o matrícula y tu contraseña.") }
            return
        }
        if (pass.length < 6) {
            _uiState.update { it.copy(errorMessage = "La contraseña debe tener al menos 6 caracteres.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            authRepository.login(identifier, pass)
                .onSuccess {
                    _uiState.update { it.copy(isLoading = false, password = "", errorMessage = null) }
                    _events.emit(AuthEvent.NavigateToDashboard)
                }
                .onFailure { error ->
                    _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
                }
        }
    }

    fun register() {
        val state = _uiState.value
        val nombre = state.nombreCompleto.trim()
        val matricula = state.matricula.trim()
        val correo = state.correo.trim()
        val pass = state.password

        if (nombre.isBlank() || matricula.isBlank() || correo.isBlank() || pass.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Todos los campos son obligatorios.") }
            return
        }
        if (matricula.length < 5) {
            _uiState.update { it.copy(errorMessage = "Ingresa una matrícula válida (mínimo 5 caracteres).") }
            return
        }
        if (!correo.contains("@") || !correo.contains(".")) {
            _uiState.update { it.copy(errorMessage = "Ingresa un correo electrónico válido (ej. alumno@utez.edu.mx).") }
            return
        }
        if (pass.length < 6) {
            _uiState.update { it.copy(errorMessage = "Por seguridad, la contraseña debe tener mínimo 6 caracteres.") }
            return
        }

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            authRepository.register(
                nombreCompleto = nombre,
                matricula = matricula,
                correo = correo,
                password = pass
            ).onSuccess {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        password = "",
                        errorMessage = null
                    )
                }
                _events.emit(AuthEvent.NavigateToDashboard)
            }.onFailure { error ->
                _uiState.update { it.copy(isLoading = false, errorMessage = error.message) }
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
            _uiState.value = AuthUiState()
            _events.emit(AuthEvent.NavigateToLogin)
        }
    }
}
