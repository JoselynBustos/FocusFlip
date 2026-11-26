package mx.edu.utez.focusflip.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mx.edu.utez.focusflip.ui.components.GlassCard
import mx.edu.utez.focusflip.ui.theme.NeonBlue
import mx.edu.utez.focusflip.ui.theme.NeonEmerald
import mx.edu.utez.focusflip.ui.theme.NeonEmeraldDark
import mx.edu.utez.focusflip.ui.theme.OracleRed
import mx.edu.utez.focusflip.ui.theme.Slate200
import mx.edu.utez.focusflip.ui.theme.Slate400
import mx.edu.utez.focusflip.ui.theme.Slate800
import mx.edu.utez.focusflip.ui.theme.Slate900
import mx.edu.utez.focusflip.ui.theme.Slate950
import mx.edu.utez.focusflip.ui.viewmodels.AuthViewModel
import mx.edu.utez.focusflip.ui.viewmodels.FocusViewModel

@Composable
fun OracleProfileScreen(
    authViewModel: AuthViewModel,
    focusViewModel: FocusViewModel,
    onNavigateBack: () -> Unit,
    onLoggedOut: () -> Unit
) {
    val prefs by focusViewModel.userPrefs.collectAsStateWithLifecycle()
    val focusState by focusViewModel.uiState.collectAsStateWithLifecycle()
    val totalMinutes by focusViewModel.totalMinutesFlow.collectAsStateWithLifecycle()
    val sessions by focusViewModel.sessions.collectAsStateWithLifecycle()

    var apiUrlInput by remember(prefs.oracleBaseUrl) {
        mutableStateOf(prefs.oracleBaseUrl)
    }

    val hours = totalMinutes / 60
    val remMins = totalMinutes % 60

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate950)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
            // Encabezado
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = Slate400,
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onNavigateBack() }
                        .padding(4.dp)
                )
                Text(
                    text = "Perfil de Estudiante",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.width(24.dp))
            }

            // Avatar y Nombre
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(NeonEmerald, NeonBlue)))
                        .border(2.dp, NeonEmerald, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = prefs.nombreCompleto.ifBlank { "FF" }.take(2).uppercase(),
                        color = Slate950,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 24.sp
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = prefs.nombreCompleto.ifBlank { "Estudiante UTEZ" },
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = "4°A - Desarrollo de Software Multiplataforma",
                    color = Slate400,
                    fontSize = 11.sp
                )
            }

            // Tarjeta Base de Datos Local Room + API REST
            Surface(
                color = NeonEmeraldDark.copy(alpha = 0.35f),
                shape = RoundedCornerShape(16.dp),
                border = BorderStroke(1.dp, NeonEmerald.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storage,
                            contentDescription = "Base de Datos",
                            tint = NeonEmerald,
                            modifier = Modifier.size(22.dp)
                        )
                        Column {
                            Text(
                                text = "Persistencia Room + API REST",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "SQLite Local + Retrofit 2",
                                color = Slate400,
                                fontSize = 10.sp
                            )
                        }
                    }

                    Surface(
                        color = NeonEmerald.copy(alpha = 0.14f),
                        shape = RoundedCornerShape(50),
                        border = BorderStroke(1.dp, NeonEmerald.copy(alpha = 0.35f))
                    ) {
                        Text(
                            text = "Activo",
                            color = NeonEmerald,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            // Estadísticas de sesión
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = Slate800
            ) {
                ProfileStatRow("Correo:", prefs.correo)
                ProfileStatRow("Matrícula:", prefs.matricula, isMono = true)
                ProfileStatRow("Total Enfoque:", "$hours hrs $remMins min", highlight = true)
                ProfileStatRow("Meta Diaria:", "${prefs.dailyGoalMinutes} min/día")
                ProfileStatRow("Sesiones Guardadas:", "${sessions.size} registros en Bitácora")
            }

            // Configuración de Endpoint API REST (Retrofit)
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = Slate800
            ) {
                Text(
                    text = "Servidor API REST (Retrofit Base URL)",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                OutlinedTextField(
                    value = apiUrlInput,
                    onValueChange = { apiUrlInput = it },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = Slate900,
                        unfocusedContainerColor = Slate900,
                        focusedBorderColor = NeonEmerald,
                        unfocusedBorderColor = Slate800,
                        focusedTextColor = Slate200,
                        unfocusedTextColor = Slate200
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { focusViewModel.updateOracleUrlAndSync(apiUrlInput) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = NeonBlue,
                        contentColor = Color.White
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudSync,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (focusState.isSyncing) "Sincronizando..." else "Sincronizar con API REST",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = focusState.cloudStatusMessage,
                    color = NeonEmerald,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Button(
            onClick = {
                authViewModel.logout()
                onLoggedOut()
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = OracleRed.copy(alpha = 0.2f),
                contentColor = OracleRed
            ),
            border = BorderStroke(1.dp, OracleRed.copy(alpha = 0.4f))
        ) {
            Text(
                text = "Cerrar Sesión",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun ProfileStatRow(
    label: String,
    value: String,
    isMono: Boolean = false,
    highlight: Boolean = false
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Slate400, fontSize = 12.sp)
        Text(
            text = value,
            color = if (highlight) NeonEmerald else Slate200,
            fontSize = 12.sp,
            fontWeight = if (highlight) FontWeight.Bold else FontWeight.Medium,
            fontFamily = if (isMono) FontFamily.Monospace else FontFamily.Default
        )
    }
}
