package mx.edu.utez.focusflip.navigation

sealed class AppRoutes(
    val route: String,
    val number: Int,
    val shortTitle: String,
    val fullTitle: String
) {
    data object Onboarding : AppRoutes("onboarding", 1, "01. Onboarding", "01. Onboarding / Bienvenida")
    data object Login : AppRoutes("login", 2, "02. Login", "02. Login / Inicio de Sesión")
    data object Register : AppRoutes("register", 3, "03. Registro", "03. Registro (Sign Up)")
    data object Dashboard : AppRoutes("dashboard", 4, "04. Dashboard", "04. Dashboard Principal")
    data object OledFocus : AppRoutes("oled_focus", 5, "05. Enfoque OLED", "05. Modo Enfoque OLED (Sensor)")
    data object PauseAlert : AppRoutes("pause_alert", 6, "06. Pausa Alerta", "06. Alerta de Pausa (Giroscopio)")
    data object CreateSession : AppRoutes("create_session", 7, "07. Crear (Create)", "07. Resumen y Guardado (CREATE)")
    data object History : AppRoutes("history", 8, "08. Bitácora (Read)", "08. Bitácora Cloud (READ)")
    data object EditSession : AppRoutes("edit_session", 9, "09. Editar (Update)", "09. Edición de Registro (UPDATE)")
    data object DeleteSession : AppRoutes("delete_session", 10, "10. Borrar (Delete)", "10. Confirmar Borrado (DELETE)")
    data object SensorSettings : AppRoutes("sensor_settings", 11, "11. Sensores", "11. Configuración Sensores")
    data object OracleProfile : AppRoutes("oracle_profile", 12, "12. Perfil Oracle", "12. Perfil & Conexión Oracle")

    companion object {
        val allScreens: List<AppRoutes> = listOf(
            Onboarding,
            Login,
            Register,
            Dashboard,
            OledFocus,
            PauseAlert,
            CreateSession,
            History,
            EditSession,
            DeleteSession,
            SensorSettings,
            OracleProfile
        )

        fun fromRoute(route: String?): AppRoutes {
            return allScreens.find { it.route == route } ?: Onboarding
        }
    }
}
