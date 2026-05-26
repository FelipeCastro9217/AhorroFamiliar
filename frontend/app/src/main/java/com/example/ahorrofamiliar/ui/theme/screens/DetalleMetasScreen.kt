package com.ahorro.familiar.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PersonAdd
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
import com.ahorro.familiar.data.model.Miembro
import com.ahorro.familiar.data.model.Pago
import com.ahorro.familiar.ui.viewmodel.MetasViewModel

private val MoradoPrincipal = Color(0xFF6200EE)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetalleMetaScreen(
    viewModel: MetasViewModel,
    metaId: Int,
    onRegistrarPago: () -> Unit,
    onVolver: () -> Unit
) {
    val detalleMeta by viewModel.detalleMeta.collectAsState()
    val pagos       by viewModel.pagos.collectAsState()
    val cargando    by viewModel.cargando.collectAsState()
    val error       by viewModel.error.collectAsState()
    val mensaje     by viewModel.mensaje.collectAsState()

    var mostrarDialogoMiembro by remember { mutableStateOf(false) }
    var nombreMiembro         by remember { mutableStateOf("") }
    val snackbarHostState     = remember { SnackbarHostState() }

    LaunchedEffect(metaId) {
        viewModel.cargarDetalleMeta(metaId)
        viewModel.cargarPagosDeMeta(metaId)
    }
    LaunchedEffect(error)   { error?.let   { snackbarHostState.showSnackbar(it); viewModel.limpiarError() } }
    LaunchedEffect(mensaje) { mensaje?.let { snackbarHostState.showSnackbar(it); viewModel.limpiarMensaje() } }


    if (mostrarDialogoMiembro) {
        AlertDialog(
            onDismissRequest = { mostrarDialogoMiembro = false; nombreMiembro = "" },
            title = { Text("Agregar miembro", fontWeight = FontWeight.Bold) },
            text = {
                OutlinedTextField(
                    value = nombreMiembro,
                    onValueChange = { nombreMiembro = it },
                    label = { Text("Nombre del miembro") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.agregarMiembro(metaId, nombreMiembro)
                        nombreMiembro = ""
                        mostrarDialogoMiembro = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MoradoPrincipal)
                ) { Text("Agregar") }
            },
            dismissButton = {
                TextButton(onClick = { mostrarDialogoMiembro = false; nombreMiembro = "" }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = detalleMeta?.nombre ?: "Detalle de Meta",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { mostrarDialogoMiembro = true }) {
                        Icon(Icons.Default.PersonAdd, contentDescription = "Agregar miembro", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MoradoPrincipal)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->

        if (cargando && detalleMeta == null) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = MoradoPrincipal)
            }
            return@Scaffold
        }

        detalleMeta?.let { meta ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {

                AsyncImage(
                    model = meta.foto,
                    contentDescription = meta.nombre,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp),
                    contentScale = ContentScale.Crop
                )

                Column(modifier = Modifier.padding(16.dp)) {


                    Text(
                        text = meta.nombre,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )
                    if (meta.descripcion.isNotBlank()) {
                        Text(
                            text = meta.descripcion,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.Gray,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Spacer(Modifier.height(16.dp))


                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F0FF)),
                        elevation = CardDefaults.cardElevation(0.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Valor total: $${String.format("%,.0f", meta.valorTotal)}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A1A1A)
                            )
                            Text(
                                text = "Recaudado: $${String.format("%,.0f", meta.totalRecaudado)}",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MoradoPrincipal,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                            Text(
                                text = "Faltante: $${String.format("%,.0f", meta.valorTotal - meta.totalRecaudado)}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 2.dp)
                            )

                            Spacer(Modifier.height(12.dp))

                            LinearProgressIndicator(
                                progress = { (meta.porcentajeCumplido / 100f).coerceIn(0f, 1f) },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(10.dp)
                                    .clip(RoundedCornerShape(5.dp)),
                                color = MoradoPrincipal,
                                trackColor = Color(0xFFE0E0E0)
                            )

                            Spacer(Modifier.height(6.dp))

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
                                    text = "Falta: ${meta.porcentajeFaltante}%",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = Color.Gray
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(16.dp))


                    Button(
                        onClick = onRegistrarPago,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MoradoPrincipal)
                    ) {
                        Text(
                            text = "💰 Registrar Pago",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    Spacer(Modifier.height(24.dp))


                    Text(
                        text = "👥 Miembros y aportes",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )
                    Spacer(Modifier.height(8.dp))

                    val miembrosConAportes = meta.aportesPorMiembro.ifEmpty { meta.miembros }
                    miembrosConAportes.forEach { miembro ->
                        MiembroItem(miembro)
                        Spacer(Modifier.height(6.dp))
                    }

                    Spacer(Modifier.height(20.dp))


                    Text(
                        text = "📋 Historial de pagos",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A1A1A)
                    )
                    Spacer(Modifier.height(8.dp))

                    if (pagos.isEmpty()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "Aún no hay pagos registrados.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.Gray
                            )
                        }
                    } else {
                        pagos.forEach { pago ->
                            PagoItem(pago)
                            Spacer(Modifier.height(6.dp))
                        }
                    }

                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
fun MiembroItem(miembro: Miembro) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE8D5FF)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = miembro.nombre.firstOrNull()?.uppercase() ?: "?",
                    fontWeight = FontWeight.Bold,
                    color = MoradoPrincipal,
                    fontSize = 18.sp
                )
            }

            Spacer(Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = miembro.nombre,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF1A1A1A)
                )
                Text(
                    text = "Aportado: $${String.format("%,.0f", miembro.totalAportado)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MoradoPrincipal
                )
            }
        }
    }
}

@Composable
fun PagoItem(pago: Pago) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(10.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = pago.nombreMiembro.ifBlank { "Miembro #${pago.miembroId}" },
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF1A1A1A)
                )
                if (pago.descripcion.isNotBlank() && pago.descripcion != "Sin descripción") {
                    Text(
                        text = pago.descripcion,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
                Text(
                    text = pago.fecha,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.LightGray
                )
            }
            Text(
                text = "$${String.format("%,.0f", pago.monto)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MoradoPrincipal
            )
        }
    }
}