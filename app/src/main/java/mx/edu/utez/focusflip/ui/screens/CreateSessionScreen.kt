package mx.edu.utez.focusflip.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mx.edu.utez.focusflip.ui.components.GradientButton
import mx.edu.utez.focusflip.ui.theme.NeonBlue
import mx.edu.utez.focusflip.ui.theme.NeonEmerald
import mx.edu.utez.focusflip.ui.theme.Slate200
import mx.edu.utez.focusflip.ui.theme.Slate300
import mx.edu.utez.focusflip.ui.theme.Slate400
import mx.edu.utez.focusflip.ui.theme.Slate800
import mx.edu.utez.focusflip.ui.theme.Slate900
import mx.edu.utez.focusflip.ui.theme.Slate950
import mx.edu.utez.focusflip.ui.viewmodels.FocusViewModel
import mx.edu.utez.focusflip.ui.viewmodels.SensorViewModel

@Composable
fun CreateSessionScreen(
    focusViewModel: FocusViewModel,
    sensorViewModel: SensorViewModel,
    onCancel: () -> Unit,
    onSavedSuccess: () -> Unit
) {
    val focusState by focusViewModel.uiState.collectAsStateWithLifecycle()
    val sensorData by sensorViewModel.sensorData.collectAsStateWithLifecycle()

    var asignatura by remember(focusState.selectedSubject) {
        mutableStateOf(focusState.selectedSubject)
    }
    var duracionStr by remember(focusState.lastMeasuredMinutes) {
        mutableStateOf(focusState.lastMeasuredMinutes.toString())
    }
    var notas by remember {
        mutableStateOf("Bocetos del prototipo integrador terminados en Classroom.")
    }

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
                Text(
                    text = "Cancelar",
                    color = Slate400,
                    fontSize = 13.sp,
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .clickable { onCancel() }
                        .padding(6.dp)
                )
                Text(
                    text = "Guardar Sesión (CREATE)",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
                Spacer(modifier = Modifier.width(40.dp))
            }

            // Banner de tiempo registrado
            Surface(
                color = NeonEmerald.copy(alpha = 0.12f),
                shape = RoundedCornerShape(14.dp),
                border = BorderStroke(1.dp, NeonEmerald.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "¡Tiempo registrado: ${duracionStr.ifBlank { "25" }} minutos!",
                        color = NeonEmerald,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = "Sensor: Giroscopio 180° + Luz (${"%.1f".format(sensorData.lux)} lx) + Proximidad",
                        color = Slate300,
                        fontSize = 10.sp
                    )
                }
            }

            // Campos del formulario CREATE
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Column {
                    Text(
                        text = "Asignatura",
                        color = Slate300,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = asignatura,
                        onValueChange = { asignatura = it },
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
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
                }

                Column {
                    Text(
                        text = "Duración Medida (Minutos)",
                        color = Slate300,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = duracionStr,
                        onValueChange = { duracionStr = it.filter { ch -> ch.isDigit() } },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        shape = RoundedCornerShape(14.dp),
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
                }

                Column {
                    Text(
                        text = "Notas de la Sesión",
                        color = Slate300,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = notas,
                        onValueChange = { notas = it },
                        minLines = 3,
                        maxLines = 4,
                        shape = RoundedCornerShape(14.dp),
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
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        GradientButton(
            text = "Guardar Sesión",
            onClick = {
                val mins = duracionStr.toIntOrNull() ?: 25
                focusViewModel.createSession(
                    asignatura = asignatura,
                    duracionMinutos = mins,
                    notas = notas,
                    currentLux = sensorData.lux,
                    sensorModo = "Giroscopio 180° + Luz OLED (${"%.0f".format(sensorData.lux)} lx)",
                    onComplete = onSavedSuccess
                )
            },
            colors = listOf(NeonEmerald, NeonBlue)
        )
    }
}
