package com.grupo06.doggoapp.presentation.screens.reserva

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ReservarScreen(onBackClick: () -> Unit, onConfirmarClick: () -> Unit) {
    val colorVerde = Color(0xFF10B981)
    val colorFondo = Color(0xFFFCFBF8)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorFondo)
            .padding(20.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = { onBackClick() }) {
                Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
            }
            Text(
                text = "Reservar Cita",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(30.dp))

        Text("Selecciona un horario disponible:", fontSize = 16.sp, fontWeight = FontWeight.Medium)

        Spacer(modifier = Modifier.height(20.dp))
        Button(
            onClick = {},
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color.White),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text("Mañana: 09:00 AM - 11:00 AM", color = Color.Black)
        }

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = { onConfirmarClick() },
            modifier = Modifier.fillMaxWidth().height(52.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colorVerde),
            shape = RoundedCornerShape(24.dp)
        ) {
            Text("Confirmar Reserva", fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}