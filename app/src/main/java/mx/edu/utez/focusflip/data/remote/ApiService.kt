package mx.edu.utez.focusflip.data.remote

import mx.edu.utez.focusflip.data.remote.dto.AuthResponse
import mx.edu.utez.focusflip.data.remote.dto.CreateSessionRequest
import mx.edu.utez.focusflip.data.remote.dto.FocusSessionDto
import mx.edu.utez.focusflip.data.remote.dto.LoginRequest
import mx.edu.utez.focusflip.data.remote.dto.RegisterRequest
import mx.edu.utez.focusflip.data.remote.dto.UpdateDailyGoalRequest
import mx.edu.utez.focusflip.data.remote.dto.UpdateSessionRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @POST("api/auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @GET("api/sessions")
    suspend fun getSessions(): Response<List<FocusSessionDto>>

    @POST("api/sessions")
    suspend fun createSession(@Body request: CreateSessionRequest): Response<FocusSessionDto>

    @PUT("api/sessions/{id}")
    suspend fun updateSession(
        @Path("id") sessionId: Int,
        @Body request: UpdateSessionRequest
    ): Response<FocusSessionDto>

    @DELETE("api/sessions/{id}")
    suspend fun deleteSession(@Path("id") sessionId: Int): Response<Unit>

    @PUT("api/goals")
    suspend fun updateDailyGoal(@Body request: UpdateDailyGoalRequest): Response<Unit>
}
