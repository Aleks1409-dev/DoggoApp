package com.grupo06.doggoapp.presentation.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.ui.graphics.vector.ImageVector

sealed class BotonNavItem(val ruta: String, val titulo: String, val icono: ImageVector) {
    data object Inicio : BotonNavItem(NavRutas.INICIO, "Inicio", Icons.Default.Home)
    data object Agenda : BotonNavItem(NavRutas.AGENDA, "Agenda", Icons.Default.CalendarMonth)
    data object Mensajes : BotonNavItem(NavRutas.MENSAJES, "Mensajes", Icons.Outlined.ChatBubbleOutline)
    data object Perfil : BotonNavItem(NavRutas.PERFIL, "Perfil", Icons.Outlined.PersonOutline)
}