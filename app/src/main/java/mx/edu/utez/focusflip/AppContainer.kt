package mx.edu.utez.focusflip

import android.content.Context
import mx.edu.utez.focusflip.data.local.AppDatabase
import mx.edu.utez.focusflip.data.local.UserPreferencesRepository
import mx.edu.utez.focusflip.data.repository.AuthRepository
import mx.edu.utez.focusflip.data.repository.FocusRepository

class AppContainer(context: Context) {
    private val database = AppDatabase.getInstance(context)
    val userPreferencesRepository = UserPreferencesRepository(context)

    val authRepository = AuthRepository(
        userDao = database.userDao(),
        preferencesRepository = userPreferencesRepository
    )

    val focusRepository = FocusRepository(
        sessionDao = database.focusSessionDao(),
        userDao = database.userDao(),
        preferencesRepository = userPreferencesRepository
    )
}
