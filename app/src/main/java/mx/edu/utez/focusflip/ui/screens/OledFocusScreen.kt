package mx.edu.utez.focusflip.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import mx.edu.utez.focusflip.ui.theme.AmberAlert
import mx.edu.utez.focusflip.ui.theme.NeonEmerald
import mx.edu.utez.focusflip.ui.theme.PureOledBlack
import mx.edu.utez.focusflip.ui.theme.Slate400
import mx.edu.utez.focusflip.ui.theme.Slate500
import mx.edu.utez.focusflip.ui.theme.Slate800
import mx.edu.utez.focusflip.ui.theme.Slate900
import mx.edu.utez.focusflip.ui.viewmodels.FocusViewModel
import mx.edu.utez.focusflip.ui.viewmodels.SensorViewModel

@Composable
fun OledFocusScreen(
    focusViewModel: FocusViewModel,
    sensorViewModel: SensorViewModel,
    onNavigateToPauseAlert: () -> Unit,
    onNavigateToCreateSession: () -> Unit
) {
    val context = LocalContext.current
    val focusState by focusViewModel.uiState.collectAsStateWithLifecycle()
    val sensorData by sensorViewModel.sensorData.collectAsStateWithLifecycle()

    var sawHardwareFaceDown by remember { mutableStateOf(sensorData.isFaceDownHardware) }

    DisposableEffect(Unit) {
        sensorViewModel.startListening(context)
        focusViewModel.startFocusTimer()
        onDispose { }
    }

    // Reloj en tiempo real que avanza cada segundo
    LaunchedEffect(focusState.isTimerRunning) {
        while (focusState.isTimerRunning) {
            delay(1000L)
            focusViewModel.tickOneSecond()
        }
    }

    // Si estaba boca abajo por sensor físico y el usuario levanta el teléfono, dispara la Vista 06 (Alerta de Pausa)
    LaunchedEffect(sensorData.isFaceDownHardware) {
        if (sensorData.isFaceDownHardware) {
            sawHardwareFaceDown = true
        } else if (sawHardwareFaceDown && !sensorData.isSimulatedFaceDown) {
            focusViewModel.pauseFocusTimer()
            sensorViewModel.setSimulatedFaceDown(false, context)
            onNavigateToPauseAlert()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PureOledBlack)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Barra superior OLED
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(NeonEmerald)
                )
                Text(
                    text = "GIROSCOPIO + LUZ ACTIVOS",
                    color = NeonEmerald,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
            }
            Text(
                text = "OLED Mode • ${"%.1f".format(sensorData.lux)} lx",
                color = Slate500,
                fontSize = 11.sp,
                fontFamily = FontFamily.Monospace
            )
        }

        // Anillo central del cronómetro de enfoque
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(210.dp)
                    .clip(CircleShape)
                    .border(3.dp, NeonEmerald.copy(alpha = 0.65f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "ENFOQUE TOTAL",
                        color = Slate400,
                        fontSize = 11.sp,
                        fontFamily = FontFamily.Monospace,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = focusState.formattedTimer,
                        color = Color.White,
                        fontSize = 44.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = focusState.selectedSubject,
                        color = NeonEmerald,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            Text(
                text = "Teléfono inclinado 180° sobre superficie",
                color = Slate400,
                fontSize = 12.sp
            )

            // Controles rápidos para demostración en vivo (Reiniciar 00:00 / Adelantar +5 min)
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = { focusViewModel.resetAndStartTimer() },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Slate900,
                        contentColor = Slate400
                    ),
                    border = BorderStroke(1.dp, Slate800),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Reiniciar 00:00", fontSize = 11.sp)
                }
                Button(
                    onClick = { focusViewModel.addDemoMinutes(5) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Slate900,
                        contentColor = NeonEmerald
                    ),
                    border = BorderStroke(1.dp, NeonEmerald.copy(alpha = 0.35f)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("+5 min (Demo)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }

            Surface(
                color = Slate900.copy(alpha = 0.6f),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, Slate800)
            ) {
                Text(
                    text = "Eje Z: ${"%.1f".format(sensorData.accelZ)} m/s² • Proximidad: ${if (sensorData.isProximityNear) "Cerca" else "Activa"} • Luz: ${"%.1f".format(sensorData.lux)} lx",
                    color = Slate400,
                    fontSize = 10.sp,
                    fontFamily = FontFamily.Monospace,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                )
            }
        }

        // Botones de acción inferior
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Button(
                onClick = {
                    focusViewModel.pauseFocusTimer()
                    sensorViewModel.setSimulatedFaceDown(false, context)
                    onNavigateToPauseAlert()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Slate900,
                    contentColor = AmberAlert
                ),
                border = BorderStroke(1.dp, Slate800)
            ) {
                Text(
                    text = "Simular Levantar Teléfono (Pausa)",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                )
            }

            Button(
                onClick = {
                    focusViewModel.finishFocusTimerAndPrepareCreate()
                    sensorViewModel.setSimulatedFaceDown(false, context)
                    onNavigateToCreateSession()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(46.dp),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = NeonEmerald.copy(alpha = 0.2f),
                    contentColor = NeonEmerald
                ),
                border = BorderStroke(1.dp, NeonEmerald.copy(alpha = 0.45f))
            ) {
                Text(
                    text = "Finalizar y Guardar",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }
        }
    }
}
