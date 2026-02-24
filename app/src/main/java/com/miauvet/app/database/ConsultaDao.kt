package com.miauvet.app.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

class ConsultaDao(context: Context) {
    private val helper = Helper(context)

    fun insertarConsulta(
        idPaciente: Int,
        idVeterinario: Int,
        motivo: String,
        diagnostico: String,
        precio: String,
        fecha: String
    ): Boolean {
        val db = helper.writableDatabase
        val valores = ContentValues().apply {
            put("id_paciente", idPaciente)
            put("id_veterinario", idVeterinario)
            put("motivo", motivo)
            put("diagnostico", diagnostico)
            put("precio", precio)
            put("fecha", fecha)
        }
        val resultado = db.insert("tb_consulta", null, valores)
        db.close()
        return resultado != -1L
    }

    fun listarConsultas(): Cursor {
        val db = helper.readableDatabase
        val query = """
            SELECT 
                c.id_consulta, 
                c.motivo, 
                c.diagnostico, 
                c.precio, 
                c.fecha,
                p.nombre AS nombre_paciente, 
                v.nombres AS nombre_veterinario 
            FROM tb_consulta c
            JOIN tb_paciente p ON c.id_paciente = p.id_paciente
            JOIN tb_veterinario v ON c.id_veterinario = v.id_veterinario
        """.trimIndent()

        return db.rawQuery(query, null)
    }

    fun actualizarConsulta(
        id: Int,
        idPaciente: Int,
        idVeterinario: Int,
        motivo: String,
        diagnostico: String,
        precio: String,
        fecha: String
    ): Boolean {
        val db = helper.writableDatabase
        val valores = ContentValues().apply {
            put("id_paciente", idPaciente)
            put("id_veterinario", idVeterinario)
            put("motivo", motivo)
            put("diagnostico", diagnostico)
            put("precio", precio)
            put("fecha", fecha)
        }
        val filasAfectadas = db.update("tb_consulta", valores, "id_consulta = ?", arrayOf(id.toString()))
        db.close()
        return filasAfectadas > 0
    }

    fun eliminarConsulta(id: Int): Boolean {
        val db = helper.writableDatabase
        val filasAfectadas = db.delete("tb_consulta", "id_consulta = ?", arrayOf(id.toString()))
        db.close()
        return filasAfectadas > 0
    }
}