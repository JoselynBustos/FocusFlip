package mx.edu.utez.focusflip.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import mx.edu.utez.focusflip.AppContainer
import mx.edu.utez.focusflip.ui.components.FocusBottomNavBar
import mx.edu.utez.focusflip.ui.screens.CreateSessionScreen
import mx.edu.utez.focusflip.ui.screens.DashboardScreen
import mx.edu.utez.focusflip.ui.screens.DeleteSessionScreen
import mx.edu.utez.focusflip.ui.screens.EditSessionScreen
import mx.edu.utez.focusflip.ui.screens.HistoryScreen
import mx.edu.utez.focusflip.ui.screens.LoginScreen
import mx.edu.utez.focusflip.ui.screens.OledFocusScreen
import mx.edu.utez.focusflip.ui.screens.OnboardingScreen
import mx.edu.utez.focusflip.ui.screens.OracleProfileScreen
import mx.edu.utez.focusflip.ui.screens.PauseAlertScreen
import mx.edu.utez.focusflip.ui.screens.RegisterScreen
import mx.edu.utez.focusflip.ui.screens.SensorSettingsScreen
import mx.edu.utez.focusflip.ui.theme.Slate950
import mx.edu.utez.focusflip.ui.viewmodels.AuthViewModel
import mx.edu.utez.focusflip.ui.viewmodels.FocusViewModel
import mx.edu.utez.focusflip.ui.viewmodels.SensorViewModel

@Composable
fun AppNavGraph(appContainer: AppContainer) {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route
    val currentScreen = AppRoutes.fromRoute(currentRoute)

    val authViewModel: AuthViewModel = viewModel(factory = simpleFactory {
        AuthViewModel(appContainer.authRepository)
    })
    val focusViewModel: FocusViewModel = viewModel(factory = simpleFactory {
        FocusViewModel(appContainer.focusRepository, appContainer.userPreferencesRepository)
    })
    val sensorViewModel: SensorViewModel = viewModel()

    val session by authViewModel.sessionState.collectAsStateWithLifecycle()

    fun navigateToScreen(target: AppRoutes) {
        val publicRoutes = listOf(AppRoutes.Onboarding, AppRoutes.Login, AppRoutes.Register)
        if (!session.isLoggedIn && target !in publicRoutes) {
            navController.navigate(AppRoutes.Login.route) {
                launchSingleTop = true
            }
            return
        }
        if (currentRoute != target.route) {
            navController.navigate(target.route) {
                launchSingleTop = true
            }
        }
    }

    // Mostrar la barra inferior únicamente cuando el usuario ya inició sesión y está dentro de la app
    val showBottomBar = session.isLoggedIn && currentScreen !in listOf(
        AppRoutes.Onboarding,
        AppRoutes.Login,
        AppRoutes.Register
    )

    Scaffold(
        containerColor = Slate950,
        bottomBar = {
            if (showBottomBar) {
                FocusBottomNavBar(
                    currentScreen = currentScreen,
                    onNavigate = { target -> navigateToScreen(target) }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = AppRoutes.Onboarding.route,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 01. Onboarding
            composable(AppRoutes.Onboarding.route) {
                OnboardingScreen(
                    onNavigateToLogin = {
                        authViewModel.clearMessages()
                        if (session.isLoggedIn) {
                            navController.navigate(AppRoutes.Dashboard.route) {
                                popUpTo(AppRoutes.Onboarding.route) { inclusive = true }
                            }
                        } else {
                            navController.navigate(AppRoutes.Login.route) {
                                popUpTo(AppRoutes.Onboarding.route) { inclusive = true }
                            }
                        }
                    }
                )
            }

            // 02. Login
            composable(AppRoutes.Login.route) {
                LoginScreen(
                    viewModel = authViewModel,
                    onNavigateToRegister = {
                        authViewModel.clearMessages()
                        navController.navigate(AppRoutes.Register.route)
                    },
                    onLoginSuccess = {
                        navController.navigate(AppRoutes.Dashboard.route) {
                            popUpTo(AppRoutes.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            // 03. Registro
            composable(AppRoutes.Register.route) {
                RegisterScreen(
                    viewModel = authViewModel,
                    onNavigateBack = {
                        authViewModel.clearMessages()
                        navController.popBackStack()
                    },
                    onRegisterSuccess = {
                        navController.navigate(AppRoutes.Dashboard.route) {
                            popUpTo(AppRoutes.Login.route) { inclusive = true }
                        }
                    }
                )
            }

            // 04. Dashboard Principal
            composable(AppRoutes.Dashboard.route) {
                DashboardScreen(
                    focusViewModel = focusViewModel,
                    sensorViewModel = sensorViewModel,
                    onNavigateToOledFocus = { navigateToScreen(AppRoutes.OledFocus) },
                    onNavigateToSensorSettings = { navigateToScreen(AppRoutes.SensorSettings) },
                    onNavigateToProfile = { navigateToScreen(AppRoutes.OracleProfile) }
                )
            }

            // 05. Modo Enfoque OLED (Sensor)
            composable(AppRoutes.OledFocus.route) {
                OledFocusScreen(
                    focusViewModel = focusViewModel,
                    sensorViewModel = sensorViewModel,
                    onNavigateToPauseAlert = { navigateToScreen(AppRoutes.PauseAlert) },
                    onNavigateToCreateSession = { navigateToScreen(AppRoutes.CreateSession) }
                )
            }

            // 06. Alerta de Pausa (Giroscopio)
            composable(AppRoutes.PauseAlert.route) {
                PauseAlertScreen(
                    focusViewModel = focusViewModel,
                    sensorViewModel = sensorViewModel,
                    onResumeFocus = { navigateToScreen(AppRoutes.OledFocus) },
                    onFinishSession = { navigateToScreen(AppRoutes.CreateSession) }
                )
            }

            // 07. Guardar Sesión (CREATE)
            composable(AppRoutes.CreateSession.route) {
                CreateSessionScreen(
                    focusViewModel = focusViewModel,
                    sensorViewModel = sensorViewModel,
                    onCancel = { navigateToScreen(AppRoutes.Dashboard) },
                    onSavedSuccess = { navigateToScreen(AppRoutes.History) }
                )
            }

            // 08. Bitácora de Estudio (READ)
            composable(AppRoutes.History.route) {
                HistoryScreen(
                    focusViewModel = focusViewModel,
                    onNavigateToCreate = { navigateToScreen(AppRoutes.CreateSession) },
                    onNavigateToEdit = { navigateToScreen(AppRoutes.EditSession) },
                    onNavigateToDelete = { navigateToScreen(AppRoutes.DeleteSession) }
                )
            }

            // 09. Editar Registro (UPDATE)
            composable(AppRoutes.EditSession.route) {
                EditSessionScreen(
                    focusViewModel = focusViewModel,
                    onCancel = { navigateToScreen(AppRoutes.History) },
                    onUpdatedSuccess = { navigateToScreen(AppRoutes.History) }
                )
            }

            // 10. Confirmar Borrado (DELETE)
            composable(AppRoutes.DeleteSession.route) {
                DeleteSessionScreen(
                    focusViewModel = focusViewModel,
                    onCancel = { navigateToScreen(AppRoutes.History) },
                    onDeletedSuccess = { navigateToScreen(AppRoutes.History) }
                )
            }

            // 11. Configuración Sensores
            composable(AppRoutes.SensorSettings.route) {
                SensorSettingsScreen(
                    focusViewModel = focusViewModel,
                    sensorViewModel = sensorViewModel,
                    onNavigateBack = { navigateToScreen(AppRoutes.Dashboard) }
                )
            }

            // 12. Perfil de Usuario
            composable(AppRoutes.OracleProfile.route) {
                OracleProfileScreen(
                    authViewModel = authViewModel,
                    focusViewModel = focusViewModel,
                    onNavigateBack = { navigateToScreen(AppRoutes.Dashboard) },
                    onLoggedOut = {
                        navController.navigate(AppRoutes.Login.route) {
                            popUpTo(0) { inclusive = true }
                        }
                    }
                )
            }
        }
    }
}

private inline fun <reified T : ViewModel> simpleFactory(
    crossinline creator: () -> T
): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <VM : ViewModel> create(modelClass: Class<VM>): VM = creator() as VM
}
