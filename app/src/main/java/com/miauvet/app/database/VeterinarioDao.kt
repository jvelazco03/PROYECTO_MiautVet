package com.miauvet.app.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

class VeterinarioDao(context: Context) {
    private val helper = Helper(context)

    fun insertarVeterinario(nombres: String, apellidos: String, especialidad: String, estado: String, fechaIngreso: String): Boolean {
        val db = helper.writableDatabase
        val valores = ContentValues().apply {
            put("nombres", nombres)
            put("apellidos", apellidos)
            put("especialidad", especialidad)
            put("estado", estado)
            put("fecha_ingreso", fechaIngreso)
        }
        val resultado = db.insert("tb_veterinario", null, valores)
        db.close()
        return resultado != -1L
    }

    fun listarVeterinarios(): Cursor {
        val db = helper.readableDatabase
        return db.rawQuery("SELECT * FROM tb_veterinario", null)
    }
}