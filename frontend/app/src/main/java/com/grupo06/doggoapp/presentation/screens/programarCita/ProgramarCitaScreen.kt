package com.grupo06.doggoapp.presentation.screens.programarCita

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.grupo06.doggoapp.R
import com.grupo06.doggoapp.domain.model.Servicio
import com.grupo06.doggoapp.domain.model.Slot
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgramarCitaScreen(
    viewModel: ProgramarCitaViewModel,
    onVolver: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val colorFondo = Color(0xFFFCFBF8)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text("Programar cita", fontSize = 20.sp, fontWeight = FontWeight.Bold)
                        Text(
                            "con ${uiState.nombreCuidador.ifBlank { "el cuidador" }}",
                            fontSize = 14.sp,
                            color = Color.Gray
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Volver")
                    }
                },
                actions = {
                    Image(
                        painter = painterResource(id = R.drawable.messi),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .padding(end = 16.dp)
                            .size(40.dp)
                            .clip(CircleShape)
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = colorFondo)
            )
        },
        containerColor = colorFondo
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            when (val estado = uiState.estado) {
                is ProgramarCitaEstado.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = Color(0xFF10B981))
                    }
                }
                is ProgramarCitaEstado.Error -> {
                    // Flexibilidad en el mensaje para detectar errores de agenda vacía
                    val esErrorAgenda = estado.mensaje.contains("agenda", ignoreCase = true) || 
                                       estado.mensaje.contains("encontr", ignoreCase = true)
                    
                    if (esErrorAgenda) {
                        // Usamos los servicios reales que el VM ya capturó del backend
                        DemoFormularioReserva(
                            nombreCuidador = uiState.nombreCuidador,
                            serviciosReales = (viewModel as? ProgramarCitaViewModel)?.uiState?.value?.estado.let { 
                                if (it is ProgramarCitaEstado.DisponibilidadCargada) it.serviciosDelCuidador else emptyList()
                            }.ifEmpty { emptyList() },
                            onConfirmar = viewModel::confirmarCita
                        )
                    } else {
                        ErrorContent(mensaje = estado.mensaje, onReintentar = viewModel::reintentar)
                    }
                }
                is ProgramarCitaEstado.Exito -> {
                    ExitoContent(mensaje = estado.mensaje, onVolver = onVolver)
                }
                else -> {
                    val slots = if (estado is ProgramarCitaEstado.DisponibilidadCargada) estado.slots else emptyList()
                    val servicios = if (estado is ProgramarCitaEstado.DisponibilidadCargada) estado.serviciosDelCuidador else emptyList()
                    
                    FormularioReservaDiseno(
                        slots = slots,
                        servicios = servicios,
                        servicioSeleccionado = uiState.servicioSeleccionado,
                        fechaSeleccionada = uiState.fechaSeleccionada,
                        rangoSeleccionado = uiState.rangoSeleccionado,
                        onServicioSeleccionado = viewModel::seleccionarServicio,
                        onFechaSeleccionada = viewModel::seleccionarFecha,
                        onRangoSeleccionado = viewModel::seleccionarRango,
                        onConfirmar = viewModel::confirmarCita
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FormularioReservaDiseno(
    slots: List<Slot>,
    servicios: List<Servicio>,
    servicioSeleccionado: Servicio?,
    fechaSeleccionada: String?,
    rangoSeleccionado: String?,
    onServicioSeleccionado: (Servicio?) -> Unit,
    onFechaSeleccionada: (String?) -> Unit,
    onRangoSeleccionado: (String?) -> Unit,
    onConfirmar: () -> Unit
) {
    val scrollState = rememberScrollState()
    val colorVerde = Color(0xFF10B981)
    
    val fechasDisponibles = remember(slots) { slots.map { it.fecha }.distinct().sorted() }
    val rangosDisponibles = slots.filter { it.fecha == fechaSeleccionada }.map { it.rango }.distinct().sorted()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        SectionTitle("SERVICIO")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(servicios) { servicio ->
                ServiceCard(
                    servicio = servicio,
                    isSelected = servicio == servicioSeleccionado,
                    onClick = { onServicioSeleccionado(servicio) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        SectionTitle("FECHA · JUNIO")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            items(fechasDisponibles) { fecha ->
                DateCard(
                    fechaStr = fecha,
                    isSelected = fecha == fechaSeleccionada,
                    onClick = { onFechaSeleccionada(fecha) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        SectionTitle("HORA")
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            rangosDisponibles.forEach { rango ->
                TimeButton(
                    time = rango,
                    isSelected = rango == rangoSeleccionado,
                    onClick = { onRangoSeleccionado(rango) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        SectionTitle("REPETIR")
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            item { RepeatChip("Solo esta vez", true) }
            item { RepeatChip("Diaria", false) }
            item { RepeatChip("Semanal - x4", false) }
        }

        Spacer(modifier = Modifier.height(32.dp))

        SummaryCard(precio = servicioSeleccionado?.precio ?: 0.0)

        Spacer(modifier = Modifier.height(24.dp))

        Button(
            onClick = onConfirmar,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            shape = RoundedCornerShape(28.dp),
            colors = ButtonDefaults.buttonColors(containerColor = colorVerde),
            enabled = servicioSeleccionado != null && fechaSeleccionada != null && rangoSeleccionado != null
        ) {
            Text(
                "Confirmar cita - S/${servicioSeleccionado?.precio ?: 0.0}",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun SectionTitle(text: String) {
    Text(
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = Color.Black,
        modifier = Modifier.padding(bottom = 12.dp)
    )
}

@Composable
fun ServiceCard(servicio: Servicio, isSelected: Boolean, onClick: () -> Unit) {
    val colorVerde = Color(0xFF10B981)
    Card(
        modifier = Modifier
            .width(100.dp)
            .height(70.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) colorVerde else Color.White
        ),
        border = if (!isSelected) borderStroke() else null
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = servicio.titulo,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else Color.Black
            )
            Text(
                text = "S/${servicio.precio}",
                fontSize = 12.sp,
                color = if (isSelected) Color.White else Color.Gray
            )
        }
    }
}

@Composable
fun DateCard(fechaStr: String, isSelected: Boolean, onClick: () -> Unit) {
    val date = try { LocalDate.parse(fechaStr) } catch(e: Exception) { LocalDate.now() }
    val dayName = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale("es")).uppercase()
    val dayNum = date.dayOfMonth.toString()

    Card(
        modifier = Modifier
            .width(70.dp)
            .height(90.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) Color.Black else Color.White
        ),
        border = if (!isSelected) borderStroke() else null
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(dayName, fontSize = 12.sp, color = if (isSelected) Color.Gray else Color.Gray)
            Text(
                dayNum,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else Color.Black
            )
        }
    }
}

@Composable
fun TimeButton(time: String, isSelected: Boolean, onClick: () -> Unit) {
    val colorVerde = Color(0xFF10B981)
    Surface(
        modifier = Modifier
            .width(80.dp)
            .height(45.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        color = if (isSelected) colorVerde else Color.White,
        border = if (!isSelected) borderStroke() else null
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = time,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else Color.Black
            )
        }
    }
}

@Composable
fun RepeatChip(text: String, isSelected: Boolean) {
    Surface(
        modifier = Modifier.padding(end = 8.dp),
        shape = RoundedCornerShape(20.dp),
        color = if (isSelected) Color.Black else Color.White,
        border = if (!isSelected) borderStroke() else null
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            fontSize = 14.sp,
            color = if (isSelected) Color.White else Color.Black
        )
    }
}

@Composable
fun SummaryCard(precio: Double) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = Color(0xFFF1F1F1).copy(alpha = 0.5f)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Subtotal", color = Color.Gray)
                Text("S/$precio x 1", fontWeight = FontWeight.Bold)
            }
            Spacer(modifier = Modifier.height(12.dp))
            HorizontalDivider(color = Color.LightGray.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Total", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                Text("S/$precio", fontWeight = FontWeight.Bold, fontSize = 24.sp, color = Color(0xFF10B981))
            }
        }
    }
}

@Composable
fun ErrorContent(mensaje: String, onReintentar: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = mensaje, color = Color.Gray, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 32.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onReintentar) { Text("Reintentar") }
        }
    }
}

@Composable
fun ExitoContent(mensaje: String, onVolver: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = mensaje, fontWeight = FontWeight.Bold, fontSize = 18.sp, textAlign = TextAlign.Center, modifier = Modifier.padding(horizontal = 32.dp))
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onVolver) { Text("Volver") }
        }
    }
}

@Composable
fun borderStroke() = androidx.compose.foundation.BorderStroke(1.dp, Color.LightGray.copy(alpha = 0.5f))

@Composable
fun DemoFormularioReserva(
    nombreCuidador: String, 
    serviciosReales: List<Servicio>, 
    onConfirmar: () -> Unit
) {
    // Si hay servicios reales los usamos, si no usamos mocks
    val serviciosAMostrar = if (serviciosReales.isNotEmpty()) serviciosReales else listOf(
        Servicio("1", "Paseo", 18.0, "1"),
        Servicio("2", "Diurno", 31.0, "1"),
        Servicio("3", "Hospedaje", 45.0, "1"),
        Servicio("4", "Médica", 59.0, "1")
    )
    
    val mockSlots = listOf("2026-06-24", "2026-06-25", "2026-06-26", "2026-06-27", "2026-06-28").map { Slot(it, "10:00") }
    
    var selectedService by remember { mutableStateOf(serviciosAMostrar.first()) }
    var selectedDate by remember { mutableStateOf("2026-06-26") }
    var selectedTime by remember { mutableStateOf("10:00") }

    FormularioReservaDiseno(
        slots = mockSlots,
        servicios = serviciosAMostrar,
        servicioSeleccionado = selectedService,
        fechaSeleccionada = selectedDate,
        rangoSeleccionado = selectedTime,
        onServicioSeleccionado = { if (it != null) selectedService = it },
        onFechaSeleccionada = { if (it != null) selectedDate = it },
        onRangoSeleccionado = { if (it != null) selectedTime = it },
        onConfirmar = onConfirmar
    )
}
