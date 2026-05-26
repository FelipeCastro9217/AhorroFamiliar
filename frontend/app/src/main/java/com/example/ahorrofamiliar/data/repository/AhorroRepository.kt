package com.ahorro.familiar.data.repository

import com.ahorro.familiar.data.model.*
import com.ahorro.familiar.data.network.AhorroApiService

class AhorroRepository {

    private val api = AhorroApiService.create()

    // ── METAS ────────────────────────────────────────────────────────────────

    suspend fun getMetas(): Result<List<Meta>> = try {
        val r = api.getMetas()
        if (r.isSuccessful && r.body() != null) Result.success(r.body()!!)
        else Result.failure(Exception("Error ${r.code()}: no se pudo cargar la lista de metas"))
    } catch (e: Exception) { Result.failure(e) }

    suspend fun getDetalleMeta(id: Int): Result<Meta> = try {
        val r = api.getDetalleMeta(id)
        if (r.isSuccessful && r.body() != null) Result.success(r.body()!!)
        else Result.failure(Exception("Meta no encontrada (código ${r.code()})"))
    } catch (e: Exception) { Result.failure(e) }

    suspend fun crearMeta(request: CrearMetaRequest): Result<Meta> = try {
        val r = api.crearMeta(request)
        if (r.isSuccessful && r.body() != null) Result.success(r.body()!!)
        else Result.failure(Exception("Error al crear meta (código ${r.code()})"))
    } catch (e: Exception) { Result.failure(e) }

    suspend fun agregarMiembro(metaId: Int, nombre: String): Result<Miembro> = try {
        val r = api.agregarMiembro(metaId, AgregarMiembroRequest(nombre))
        if (r.isSuccessful && r.body() != null) Result.success(r.body()!!)
        else Result.failure(Exception("Error al agregar miembro (código ${r.code()})"))
    } catch (e: Exception) { Result.failure(e) }

    // ── PAGOS ────────────────────────────────────────────────────────────────

    suspend fun getPagosDeMeta(metaId: Int): Result<List<Pago>> = try {
        val r = api.getPagosDeMeta(metaId)
        if (r.isSuccessful && r.body() != null) Result.success(r.body()!!)
        else Result.failure(Exception("Error al obtener pagos (código ${r.code()})"))
    } catch (e: Exception) { Result.failure(e) }

    suspend fun registrarPago(request: CrearPagoRequest): Result<Pago> = try {
        val r = api.registrarPago(request)
        if (r.isSuccessful && r.body() != null) Result.success(r.body()!!)
        else Result.failure(Exception("Error al registrar pago (código ${r.code()})"))
    } catch (e: Exception) { Result.failure(e) }
}
