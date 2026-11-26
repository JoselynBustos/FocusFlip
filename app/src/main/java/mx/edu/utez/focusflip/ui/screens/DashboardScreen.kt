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
import androidx.compose.material.icons.automirrored.filled.RotateRight
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mx.edu.utez.focusflip.ui.components.GlassCard
import mx.edu.utez.focusflip.ui.theme.NeonBlue
import mx.edu.utez.focusflip.ui.theme.NeonEmerald
import mx.edu.utez.focusflip.ui.theme.NeonEmeraldDark
import mx.edu.utez.focusflip.ui.theme.NeonPurple
import mx.edu.utez.focusflip.ui.theme.Slate200
import mx.edu.utez.focusflip.ui.theme.Slate300
import mx.edu.utez.focusflip.ui.theme.Slate400
import mx.edu.utez.focusflip.ui.theme.Slate800
import mx.edu.utez.focusflip.ui.theme.Slate900
import mx.edu.utez.focusflip.ui.theme.Slate950
import mx.edu.utez.focusflip.ui.viewmodels.FocusViewModel
import mx.edu.utez.focusflip.ui.viewmodels.SensorViewModel

@Composable
fun DashboardScreen(
    focusViewModel: FocusViewModel,
    sensorViewModel: SensorViewModel,
    onNavigateToOledFocus: () -> Unit,
    onNavigateToSensorSettings: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val context = LocalContext.current
    val focusState by focusViewModel.uiState.collectAsStateWithLifecycle()
    val prefs by focusViewModel.userPrefs.collectAsStateWithLifecycle()
    val totalMinutes by focusViewModel.totalMinutesFlow.collectAsStateWithLifecycle()
    val todayMinutes by focusViewModel.todayMinutesFlow.collectAsStateWithLifecycle()
    val sensorData by sensorViewModel.sensorData.collectAsStateWithLifecycle()

    var expandedSubjectMenu by remember { mutableStateOf(false) }
    var showEditGoalDialog by remember { mutableStateOf(false) }
    var goalInput by remember(prefs.dailyGoalMinutes) {
        mutableStateOf(prefs.dailyGoalMinutes.toString())
    }

    DisposableEffect(Unit) {
        sensorViewModel.startListening(context)
        onDispose { }
    }

    LaunchedEffect(sensorData.isFaceDownHardware) {
        if (sensorData.isFaceDownHardware) {
            focusViewModel.startFocusTimer()
            onNavigateToOledFocus()
        }
    }

    if (showEditGoalDialog) {
        AlertDialog(
            onDismissRequest = { showEditGoalDialog = false },
            containerColor = Slate900,
            title = {
                Text("Editar Meta del Día", color = Color.White, fontWeight = FontWeight.Bold)
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        "Define tu meta diaria de minutos enfocados:",
                        color = Slate300,
                        fontSize = 12.sp
                    )
                    OutlinedTextField(
                        value = goalInput,
                        onValueChange = { goalInput = it.filter { ch -> ch.isDigit() } },
                        label = { Text("Minutos por día (ej. 120)") },
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val parsed = goalInput.toIntOrNull() ?: 120
                        focusViewModel.updateDailyGoal(parsed)
                        showEditGoalDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = NeonEmerald, contentColor = Slate950)
                ) {
                    Text("Guardar Meta", fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditGoalDialog = false }) {
                    Text("Cancelar", color = Slate400)
                }
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate950)
            .verticalScroll(rememberScrollState())
            .padding(18.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Barra superior de perfil
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.clickable { onNavigateToProfile() }
            ) {
                Box(
                    modifier = Modifier
                        .size(38.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(NeonEmerald, NeonBlue))),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = prefs.nombreCompleto.ifBlank { "FF" }.take(2).uppercase(),
                        color = Slate950,
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 13.sp
                    )
                }
                Column {
                    Text(
                        text = prefs.nombreCompleto.ifBlank { "Estudiante UTEZ" }.substringBeforeLast(" "),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(NeonEmerald)
                        )
                        Text(
                            text = "Sensor Listo",
                            color = NeonEmerald,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            IconButton(
                onClick = onNavigateToSensorSettings,
                modifier = Modifier
                    .size(36.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(Slate900)
            ) {
                Icon(
                    imageVector = Icons.Default.Settings,
                    contentDescription = "Ajustes de Sensores",
                    tint = Slate400,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        // Selector de Asignatura
        Column {
            Text(
                text = "SELECCIONA ASIGNATURA",
                color = Slate400,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Box {
                Surface(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .clickable { expandedSubjectMenu = true },
                    color = Slate900,
                    shape = RoundedCornerShape(14.dp),
                    border = BorderStroke(1.dp, Slate800)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 13.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = focusState.selectedSubject,
                            color = Slate200,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Icon(
                            imageVector = Icons.Default.ArrowDropDown,
                            contentDescription = null,
                            tint = NeonEmerald
                        )
                    }
                }
                DropdownMenu(
                    expanded = expandedSubjectMenu,
                    onDismissRequest = { expandedSubjectMenu = false },
                    modifier = Modifier.background(Slate900)
                ) {
                    focusState.subjects.forEach { subject ->
                        DropdownMenuItem(
                            text = { Text(subject, color = Slate200, fontSize = 13.sp) },
                            onClick = {
                                focusViewModel.selectSubject(subject)
                                expandedSubjectMenu = false
                            }
                        )
                    }
                }
            }
        }

        // Tarjeta Principal de Activación por Giroscopio / Inclinación
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = NeonEmerald.copy(alpha = 0.4f),
            onClick = {
                sensorViewModel.setSimulatedFaceDown(true, context)
                focusViewModel.startFocusTimer()
                onNavigateToOledFocus()
            }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .width(68.dp)
                        .height(110.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(NeonEmeraldDark.copy(alpha = 0.35f))
                        .border(2.dp, NeonEmerald.copy(alpha = 0.8f), RoundedCornerShape(18.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.PhoneAndroid,
                            contentDescription = null,
                            tint = NeonEmerald,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.RotateRight,
                            contentDescription = null,
                            tint = NeonEmerald,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Coloca tu teléfono boca abajo",
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "El contador iniciará automáticamente al detectar que colocaste tu teléfono sobre la mesa.",
                    color = Slate400,
                    fontSize = 11.sp,
                    lineHeight = 16.sp,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                Surface(
                    color = NeonEmerald.copy(alpha = 0.14f),
                    shape = RoundedCornerShape(50),
                    border = BorderStroke(1.dp, NeonEmerald.copy(alpha = 0.35f))
                ) {
                    Text(
                        text = "Haz clic para Simular Giro ➔",
                        color = NeonEmerald,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )
                }
            }
        }

        // Resumen de Métricas
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            GlassCard(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Horas Enfocadas",
                    color = Slate400,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${"%.1f".format(totalMinutes / 60.0)} hrs",
                    color = Color.White,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            GlassCard(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Racha Actual",
                    color = Slate400,
                    fontSize = 11.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "🔥 5 días",
                    color = NeonEmerald,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        // Tarjeta de Meta del Día
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = NeonPurple.copy(alpha = 0.4f)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Meta Diaria de Concentración",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "$todayMinutes min / ${prefs.dailyGoalMinutes} min completados hoy",
                        color = Slate400,
                        fontSize = 11.sp
                    )
                }
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { showEditGoalDialog = true },
                    color = NeonPurple.copy(alpha = 0.18f),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, NeonPurple.copy(alpha = 0.45f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Editar Meta",
                            tint = NeonPurple,
                            modifier = Modifier.size(13.dp)
                        )
                        Text(
                            text = "Editar Meta",
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            val progress = (todayMinutes.toFloat() / prefs.dailyGoalMinutes.coerceAtLeast(1).toFloat())
                .coerceIn(0.05f, 1f)
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(7.dp)
                    .clip(RoundedCornerShape(50)),
                color = NeonEmerald,
                trackColor = Slate800
            )
        }
    }
}
