package com.grupo06.doggoapp.presentation.screens.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grupo06.doggoapp.di.AppContainer

private val ColorFondo = Color(0xFFFCFBF8)
private val ColorRojo = Color(0xFFDC2626)

@Composable
fun PerfilScreen(appContainer: AppContainer, onLogoutSuccess: () -> Unit) {
    val viewModel = appContainer.perfilViewModel
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.isLoggedOut) {
        if (uiState.isLoggedOut) { onLogoutSuccess() }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorFondo)
            .padding(24.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
            Box(modifier = Modifier.size(100.dp).clip(CircleShape).background(Color(0xFF10B981)), contentAlignment = Alignment.Center) {
                Text("AL", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("Ana López", fontSize = 22.sp, fontWeight = FontWeight.Bold)
            Text("ana.lopez@correo.com", color = Color.Gray)
        }

        Spacer(modifier = Modifier.height(24.dp))

        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            StatItem("12", "CITAS")
            StatItem("9", "RESEÑAS")
            StatItem("4", "FAVORITOS")
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text("CUENTA", fontWeight = FontWeight.Bold, color = Color.Gray, fontSize = 12.sp)
        PerfilItem("Información personal")
        PerfilItem("Ubicación")

        Spacer(modifier = Modifier.height(24.dp))

        OutlinedButton(
            onClick = { viewModel.cerrarSesion() },
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = ColorRojo),
            modifier = Modifier.fillMaxWidth().height(52.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.Logout, null, tint = ColorRojo)
            Text("Cerrar sesión", fontSize = 16.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 8.dp))
        }
    }
}

@Composable
fun StatItem(valor: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(valor, fontWeight = FontWeight.Bold, fontSize = 20.sp)
        Text(label, fontSize = 10.sp, color = Color.Gray)
    }
}

@Composable
fun PerfilItem(texto: String) {
    Card(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp), colors = CardDefaults.cardColors(containerColor = Color.White)) {
        Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            Text(texto, fontWeight = FontWeight.Medium)
            Icon(Icons.Default.ChevronRight, null, tint = Color.Gray)
        }
    }
}