package com.ahorro.familiar.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ahorro.familiar.data.model.Meta
import com.ahorro.familiar.ui.viewmodel.MetasViewModel

// Color morado principal de la app (coincide con el tema del mockup)
private val MoradoPrincipal = Color(0xFF6200EE)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ListaMetasScreen(
    viewModel: MetasViewModel,
    onMetaClick: (Int) -> Unit,
    onCrearMeta: () -> Unit
) {
    val metas     by viewModel.metas.collectAsState()
    val cargando  by viewModel.cargando.collectAsState()
    val error     by viewModel.error.collectAsState()
    val mensaje   by viewModel.mensaje.collectAsState()

    LaunchedEffect(Unit) { viewModel.cargarMetas() }

    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(error)   { error?.let   { snackbarHostState.showSnackbar(it); viewModel.limpiarError() } }
    LaunchedEffect(mensaje) { mensaje?.let { snackbarHostState.showSnackbar(it); viewModel.limpiarMensaje() } }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Savings,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                        Spacer(Modifier.width(8.dp))
                        Text(
                            text = "Ahorro Familiar",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MoradoPrincipal
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = onCrearMeta,
                icon = { Icon(Icons.Default.Add, contentDescription = "Nueva meta") },
                text = { Text("Nueva Meta") },
                containerColor = MoradoPrincipal,
                contentColor = Color.White
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            when {
                cargando -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                        color = MoradoPrincipal
                    )
                }
                metas.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.Savings,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = Color.LightGray
                        )
                        Spacer(Modifier.height(16.dp))
                        Text(
                            text = "No hay metas de ahorro aún",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                        Text(
                            text = "Crea una con el botón inferior",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.LightGray
                        )
                    }
                }
                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(metas) { meta ->
                            MetaCard(meta = meta, onClick = { onMetaClick(meta.id) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun MetaCard(meta: Meta, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column {
            // Imagen de la meta
            AsyncImage(
                model = meta.foto,
                contentDescription = meta.nombre,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(16.dp)) {
                // Nombre de la meta
                Text(
                    text = meta.nombre,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A)
                )

                Spacer(Modifier.height(4.dp))

                // Descripción si existe
                if (meta.descripcion.isNotBlank()) {
                    Text(
                        text = meta.descripcion,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // Valores financieros
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "Meta",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                        Text(
                            text = "$${String.format("%,.0f", meta.valorTotal)}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A1A1A)
                        )
                    }
                    Column(horizontalAlignment = Alignment.End) {
                        Text(
                            text = "Recaudado",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                        Text(
                            text = "$${String.format("%,.0f", meta.totalRecaudado)}",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = MoradoPrincipal
                        )
                    }
                }

                Spacer(Modifier.height(10.dp))

                // Barra de progreso
                LinearProgressIndicator(
                    progress = { (meta.porcentajeCumplido / 100f).coerceIn(0f, 1f) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = MoradoPrincipal,
                    trackColor = Color(0xFFE0E0E0)
                )

                Spacer(Modifier.height(6.dp))

                // Porcentaje y miembros
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "${meta.porcentajeCumplido}% completado",
                        style = MaterialTheme.typography.labelMedium,
                        color = MoradoPrincipal,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "${meta.miembros.size} miembro(s)",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Gray
                    )
                }
            }
        }
    }
}
