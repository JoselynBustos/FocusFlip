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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import mx.edu.utez.focusflip.R
import mx.edu.utez.focusflip.ui.components.GlassCard
import mx.edu.utez.focusflip.ui.theme.NeonBlue
import mx.edu.utez.focusflip.ui.theme.NeonEmerald
import mx.edu.utez.focusflip.ui.theme.Slate200
import mx.edu.utez.focusflip.ui.theme.Slate400
import mx.edu.utez.focusflip.ui.theme.Slate800
import mx.edu.utez.focusflip.ui.theme.Slate900
import mx.edu.utez.focusflip.ui.theme.Slate950
import mx.edu.utez.focusflip.ui.viewmodels.FocusViewModel
import mx.edu.utez.focusflip.ui.viewmodels.SensorViewModel

@Composable
fun SensorSettingsScreen(
    focusViewModel: FocusViewModel,
    sensorViewModel: SensorViewModel,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current
    val prefs by focusViewModel.userPrefs.collectAsStateWithLifecycle()

    var sensitivity by remember(prefs.gyroSensitivity) {
        mutableFloatStateOf(prefs.gyroSensitivity)
    }
    var useProximity by remember(prefs.useProximitySensor) {
        mutableStateOf(prefs.useProximitySensor)
    }
    var useLight by remember(prefs.useLightSensor) {
        mutableStateOf(prefs.useLightSensor)
    }
    var soundAlerts by remember(prefs.soundAlertsEnabled) {
        mutableStateOf(prefs.soundAlertsEnabled)
    }
    var calibratedMessage by remember { mutableStateOf<String?>(null) }

    DisposableEffect(Unit) {
        sensorViewModel.startListening(context)
        onDispose { }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate950)
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Encabezado
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onNavigateBack() }
                    .padding(6.dp)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Inicio",
                    tint = Slate400,
                    modifier = Modifier.size(16.dp)
                )
                Text(text = "Inicio", color = Slate400, fontSize = 13.sp)
            }
            Text(
                text = "Ajustes de Sensores",
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
            Spacer(modifier = Modifier.width(40.dp))
        }

        // Control 1: Sensibilidad Giroscopio
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = Slate800
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Sensibilidad Giroscopio",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Text(
                    text = if (sensitivity >= 0.75f) "Alta (180°)" else if (sensitivity >= 0.5f) "Media (165°)" else "Baja (150°)",
                    color = NeonEmerald,
                    fontSize = 12.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
            Slider(
                value = sensitivity,
                onValueChange = {
                    sensitivity = it
                    sensorViewModel.applySettings(sensitivity, useProximity, useLight, soundAlerts)
                    focusViewModel.saveSensorSettings(sensitivity, useProximity, useLight, soundAlerts)
                },
                valueRange = 0.3f..1.0f,
                colors = SliderDefaults.colors(
                    thumbColor = NeonEmerald,
                    activeTrackColor = NeonEmerald,
                    inactiveTrackColor = Slate800
                )
            )
        }

        // Control 2: Sensor de Proximidad
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = Slate800
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Sensor de Proximidad",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Activar pantalla OLED al colocar el teléfono boca abajo",
                        color = Slate400,
                        fontSize = 11.sp
                    )
                }
                Switch(
                    checked = useProximity,
                    onCheckedChange = {
                        useProximity = it
                        sensorViewModel.applySettings(sensitivity, useProximity, useLight, soundAlerts)
                        focusViewModel.saveSensorSettings(sensitivity, useProximity, useLight, soundAlerts)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Slate950,
                        checkedTrackColor = NeonEmerald
                    )
                )
            }
        }

        // Control 3: Sensor de Luz Ambiental
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = Slate800
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Sensor de Luz Ambiental",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Detectar oscuridad sobre la mesa para iniciar el enfoque",
                        color = Slate400,
                        fontSize = 11.sp
                    )
                }
                Switch(
                    checked = useLight,
                    onCheckedChange = {
                        useLight = it
                        sensorViewModel.applySettings(sensitivity, useProximity, useLight, soundAlerts)
                        focusViewModel.saveSensorSettings(sensitivity, useProximity, useLight, soundAlerts)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Slate950,
                        checkedTrackColor = NeonBlue
                    )
                )
            }
        }

        // Control 4: Sonido al Voltear / Pausar
        GlassCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = Slate800
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Sonido al Voltear / Pausar",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Reproducir alerta sonora al iniciar o pausar sesión",
                        color = Slate400,
                        fontSize = 11.sp
                    )
                }
                Switch(
                    checked = soundAlerts,
                    onCheckedChange = {
                        soundAlerts = it
                        sensorViewModel.applySettings(sensitivity, useProximity, useLight, soundAlerts)
                        focusViewModel.saveSensorSettings(sensitivity, useProximity, useLight, soundAlerts)
                    },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Slate950,
                        checkedTrackColor = NeonEmerald
                    )
                )
            }
        }

        calibratedMessage?.let { msg ->
            Surface(
                color = NeonEmerald.copy(alpha = 0.14f),
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, NeonEmerald.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = msg,
                    color = NeonEmerald,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        Button(
            onClick = {
                sensorViewModel.calibrateSensors()
                sensorViewModel.playSound(context, R.raw.laser)
                calibratedMessage = "✓ Sensores calibrados correctamente."
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(46.dp),
            shape = RoundedCornerShape(14.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Slate900,
                contentColor = Slate200
            ),
            border = BorderStroke(1.dp, Slate800)
        ) {
            Icon(
                imageVector = Icons.Default.Explore,
                contentDescription = null,
                tint = NeonEmerald,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Calibrar Sensor Ahora",
                fontWeight = FontWeight.SemiBold,
                fontSize = 13.sp
            )
        }
    }
}
