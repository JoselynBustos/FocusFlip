package mx.edu.utez.focusflip.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.automirrored.filled.RotateLeft
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import mx.edu.utez.focusflip.ui.components.GradientButton
import mx.edu.utez.focusflip.ui.theme.NeonBlue
import mx.edu.utez.focusflip.ui.theme.NeonEmerald
import mx.edu.utez.focusflip.ui.theme.NeonPurple
import mx.edu.utez.focusflip.ui.theme.Slate400
import mx.edu.utez.focusflip.ui.theme.Slate800
import mx.edu.utez.focusflip.ui.theme.Slate900
import mx.edu.utez.focusflip.ui.theme.Slate950

@Composable
fun OnboardingScreen(
    onNavigateToLogin: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Slate950)
            .verticalScroll(rememberScrollState())
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Botón Saltar
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Text(
                text = "Saltar",
                color = Slate400,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .clickable { onNavigateToLogin() }
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            )
        }

        // Ilustración central con efecto neón
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(22.dp)
        ) {
            Box(
                modifier = Modifier.size(185.dp),
                contentAlignment = Alignment.Center
            ) {
                // Halo brillante trasero
                Box(
                    modifier = Modifier
                        .size(175.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    NeonEmerald.copy(alpha = 0.28f),
                                    NeonPurple.copy(alpha = 0.22f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Tarjeta central del dispositivo
                Surface(
                    modifier = Modifier.size(148.dp),
                    shape = RoundedCornerShape(28.dp),
                    color = Slate900,
                    border = BorderStroke(1.5.dp, Slate800)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.RotateLeft,
                            contentDescription = "Giroscopio",
                            tint = NeonEmerald,
                            modifier = Modifier.size(44.dp)
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.PhoneAndroid,
                                contentDescription = "Teléfono",
                                tint = NeonPurple,
                                modifier = Modifier.size(28.dp)
                            )
                            Icon(
                                imageVector = Icons.Default.LightMode,
                                contentDescription = "Sensor de Luz",
                                tint = NeonBlue,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(horizontal = 8.dp)
            ) {
                Text(
                    text = "Modo No Molestar por Inclinación",
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.ExtraBold,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = buildAnnotatedString {
                        append("Solo coloca tu teléfono ")
                        withStyle(SpanStyle(color = NeonEmerald, fontWeight = FontWeight.Bold)) {
                            append("boca abajo")
                        }
                        append(" en tu escritorio. El giroscopio, el sensor de proximidad y el sensor de luz ambiental activarán el modo OLED e iniciarán tu sesión de estudio automáticamente.")
                    },
                    color = Slate400,
                    fontSize = 13.sp,
                    lineHeight = 19.sp,
                    textAlign = TextAlign.Center
                )
            }

            // Indicadores del carrusel
            Row(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(26.dp)
                        .height(6.dp)
                        .clip(CircleShape)
                        .background(NeonEmerald)
                )
                Box(
                    modifier = Modifier
                        .width(8.dp)
                        .height(6.dp)
                        .clip(CircleShape)
                        .background(Slate800)
                )
                Box(
                    modifier = Modifier
                        .width(8.dp)
                        .height(6.dp)
                        .clip(CircleShape)
                        .background(Slate800)
                )
            }
        }

        GradientButton(
            text = "Comenzar Ahora ➔",
            onClick = onNavigateToLogin
        )
    }
}
