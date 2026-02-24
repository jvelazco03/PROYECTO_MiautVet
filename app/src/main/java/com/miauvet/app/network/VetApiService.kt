package com.miauvet.app.network

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface VetApiService {

    @GET("veterinarios")
    suspend fun obtenerVeterinarios(): List<VeterinarioApi>

    @GET("pacientes")
    suspend fun obtenerPacientes(): List<PacienteApi>

    @GET("veterinarios/{id}")
    suspend fun obtenerVeterinarioPorId(@Path("id") id: String): VeterinarioApi

    @GET("pacientes/{id}")
    suspend fun obtenerPacientePorId(@Path("id") id: String): PacienteApi

    @POST("veterinarios")
    suspend fun registrarVeterinario(@Body vet: VeterinarioApi): VeterinarioApi

    @POST("pacientes")
    suspend fun registrarPaciente(@Body paciente: PacienteApi): PacienteApi

    @PUT("veterinarios/{id}")
    suspend fun actualizarVeterinario(@Path("id") id: String, @Body vet: VeterinarioApi): VeterinarioApi

    @PUT("pacientes/{id}")
    suspend fun actualizarPaciente(@Path("id") id: String, @Body paciente: PacienteApi): PacienteApi

    @DELETE("veterinarios/{id}")
    suspend fun eliminarVeterinario(@Path("id") id: String): VeterinarioApi

    @DELETE("pacientes/{id}")
    suspend fun eliminarPaciente(@Path("id") id: String): PacienteApi
}