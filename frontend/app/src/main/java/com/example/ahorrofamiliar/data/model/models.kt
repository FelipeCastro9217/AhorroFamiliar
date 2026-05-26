package com.ahorro.familiar.data.model

data class Miembro(
    val id: Int,
    val nombre: String,
    val totalAportado: Double = 0.0,
    val pagos: List<Pago> = emptyList()
)

data class Meta(
    val id: Int,
    val nombre: String,
    val descripcion: String,
    val valorTotal: Double,
    val foto: String,
    val miembros: List<Miembro>,
    val totalRecaudado: Double = 0.0,
    val porcentajeCumplido: Int = 0,
    val porcentajeFaltante: Int = 100,
    val aportesPorMiembro: List<Miembro> = emptyList()
)

data class Pago(
    val id: Int = 0,
    val metaId: Int,
    val miembroId: Int,
    val monto: Double,
    val fecha: String = "",
    val descripcion: String = "",
    val nombreMiembro: String = ""
)

// Request bodies
data class CrearMetaRequest(
    val nombre: String,
    val descripcion: String,
    val valorTotal: Double,
    val foto: String,
    val miembros: List<NombreMiembro>
)

data class NombreMiembro(val nombre: String)

data class AgregarMiembroRequest(val nombre: String)

data class CrearPagoRequest(
    val metaId: Int,
    val miembroId: Int,
    val monto: Double,
    val descripcion: String
)

