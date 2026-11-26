package mx.edu.utez.focusflip.ui.screens

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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mx.edu.utez.focusflip.ui.theme.NeonBlue
import mx.edu.utez.focusflip.ui.theme.Slate200
import mx.edu.utez.focusflip.ui.theme.Slate300
import mx.edu.utez.focusflip.ui.theme.Slate400
import mx.edu.utez.focusflip.ui.theme.Slate800
import mx.edu.utez.focusflip.ui.theme.Slate900
import mx.edu.utez.focusflip.ui.theme.Slate950
import mx.edu.utez.focusflip.ui.viewmodels.FocusViewModel

@Composable
fun EditSessionScreen(
    focusViewModel: FocusViewModel,
    onCancel: () -> Unit,
    onUpdatedSuccess: () -> Unit
) {
    val focusState by focusViewModel.uiState.collectAsStateWithLifecycle()
    val sessions by focusViewModel.sessions.collectAsStateWithLifecycle()

    val targetSession = focusState.activeSessionToEdit ?: sessions.firstOrNull()

    val asignaturaFija = targetSession?.asignatura ?: "Desarrollo de Apps Móviles"
    val duracionFija = targetSession?.duracionMinutos ?: 25
    var notas by remember(targetSession) {
        mutableStateOf(
            targetSession?.notas
                ?: "Avance en actividades del proyecto y repaso de la materia."
        )
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
                    text = "Editar Notas de Sesión",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
                Spacer(modifier = Modifier.width(40.dp))
            }

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
                        value = asignaturaFija,
                        onValueChange = { },
                        enabled = false,
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledContainerColor = Slate900.copy(alpha = 0.6f),
                            disabledBorderColor = Slate800,
                            disabledTextColor = Slate400
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Column {
                    Text(
                        text = "Duración Registrada (No editable)",
                        color = Slate300,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    OutlinedTextField(
                        value = "$duracionFija minutos",
                        onValueChange = { },
                        enabled = false,
                        singleLine = true,
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledContainerColor = Slate900.copy(alpha = 0.6f),
                            disabledBorderColor = Slate800,
                            disabledTextColor = Slate400
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
                            focusedBorderColor = NeonBlue,
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

        Button(
            onClick = {
                if (targetSession != null) {
                    focusViewModel.updateSession(
                        sessionId = targetSession.id,
                        asignatura = asignaturaFija,
                        duracionMinutos = duracionFija,
                        notas = notas,
                        onComplete = onUpdatedSuccess
                    )
                } else {
                    onUpdatedSuccess()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = NeonBlue,
                contentColor = Color.White
            )
        ) {
            Text(
                text = "Guardar Cambios",
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )
        }
    }
}
