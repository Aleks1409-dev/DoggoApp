package com.grupo06.doggoapp.presentation.screens.inicio

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.grupo06.doggoapp.domain.model.Cuidador

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InicioScreen(
    viewModel: InicioViewModel,
    onCuidadorClick: (String) -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val filtros = uiState.filtros
    val colorFondo = Color(0xFFFCFBF8)
    val colorVerde = Color(0xFF10B981)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(colorFondo)
            .padding(horizontal = 20.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = "Ubicación",
                        tint = Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Miraflores, Lima", color = Color.Gray, fontSize = 14.sp)
                }
                Text(
                    text = "Cuidadores cercanos",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
            }
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .clip(CircleShape)
                    .background(colorVerde.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Text("P", color = colorVerde, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = filtros.busqueda,
                onValueChange = viewModel::buscar,
                placeholder = { Text("Buscar cuidador...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar"
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedIndicatorColor = colorVerde,
                    unfocusedIndicatorColor = Color.LightGray,
                    cursorColor = colorVerde
                ),
                modifier = Modifier
                    .weight(1f)
                    .height(52.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            IconButtonWithBackground(
                onClick = { viewModel.ordenarPorPrecio(!filtros.ordenAscendente) },
                content = {
                    Icon(
                        imageVector = if (filtros.ordenAscendente) {
                            Icons.Default.KeyboardArrowUp
                        } else {
                            Icons.Default.KeyboardArrowDown
                        },
                        contentDescription = if (filtros.ordenAscendente) {
                            "Ordenar de menor a mayor precio"
                        } else {
                            "Ordenar de mayor a menor precio"
                        },
                        tint = Color.Black
                    )
                }
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        val opcionesServicios = listOf("Todos") + uiState.serviciosDisponibles
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(opcionesServicios, key = { it }) { opcion ->
                val seleccionado = when (opcion) {
                    "Todos" -> filtros.servicioSeleccionado == null
                    else -> filtros.servicioSeleccionado == opcion
                }
                FilterChip(
                    selected = seleccionado,
                    onClick = {
                        viewModel.filtrarPorServicio(
                            if (opcion == "Todos") null else opcion
                        )
                    },
                    label = { Text(opcion) }
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentAlignment = Alignment.Center
        ) {
            when (val estado = uiState.estado) {
                is InicioEstado.Loading -> {
                    CircularProgressIndicator(color = colorVerde)
                }

                is InicioEstado.Empty -> {
                    MensajeCentrado(
                        icono = {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = null,
                                tint = Color.Gray,
                                modifier = Modifier.size(48.dp)
                            )
                        },
                        titulo = "No hay resultados",
                        subtitulo = "Prueba con otro texto de búsqueda o cambia los filtros."
                    )
                }

                is InicioEstado.Error -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = estado.mensaje,
                            color = Color.Gray,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 32.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = viewModel::reintentar) {
                            Text("Reintentar")
                        }
                    }
                }

                is InicioEstado.Success -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(bottom = 80.dp)
                    ) {
                        items(
                            items = estado.cuidadores,
                            key = { it.id }
                        ) { cuidador ->
                            CuidadorCardReal(
                                cuidador = cuidador,
                                onClick = onCuidadorClick
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun IconButtonWithBackground(
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    Box(
        modifier = Modifier
            .size(52.dp)
            .background(Color.White, RoundedCornerShape(24.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

@Composable
private fun MensajeCentrado(
    icono: @Composable () -> Unit,
    titulo: String,
    subtitulo: String
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        icono()
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = titulo,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = subtitulo,
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
    }
}

@Composable
fun CuidadorCardReal(
    cuidador: Cuidador,
    onClick: (String) -> Unit = {}
) {
    val imagen = cuidador.fotoResId ?: R.drawable.messi
    val colorVerde = Color(0xFF10B981)

    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(cuidador.id) }
    ) {
        Row(modifier = Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .size(110.dp)
                    .clip(RoundedCornerShape(16.dp))
            ) {
                Image(
                    painter = painterResource(id = imagen),
                    contentDescription = "Foto de ${cuidador.nombre}",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Surface(
                    shape = CircleShape,
                    color = Color.White.copy(alpha = 0.8f),
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(28.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.FavoriteBorder,
                            contentDescription = "Favorito",
                            modifier = Modifier.size(16.dp),
                            tint = colorVerde
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = cuidador.nombre,
                    fontWeight = FontWeight.Bold,
                    fontSize = 18.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = Color.Gray,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = cuidador.ubicacion,
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        tint = Color(0xFFFFC107),
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = " ${cuidador.rating} ",
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "(${cuidador.servicios.size} servicios)",
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
                Spacer(modifier = Modifier.height(6.dp))

                val especialidadesTexto = cuidador.especialidades
                    .joinToString(
                        ", ",
                        prefix = "",
                        postfix = ""
                    )
                    .takeIf { it.isNotBlank() }
                    ?: "Sin especialidades registradas"

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFD4E6F1)
                ) {
                    Text(
                        text = especialidadesTexto,
                        color = Color.Black,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = cuidador.tipo,
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                    Text(
                        text = if (cuidador.tarifaMinima != null) {
                            "S/ %.2f".format(cuidador.tarifaMinima)
                        } else {
                            "Tarifa no disponible"
                        },
                        color = colorVerde,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }
            }
        }
    }
}
