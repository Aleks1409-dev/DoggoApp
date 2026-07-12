package com.grupo06.doggoapp.presentation.screens.programarCita

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grupo06.doggoapp.domain.model.Servicio
import com.grupo06.doggoapp.domain.model.Slot

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun ProgramarCitaScreen(
    viewModel: ProgramarCitaViewModel,
    onVolver: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(modifier = Modifier.fillMaxSize()) {
        TopAppBar(
            title = { Text("Programar cita") },
            navigationIcon = {
                IconButton(onClick = onVolver) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
        )

        when (val estado = uiState.estado) {
            is ProgramarCitaEstado.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF10B981))
                }
            }

            is ProgramarCitaEstado.Error -> {
                ErrorContent(
                    mensaje = estado.mensaje,
                    onReintentar = viewModel::reintentar
                )
            }

            is ProgramarCitaEstado.Exito -> {
                ExitoContent(
                    mensaje = estado.mensaje,
                    onVolver = onVolver
                )
            }

            is ProgramarCitaEstado.Confirmando,
            is ProgramarCitaEstado.DisponibilidadCargada -> {
                val slots = if (estado is ProgramarCitaEstado.DisponibilidadCargada) {
                    estado.slots
                } else {
                    emptyList()
                }
                val servicios = if (estado is ProgramarCitaEstado.DisponibilidadCargada) {
                    estado.serviciosDelCuidador
                } else {
                    emptyList()
                }
                val confirmando = estado is ProgramarCitaEstado.Confirmando

                FormularioReserva(
                    nombreCuidador = uiState.nombreCuidador,
                    slots = slots,
                    servicios = servicios,
                    servicioSeleccionado = uiState.servicioSeleccionado,
                    fechaSeleccionada = uiState.fechaSeleccionada,
                    rangoSeleccionado = uiState.rangoSeleccionado,
                    confirmando = confirmando,
                    onServicioSeleccionado = viewModel::seleccionarServicio,
                    onFechaSeleccionada = viewModel::seleccionarFecha,
                    onRangoSeleccionado = viewModel::seleccionarRango,
                    onConfirmar = viewModel::confirmarCita
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun FormularioReserva(
    nombreCuidador: String,
    slots: List<Slot>,
    servicios: List<Servicio>,
    servicioSeleccionado: Servicio?,
    fechaSeleccionada: String?,
    rangoSeleccionado: String?,
    confirmando: Boolean,
    onServicioSeleccionado: (Servicio?) -> Unit,
    onFechaSeleccionada: (String?) -> Unit,
    onRangoSeleccionado: (String?) -> Unit,
    onConfirmar: () -> Unit
) {
    val scrollState = rememberScrollState()
    val colorVerde = Color(0xFF10B981)

    val fechasDisponibles = remember(slots) { slots.fechasUnicas() }
    val rangosDisponibles = slots
        .filter { it.fecha == fechaSeleccionada }
        .map { it.rango }
        .distinct()
        .sorted()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            text = "Programar cita",
            fontWeight = FontWeight.Bold,
            fontSize = 24.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "con ${nombreCuidador.ifBlank { "el cuidador" }}",
            color = Color.Gray,
            fontSize = 16.sp
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "SERVICIO",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (servicios.isEmpty()) {
            Text(
                text = "Este cuidador no tiene servicios registrados.",
                color = Color.Gray,
                fontSize = 14.sp
            )
        } else {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                servicios.forEach { servicio ->
                    SeleccionChip(
                        texto = servicio.titulo,
                        seleccionado = servicio == servicioSeleccionado,
                        onClick = {
                            onServicioSeleccionado(
                                if (servicio == servicioSeleccionado) null else servicio
                            )
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "FECHA",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (slots.isEmpty()) {
            Text(
                text = "No hay disponibilidad para este cuidador.",
                color = Color.Gray,
                fontSize = 14.sp
            )
        } else {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                fechasDisponibles.forEach { fecha: String ->
                    SeleccionChip(
                        texto = formatearFecha(fecha),
                        seleccionado = fecha == fechaSeleccionada,
                        onClick = {
                            onFechaSeleccionada(
                                if (fecha == fechaSeleccionada) null else fecha
                            )
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "HORA",
            fontWeight = FontWeight.Bold,
            fontSize = 12.sp,
            color = Color.Gray
        )
        Spacer(modifier = Modifier.height(8.dp))
        if (fechaSeleccionada == null) {
            Text(
                text = "Selecciona una fecha para ver los horarios.",
                color = Color.Gray,
                fontSize = 14.sp
            )
        } else if (rangosDisponibles.isEmpty()) {
            Text(
                text = "No hay rangos disponibles para la fecha seleccionada.",
                color = Color.Gray,
                fontSize = 14.sp
            )
        } else {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                rangosDisponibles.forEach { rango ->
                    SeleccionChip(
                        texto = rango,
                        seleccionado = rango == rangoSeleccionado,
                        onClick = {
                            onRangoSeleccionado(
                                if (rango == rangoSeleccionado) null else rango
                            )
                        }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        val precio = servicioSeleccionado?.precio ?: 0.0
        ResumenPrecio(precio = precio)

        Spacer(modifier = Modifier.height(24.dp))

        val puedeConfirmar = servicioSeleccionado != null &&
                fechaSeleccionada != null &&
                rangoSeleccionado != null &&
                !confirmando

        Button(
            onClick = onConfirmar,
            enabled = puedeConfirmar,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorVerde,
                disabledContainerColor = Color.LightGray
            )
        ) {
            Text(
                text = if (confirmando) {
                    "Confirmando..."
                } else {
                    "Confirmar cita · S/ %.2f".format(precio)
                },
                fontSize = 16.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SeleccionChip(
    texto: String,
    seleccionado: Boolean,
    onClick: () -> Unit
) {
    val colorVerde = Color(0xFF10B981)

    FilterChip(
        selected = seleccionado,
        onClick = onClick,
        label = { Text(texto) },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = colorVerde.copy(alpha = 0.15f),
            selectedLabelColor = colorVerde,
            selectedLeadingIconColor = colorVerde
        ),
        border = FilterChipDefaults.filterChipBorder(
            enabled = true,
            selected = seleccionado,
            borderColor = if (seleccionado) colorVerde else Color.LightGray
        )
    )
}

@Composable
private fun ResumenPrecio(precio: Double) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Subtotal", fontSize = 14.sp, color = Color.Gray)
            Text(
                text = "S/ %.2f x 1".format(precio),
                fontSize = 14.sp,
                color = Color.Gray
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Total",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "S/ %.2f".format(precio),
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF10B981)
            )
        }
    }
}

@Composable
private fun ErrorContent(
    mensaje: String,
    onReintentar: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = mensaje,
                color = Color.Gray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onReintentar) {
                Text("Reintentar")
            }
        }
    }
}

@Composable
private fun ExitoContent(
    mensaje: String,
    onVolver: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = mensaje,
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 32.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onVolver) {
                Text("Volver")
            }
        }
    }
}

private fun List<Slot>.fechasUnicas(): List<String> {
    return map { it.fecha }.distinct().sorted()
}

private fun formatearFecha(fecha: String): String {
    // Formato esperado: YYYY-MM-DD
    val partes = fecha.split("-")
    return if (partes.size == 3) {
        "${partes[2]}/${partes[1]}/${partes[0]}"
    } else {
        fecha
    }
}
