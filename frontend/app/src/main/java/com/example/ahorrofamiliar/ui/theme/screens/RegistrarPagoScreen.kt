package com.example.ahorrofamiliar.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ahorrofamiliar.ui.viewmodel.MetasViewModel

private val MoradoPrincipal = Color(0xFF6200EE)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegistrarPagoScreen(
    viewModel: MetasViewModel,
    metaId: Int,
    onVolver: () -> Unit
) {
    val detalleMeta by viewModel.detalleMeta.collectAsState()
    val error       by viewModel.error.collectAsState()
    val mensaje     by viewModel.mensaje.collectAsState()

    var miembroSeleccionadoIndex by remember { mutableStateOf(0) }
    var monto                    by remember { mutableStateOf("") }
    var descripcion              by remember { mutableStateOf("") }
    var expandedDropdown         by remember { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }
    val miembros = detalleMeta?.miembros ?: emptyList()

    LaunchedEffect(error)   { error?.let   { snackbarHostState.showSnackbar(it); viewModel.limpiarError() } }
    LaunchedEffect(mensaje) {
        mensaje?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.limpiarMensaje()
            onVolver()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Registrar Pago",
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onVolver) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MoradoPrincipal)
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(Modifier.height(4.dp))


            detalleMeta?.let { meta ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F0FF)),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text(
                            text = "Meta de ahorro",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Gray
                        )
                        Text(
                            text = meta.nombre,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MoradoPrincipal
                        )
                        Text(
                            text = "Recaudado: $${String.format("%,.0f", meta.totalRecaudado)} / $${String.format("%,.0f", meta.valorTotal)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.Gray
                        )
                    }
                }
            }

            Text(
                text = "Datos del pago",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )


            ExposedDropdownMenuBox(
                expanded = expandedDropdown,
                onExpandedChange = { expandedDropdown = it }
            ) {
                OutlinedTextField(
                    value = miembros.getOrNull(miembroSeleccionadoIndex)?.nombre ?: "Sin miembros",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Miembro que realiza el pago") },
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expandedDropdown) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    shape = RoundedCornerShape(10.dp)
                )
                ExposedDropdownMenu(
                    expanded = expandedDropdown,
                    onDismissRequest = { expandedDropdown = false }
                ) {
                    miembros.forEachIndexed { index, miembro ->
                        DropdownMenuItem(
                            text = { Text(miembro.nombre) },
                            onClick = {
                                miembroSeleccionadoIndex = index
                                expandedDropdown = false
                            }
                        )
                    }
                }
            }

            OutlinedTextField(
                value = monto,
                onValueChange = { monto = it },
                label = { Text("Monto del pago ($)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(10.dp)
            )

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción (opcional)") },
                placeholder = { Text("Ej: Cuota de mayo, Primer aporte...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    val montoDouble = monto.toDoubleOrNull() ?: 0.0
                    val miembroId = miembros.getOrNull(miembroSeleccionadoIndex)?.id ?: return@Button
                    viewModel.registrarPago(metaId, miembroId, montoDouble, descripcion)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MoradoPrincipal)
            ) {
                Text(
                    text = "💰 Guardar Pago",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
