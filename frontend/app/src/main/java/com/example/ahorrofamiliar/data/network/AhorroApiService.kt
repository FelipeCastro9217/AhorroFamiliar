package com.example.ahorrofamiliar.data.network

import com.example.ahorrofamiliar.data.model.*
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*

interface AhorroApiService {
    @GET("metas")
    suspend fun getMetas(): Response<List<Meta>>
    @GET("metas/{id}")
    suspend fun getDetalleMeta(
        @Path("id") id: Int): Response<Meta>
    @POST("metas")
    suspend fun crearMeta(
        @Body request: CrearMetaRequest): Response<Meta>
    @POST("metas/{id}/miembros")
    suspend fun agregarMiembro(
        @Path("id") metaId: Int,
        @Body request: AgregarMiembroRequest
    ): Response<Miembro>

    @GET("metas/{id}/pagos")
    suspend fun getPagosDeMeta(
        @Path("id") metaId: Int): Response<List<Pago>>
    @POST("pagos")
    suspend fun registrarPago(
        @Body request: CrearPagoRequest): Response<Pago>
    companion object {
        private const val BASE_URL = "http://10.0.2.2:3000/"

        fun create(): AhorroApiService {
            return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(AhorroApiService::class.java)
        }
    }
}
