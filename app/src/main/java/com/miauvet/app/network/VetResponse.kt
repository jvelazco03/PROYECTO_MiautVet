package com.miauvet.app.network

import com.google.gson.annotations.SerializedName

data class VeterinarioApi(
    @SerializedName("id") val id: String,
    @SerializedName("nombres") val nombres: String,
    @SerializedName("apellidos") val apellidos: String,
    @SerializedName("especialidad") val especialidad: String,
    @SerializedName("estado") val estado: String,
    @SerializedName("fecha_ingreso") val fechaIngreso: String,
    @SerializedName("foto") val foto: String
)

data class PacienteApi(
    @SerializedName("id") val id: String,
    @SerializedName("nombre") val nombre: String,
    @SerializedName("raza") val raza: String,
    @SerializedName("sexo") val sexo: String,
    @SerializedName("foto") val foto: String
)