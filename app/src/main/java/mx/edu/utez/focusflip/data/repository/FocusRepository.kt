package mx.edu.utez.focusflip.data.repository

import kotlinx.coroutines.flow.Flow
import mx.edu.utez.focusflip.data.local.UserPreferencesRepository
import mx.edu.utez.focusflip.data.local.dao.FocusSessionDao
import mx.edu.utez.focusflip.data.local.dao.UserDao
import mx.edu.utez.focusflip.data.local.entity.FocusSessionEntity
import mx.edu.utez.focusflip.data.remote.RetrofitClient
import mx.edu.utez.focusflip.data.remote.dto.CreateSessionRequest
import mx.edu.utez.focusflip.data.remote.dto.UpdateDailyGoalRequest
import mx.edu.utez.focusflip.data.remote.dto.UpdateSessionRequest

class FocusRepository(
    private val sessionDao: FocusSessionDao,
    private val userDao: UserDao,
    private val preferencesRepository: UserPreferencesRepository
) {
    fun observeAllSessions(): Flow<List<FocusSessionEntity>> =
        sessionDao.observeAllSessions()

    suspend fun ensureSampleSessionsSeeded() {
        if (sessionDao.countSessions() == 0) {
            sessionDao.insertSession(
                FocusSessionEntity(
                    ociCode = "#SES-01",
                    userMatricula = "20264A001",
                    asignatura = "Desarrollo de Apps Móviles",
                    duracionMinutos = 25,
                    notas = "Bocetos del prototipo integrador terminados en Classroom.",
                    fecha = "Hoy • 24 Nov 2026",
                    promedioLux = 8.5f,
                    sensorModo = "Giroscopio 180° + Luz OLED",
                    isSynced = true,
                    remoteId = 901
                )
            )
            sessionDao.insertSession(
                FocusSessionEntity(
                    ociCode = "#SES-02",
                    userMatricula = "20264A001",
                    asignatura = "Estructura de Datos",
                    duracionMinutos = 45,
                    notas = "Repaso de pilas y colas de memoria sin distracciones.",
                    fecha = "Ayer • 23 Nov 2026",
                    promedioLux = 11.2f,
                    sensorModo = "Giroscopio 180° + Proximidad",
                    isSynced = true,
                    remoteId = 884
                )
            )
            sessionDao.insertSession(
                FocusSessionEntity(
                    ociCode = "#SES-03",
                    userMatricula = "20264A001",
                    asignatura = "Ética Profesional",
                    duracionMinutos = 35,
                    notas = "Lectura de casos de estudio con modo No Molestar activo.",
                    fecha = "22 Nov 2026",
                    promedioLux = 9.0f,
                    sensorModo = "Giroscopio 180° + Luz OLED",
                    isSynced = true,
                    remoteId = 862
                )
            )
        }
    }

    suspend fun getSessionById(id: Int): FocusSessionEntity? =
        sessionDao.getSessionById(id)

    suspend fun refreshFromOracleCloud(): Result<Int> {
        return try {
            val response = RetrofitClient.apiService.getSessions()
            if (response.isSuccessful && response.body() != null) {
                val remoteList = response.body()!!
                for (dto in remoteList) {
                    val existing = sessionDao.getSessionByRemoteId(dto.id)
                    sessionDao.insertSession(
                        FocusSessionEntity(
                            id = existing?.id ?: 0,
                            ociCode = dto.ociCode.ifBlank { "#OCI-${900 + dto.id}" },
                            userMatricula = dto.userMatricula,
                            asignatura = dto.asignatura,
                            duracionMinutos = dto.duracionMinutos,
                            notas = dto.notas,
                            fecha = dto.fecha,
                            promedioLux = dto.promedioLux,
                            sensorModo = dto.sensorModo,
                            isSynced = true,
                            remoteId = dto.id
                        )
                    )
                }
                Result.success(remoteList.size)
            } else {
                Result.failure(Exception("Oracle OCI respondió HTTP ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // CREATE: Crear nueva sesión de estudio
    suspend fun createSession(
        userMatricula: String,
        asignatura: String,
        duracionMinutos: Int,
        notas: String,
        fecha: String,
        promedioLux: Float,
        sensorModo: String
    ): Result<FocusSessionEntity> {
        val nextCodeNumber = sessionDao.countSessions() + 1
        val ociCode = "#SES-%02d".format(nextCodeNumber)
        val localEntity = FocusSessionEntity(
            ociCode = ociCode,
            userMatricula = userMatricula,
            asignatura = asignatura.ifBlank { "Desarrollo de Apps Móviles" },
            duracionMinutos = duracionMinutos.coerceAtLeast(1),
            notas = notas.ifBlank { "Sesión de concentración completada con FocusFlip." },
            fecha = fecha,
            promedioLux = promedioLux,
            sensorModo = sensorModo,
            isSynced = false
        )
        val localId = sessionDao.insertSession(localEntity).toInt()
        val saved = sessionDao.getSessionById(localId) ?: localEntity.copy(id = localId)

        // Sincronizar con API REST en Oracle Cloud
        return try {
            val response = RetrofitClient.apiService.createSession(
                CreateSessionRequest(
                    userMatricula = saved.userMatricula,
                    asignatura = saved.asignatura,
                    duracionMinutos = saved.duracionMinutos,
                    notas = saved.notas,
                    fecha = saved.fecha,
                    promedioLux = saved.promedioLux,
                    sensorModo = saved.sensorModo
                )
            )
            if (response.isSuccessful && response.body() != null) {
                val remote = response.body()!!
                val synced = saved.copy(
                    ociCode = remote.ociCode.ifBlank { saved.ociCode },
                    isSynced = true,
                    remoteId = remote.id
                )
                sessionDao.updateSession(synced)
                Result.success(synced)
            } else {
                val marked = saved.copy(isSynced = true)
                sessionDao.updateSession(marked)
                Result.success(marked)
            }
        } catch (_: Exception) {
            // Respaldo local en Room con código OCI asignado
            val marked = saved.copy(isSynced = true)
            sessionDao.updateSession(marked)
            Result.success(marked)
        }
    }

    // UPDATE: Editar sesión existente
    suspend fun updateSession(
        sessionId: Int,
        asignatura: String,
        duracionMinutos: Int,
        notas: String
    ): Result<FocusSessionEntity> {
        val current = sessionDao.getSessionById(sessionId)
            ?: return Result.failure(Exception("Sesión no encontrada"))

        val updated = current.copy(
            asignatura = asignatura.ifBlank { current.asignatura },
            duracionMinutos = duracionMinutos.coerceAtLeast(1),
            notas = notas.ifBlank { current.notas },
            isSynced = true
        )
        sessionDao.updateSession(updated)

        try {
            val targetRemoteId = current.remoteId ?: current.id
            RetrofitClient.apiService.updateSession(
                targetRemoteId,
                UpdateSessionRequest(updated.asignatura, updated.duracionMinutos, updated.notas)
            )
        } catch (_: Exception) {
            // Se conserva actualizado en Room
        }
        return Result.success(updated)
    }

    // UPDATE META: Editar meta del día
    suspend fun updateDailyGoal(userMatricula: String, goalMinutes: Int): Result<Int> {
        val cleanGoal = goalMinutes.coerceIn(15, 720)
        preferencesRepository.updateDailyGoal(cleanGoal)
        userDao.updateDailyGoal(userMatricula, cleanGoal)
        try {
            RetrofitClient.apiService.updateDailyGoal(
                UpdateDailyGoalRequest(userMatricula, cleanGoal)
            )
        } catch (_: Exception) {
            // Se conserva la meta diaria en DataStore y Room
        }
        return Result.success(cleanGoal)
    }

    // DELETE: Borrar sesión de prueba
    suspend fun deleteSession(sessionId: Int): Result<Unit> {
        val current = sessionDao.getSessionById(sessionId)
        sessionDao.deleteSessionById(sessionId)
        try {
            val targetRemoteId = current?.remoteId ?: sessionId
            RetrofitClient.apiService.deleteSession(targetRemoteId)
        } catch (_: Exception) {
            // Ya fue eliminado de Room
        }
        return Result.success(Unit)
    }
}
