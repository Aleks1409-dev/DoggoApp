package com.piero.doggoapp.presentation.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Error
import androidx.compose.material.icons.filled.Warning
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class SnackbarStatus(
    val colorFondo: Color,
    val colorTexto: Color,
    val icono: ImageVector
) {
    SUCCESS(
        colorFondo = Color(0xFF28DC2B),
        colorTexto = Color(0xFF000000),
        icono = Icons.Default.CheckCircle
    ),
    ERROR(
        colorFondo = Color(0xFFE53935),
        colorTexto = Color(0xFFFFFFFF),
        icono = Icons.Default.Error
    ),
    WARNING(
        colorFondo = Color(0xFFFFE535),
        colorTexto = Color(0xFF000000),
        icono = Icons.Default.Warning
    )
}