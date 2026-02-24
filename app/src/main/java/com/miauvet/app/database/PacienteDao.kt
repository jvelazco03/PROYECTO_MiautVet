package com.miauvet.app.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

class PacienteDao(context: Context) {
    private val helper = Helper(context)

    fun insertarPaciente(nombre: String, raza: String, edad: String, peso: String, idCliente: Int, fechaRegistro: String): Boolean {
        val db = helper.writableDatabase
        val valores = ContentValues().apply {
            put("nombre", nombre)
            put("raza", raza)
            put("edad", edad)
            put("peso", peso)
            put("id_cliente", idCliente)
            put("fecha_registro", fechaRegistro)
        }
        val resultado = db.insert("tb_paciente", null, valores)
        db.close()
        return resultado != -1L
    }

    fun listarPacientes(): Cursor {
        val db = helper.readableDatabase
        return db.rawQuery("SELECT * FROM tb_paciente", null)
    }
}