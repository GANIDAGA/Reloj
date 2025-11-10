package com.example.reloj

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.reloj.ui.theme.RelojTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            RelojTheme {
                UIPrincipal()
            }
        }
    }
}

@Composable
fun UIPrincipal() {
    val context = LocalContext.current
    val scope = rememberCoroutineScope() // coroutine scope para usar delay
    var horaActual by remember { mutableStateOf(obtenerHoraActual()) }

    // Actualiza la hora cada segundo
    LaunchedEffect(Unit) {
        while (true) {
            horaActual = obtenerHoraActual()
            delay(1000)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = horaActual,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(onClick = {
                // Ejecutar dentro de coroutine para poder usar delay sin bloquear la UI
                scope.launch {
                    var luz = 0
                    var nombreluz = ""

                    // Obtener minutos
                    val formato = SimpleDateFormat("mm", Locale.getDefault())
                    val minutos = formato.format(Date())

                    // Obtener hora 24h y convertir a 12h
                    val formato2 = SimpleDateFormat("HH", Locale.getDefault())
                    var horaval = formato2.format(Date()).toInt()
                    if (horaval == 0) horaval = 12
                    else if (horaval > 12) {
                        horaval -= 12
                        luz = 1
                    }
                    val horas = String.format("%02d", horaval) // siempre dos dígitos

                    // Reproducir audio de la hora
                    val resId2 = context.resources.getIdentifier("audiohora$horas", "raw", context.packageName)
                    if (resId2 != 0) {
                        reproducirAudio(context, resId2)
                    } else {
                        println("No se encontró el archivo para la hora $horas")
                    }

                    delay(2050) // reemplaza Thread.sleep(2000)

                    // Determinar AM / PM
                    nombreluz = if (luz == 1) "pm" else "am"
                    val resId3 = context.resources.getIdentifier(nombreluz, "raw", context.packageName)
                    if (resId3 != 0) {  // <-- corregido de resId2 a resId3
                        reproducirAudio(context, resId3)
                    } else {
                        println("No se encontró el archivo para $nombreluz")
                    }

                    delay(1200) // reemplaza Thread.sleep(1500)

                    // Reproducir audio de los minutos
                    val resId = context.resources.getIdentifier("audio$minutos", "raw", context.packageName)
                    if (resId != 0) {
                        reproducirAudio(context, resId)
                    } else {
                        println("No se encontró el archivo para el minuto $minutos")
                    }
                }
            }) {
                Text("Te digo la hora")
            }
        }
    }
}

// Función para obtener la hora actual en HH:mm:ss
fun obtenerHoraActual(): String {
    val formato = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
    return formato.format(Date())
}

// Función para reproducir un audio desde res/raw
fun reproducirAudio(context: Context, recursoAudio: Int) {
    try {
        val mediaPlayer = MediaPlayer.create(context, recursoAudio)
        mediaPlayer?.start()

        mediaPlayer?.setOnCompletionListener {
            it.release()
            println("Reproducción terminada")
        }
    } catch (e: Exception) {
        e.printStackTrace()
        println("Error al reproducir el audio: ${e.message}")
    }
}

@Preview(showBackground = true)
@Composable
fun VistaPreviaReloj() {
    RelojTheme {
        UIPrincipal()
    }
}
