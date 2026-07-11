package com.grupo06.doggoapp.presentation.screens.perfil

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
        if (uiState.isLoggedOut) {
            onLogoutSuccess()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(ColorFondo)
            .padding(24.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = "Pantalla de Perfil")

        OutlinedButton(
            onClick = { viewModel.cerrarSesion() },
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = ColorRojo),
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
        ) {
            Icon(Icons.AutoMirrored.Filled.Logout, contentDescription = null, tint = ColorRojo)
            Text(
                text = "Cerrar sesión",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}
