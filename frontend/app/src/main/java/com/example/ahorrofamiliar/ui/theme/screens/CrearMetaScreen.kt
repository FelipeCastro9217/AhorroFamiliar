package com.ahorro.familiar.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import com.ahorro.familiar.ui.viewmodel.MetasViewModel

private val MoradoPrincipal = Color(0xFF6200EE)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CrearMetaScreen(
    viewModel: MetasViewModel,
    onVolver: () -> Unit
) {
    val error   by viewModel.error.collectAsState()
    val mensaje by viewModel.mensaje.collectAsState()

    var nombre       by remember { mutableStateOf("") }
    var descripcion  by remember { mutableStateOf("") }
    var valorTotal   by remember { mutableStateOf("") }
    var fotoUrl      by remember { mutableStateOf("") }
    var miembrosTexto by remember { mutableStateOf("") }

    val snackbarHostState = remember { SnackbarHostState() }

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
                        text = "Nueva Meta de Ahorro",
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
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Spacer(Modifier.height(4.dp))

            Text(
                text = "Información del producto",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )

            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre del producto") },
                placeholder = { Text("Ej: Televisor, Moto, Computador...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(10.dp)
            )

            OutlinedTextField(
                value = descripcion,
                onValueChange = { descripcion = it },
                label = { Text("Descripción (opcional)") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(10.dp)
            )

            OutlinedTextField(
                value = valorTotal,
                onValueChange = { valorTotal = it },
                label = { Text("Valor total ($)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(10.dp)
            )

            OutlinedTextField(
                value = fotoUrl,
                onValueChange = { fotoUrl = it },
                label = { Text("URL de la foto (opcional)") },
                placeholder = { Text("https://...") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(10.dp)
            )

            Divider(modifier = Modifier.padding(vertical = 4.dp))

            Text(
                text = "Miembros del ahorro",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF1A1A1A)
            )

            OutlinedTextField(
                value = miembrosTexto,
                onValueChange = { miembrosTexto = it },
                label = { Text("Miembros separados por coma") },
                placeholder = { Text("Ej: Juan, María, Carlos") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(10.dp)
            )

            Spacer(Modifier.height(8.dp))

            Button(
                onClick = {
                    val valor = valorTotal.toDoubleOrNull() ?: 0.0
                    val miembros = miembrosTexto
                        .split(",")
                        .map { it.trim() }
                        .filter { it.isNotBlank() }
                    viewModel.crearMeta(nombre, descripcion, valor, fotoUrl, miembros)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                shape = RoundedCornerShape(10.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MoradoPrincipal)
            ) {
                Text(
                    text = "✅ Crear Meta de Ahorro",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(16.dp))
        }
    }
}
