package mx.edu.utez.focusflip.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitClient {
    const val DEFAULT_BASE_URL = "http://192.168.1.100:5000/"

    @Volatile
    private var currentBaseUrl: String = DEFAULT_BASE_URL

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .connectTimeout(5, TimeUnit.SECONDS)
        .readTimeout(8, TimeUnit.SECONDS)
        .writeTimeout(8, TimeUnit.SECONDS)
        .build()

    @Volatile
    private var cachedService: ApiService? = null

    val apiService: ApiService
        get() = cachedService ?: buildService(currentBaseUrl).also { cachedService = it }

    fun updateBaseUrl(newUrl: String) {
        val normalized = try {
            val trimmed = newUrl.trim()
            if (!trimmed.startsWith("http://") && !trimmed.startsWith("https://")) {
                DEFAULT_BASE_URL
            } else if (trimmed.endsWith("/")) {
                trimmed
            } else {
                "$trimmed/"
            }
        } catch (_: Exception) {
            DEFAULT_BASE_URL
        }
        if (normalized != currentBaseUrl || cachedService == null) {
            currentBaseUrl = normalized
            cachedService = buildService(normalized)
        }
    }

    private fun buildService(baseUrl: String): ApiService {
        return try {
            Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        } catch (_: Exception) {
            Retrofit.Builder()
                .baseUrl(DEFAULT_BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(ApiService::class.java)
        }
    }
}
