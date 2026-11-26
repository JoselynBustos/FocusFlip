package mx.edu.utez.focusflip.data.repository

import kotlinx.coroutines.flow.Flow
import mx.edu.utez.focusflip.data.local.UserPreferencesRepository
import mx.edu.utez.focusflip.data.local.UserSessionPrefs
import mx.edu.utez.focusflip.data.local.dao.UserDao
import mx.edu.utez.focusflip.data.local.entity.UserEntity
import mx.edu.utez.focusflip.data.remote.RetrofitClient
import mx.edu.utez.focusflip.data.remote.dto.LoginRequest
import mx.edu.utez.focusflip.data.remote.dto.RegisterRequest
import java.security.MessageDigest
import java.time.LocalDate

class AuthRepository(
    private val userDao: UserDao,
    private val preferencesRepository: UserPreferencesRepository
) {
    val sessionFlow: Flow<UserSessionPrefs> = preferencesRepository.userPrefsFlow

    suspend fun login(correoOrMatricula: String, password: String): Result<UserSessionPrefs> {
        val cleanInput = correoOrMatricula.trim()
        val passwordHash = hashPassword(password)

        return try {
            val response = RetrofitClient.apiService.login(LoginRequest(cleanInput, password))
            if (response.isSuccessful && response.body()?.success == true && response.body()?.user != null) {
                val user = response.body()!!.user!!
                userDao.insertUser(
                    UserEntity(
                        matricula = user.matricula,
                        nombreCompleto = user.nombreCompleto,
                        correo = user.correo,
                        passwordHash = passwordHash,
                        dailyGoalMinutes = user.dailyGoalMinutes,
                        createdAt = user.createdAt ?: LocalDate.now().toString()
                    )
                )
                preferencesRepository.saveSession(user.matricula, user.nombreCompleto, user.correo)
                Result.success(
                    UserSessionPrefs(
                        matricula = user.matricula,
                        nombreCompleto = user.nombreCompleto,
                        correo = user.correo,
                        isLoggedIn = true,
                        dailyGoalMinutes = user.dailyGoalMinutes
                    )
                )
            } else {
                val apiErrorMsg = response.body()?.message ?: "Credenciales inválidas"
                // Si el servidor respondió 401/404 explícitamente, verificar también en Room por si se registró offline
                validateLocalCredentials(cleanInput, passwordHash, apiErrorMsg)
            }
        } catch (_: Exception) {
            // Respaldo Offline: validar estrictamente contra usuarios registrados en Room
            validateLocalCredentials(cleanInput, passwordHash)
        }
    }

    private suspend fun validateLocalCredentials(
        cleanInput: String,
        passwordHash: String,
        customNotFoundMessage: String? = null
    ): Result<UserSessionPrefs> {
        val localUser = userDao.getUserByIdentifier(cleanInput)
            ?: return Result.failure(
                Exception(customNotFoundMessage ?: "Usuario no registrado. Crea una cuenta primero.")
            )

        if (localUser.passwordHash != passwordHash) {
            return Result.failure(Exception("Contraseña incorrecta. Verifica tus datos."))
        }

        preferencesRepository.saveSession(localUser.matricula, localUser.nombreCompleto, localUser.correo)
        return Result.success(
            UserSessionPrefs(
                matricula = localUser.matricula,
                nombreCompleto = localUser.nombreCompleto,
                correo = localUser.correo,
                isLoggedIn = true,
                dailyGoalMinutes = localUser.dailyGoalMinutes
            )
        )
    }

    suspend fun register(
        nombreCompleto: String,
        matricula: String,
        correo: String,
        password: String
    ): Result<UserSessionPrefs> {
        val cleanMatricula = matricula.trim().uppercase()
        val cleanNombre = nombreCompleto.trim()
        val cleanCorreo = correo.trim().lowercase()
        val now = LocalDate.now().toString()
        val passwordHash = hashPassword(password)

        // Validar duplicados en Room antes de registrar
        if (userDao.getUserByIdentifier(cleanCorreo) != null) {
            return Result.failure(Exception("Este correo electrónico ya está registrado."))
        }
        if (userDao.getUserByIdentifier(cleanMatricula) != null) {
            return Result.failure(Exception("Esta matrícula ya está registrada."))
        }

        return try {
            val response = RetrofitClient.apiService.register(
                RegisterRequest(cleanMatricula, cleanNombre, cleanCorreo, password)
            )
            if (response.isSuccessful && response.body()?.success == true) {
                userDao.insertUser(
                    UserEntity(cleanMatricula, cleanNombre, cleanCorreo, passwordHash, 120, now)
                )
                preferencesRepository.saveSession(cleanMatricula, cleanNombre, cleanCorreo)
                Result.success(UserSessionPrefs(cleanMatricula, cleanNombre, cleanCorreo, true))
            } else {
                Result.failure(Exception(response.body()?.message ?: "El usuario ya existe en el servidor"))
            }
        } catch (_: Exception) {
            // Registro local en Room cuando no hay servidor disponible
            userDao.insertUser(
                UserEntity(cleanMatricula, cleanNombre, cleanCorreo, passwordHash, 120, now)
            )
            preferencesRepository.saveSession(cleanMatricula, cleanNombre, cleanCorreo)
            Result.success(UserSessionPrefs(cleanMatricula, cleanNombre, cleanCorreo, true))
        }
    }

    suspend fun logout() {
        preferencesRepository.clearSession()
    }

    private fun hashPassword(raw: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(raw.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}
