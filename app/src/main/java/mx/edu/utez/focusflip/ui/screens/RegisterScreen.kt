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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mx.edu.utez.focusflip.ui.theme.NeonEmerald
import mx.edu.utez.focusflip.ui.theme.OracleRed
import mx.edu.utez.focusflip.ui.theme.Slate200
import mx.edu.utez.focusflip.ui.theme.Slate300
import mx.edu.utez.focusflip.ui.theme.Slate400
import mx.edu.utez.focusflip.ui.theme.Slate500
import mx.edu.utez.focusflip.ui.theme.Slate800
import mx.edu.utez.focusflip.ui.theme.Slate900
import mx.edu.utez.focusflip.ui.theme.Slate950
import mx.edu.utez.focusflip.ui.viewmodels.AuthEvent
import mx.edu.utez.focusflip.ui.viewmodels.AuthViewModel

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onNavigateBack: () -> Unit,
    onRegisterSuccess: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            if (event is AuthEvent.NavigateToDashboard) {
                onRegisterSuccess()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate950)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onNavigateBack() }
                    .padding(vertical = 6.dp, horizontal = 4.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = Slate400,
                    modifier = Modifier.size(16.dp)
                )
                Text(
                    text = "Volver",
                    color = Slate400,
                    fontSize = 13.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Crear Cuenta",
                color = Color.White,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Registra tus datos para guardar tus sesiones de estudio",
                color = Slate400,
                fontSize = 13.sp
            )

            Spacer(modifier = Modifier.height(18.dp))

            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                RegisterField(
                    label = "Nombre Completo",
                    value = state.nombreCompleto,
                    placeholder = "Ej. Carlos Mendoza",
                    onValueChange = viewModel::onNombreChange
                )
                RegisterField(
                    label = "Matrícula / Usuario",
                    value = state.matricula,
                    placeholder = "20264A001",
                    onValueChange = viewModel::onMatriculaChange
                )
                RegisterField(
                    label = "Correo Electrónico",
                    value = state.correo,
                    placeholder = "alumno@ut.edu.mx",
                    onValueChange = viewModel::onCorreoChange
                )
                RegisterField(
                    label = "Contraseña",
                    value = state.password,
                    placeholder = "Mínimo 6 caracteres",
                    isPassword = true,
                    onValueChange = viewModel::onPasswordChange
                )
            }

            state.errorMessage?.let { error ->
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = error,
                    color = OracleRed,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = viewModel::register,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = NeonEmerald,
                contentColor = Slate950
            )
        ) {
            Text(
                text = "Crear Cuenta",
                fontWeight = FontWeight.ExtraBold,
                fontSize = 13.sp
            )
        }
    }
}

@Composable
private fun RegisterField(
    label: String,
    value: String,
    placeholder: String,
    isPassword: Boolean = false,
    onValueChange: (String) -> Unit
) {
    Column {
        Text(
            text = label,
            color = Slate300,
            fontSize = 12.sp,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(4.dp))
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = { Text(placeholder, color = Slate500, fontSize = 12.sp) },
            singleLine = true,
            visualTransformation = if (isPassword) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
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
