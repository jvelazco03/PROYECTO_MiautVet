package com.miauvet.app.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor

class ClienteDao(context: Context) {
    private val helper = Helper(context)

    fun insertarCliente(
        nombres: String,
        apellidos: String,
        dni: String,
        telefono: String,
        direccion: String,
        fechaRegistro: String
    ): Boolean {
        val db = helper.writableDatabase
        val valores = ContentValues().apply {
            put("nombres", nombres)
            put("apellidos", apellidos)
            put("dni", dni)
            put("telefono", telefono)
            put("direccion", direccion)
            put("fecha_registro", fechaRegistro)
        }
        val resultado = db.insert("tb_cliente", null, valores)
        db.close()
        return resultado != -1L
    }

    fun listarClientes(): Cursor {
        val db = helper.readableDatabase
        return db.rawQuery("SELECT * FROM tb_cliente", null)
    }

    fun actualizarCliente(
        id: Int,
        nombres: String,
        apellidos: String,
        dni: String,
        telefono: String,
        direccion: String,
        fechaRegistro: String
    ): Boolean {
        val db = helper.writableDatabase
        val valores = ContentValues().apply {
            put("nombres", nombres)
            put("apellidos", apellidos)
            put("dni", dni)
            put("telefono", telefono)
            put("direccion", direccion)
            put("fecha_registro", fechaRegistro)
        }
        val filasAfectadas = db.update("tb_cliente", valores, "id_cliente = ?", arrayOf(id.toString()))
        db.close()
        return filasAfectadas > 0
    }


    fun eliminarCliente(id: Int): Boolean {
        val db = helper.writableDatabase
        val filasAfectadas = db.delete("tb_cliente", "id_cliente = ?", arrayOf(id.toString()))
        db.close()
        return filasAfectadas > 0
    }

    fun listarNombresClientes(): Map<Int, String> {
        val db = helper.readableDatabase
        val mapa = mutableMapOf<Int, String>()

        val cursor = db.rawQuery("SELECT id_cliente, nombres, apellidos FROM tb_cliente", null)

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id_cliente"))
                val nombre = cursor.getString(cursor.getColumnIndexOrThrow("nombres"))
                val apellido = cursor.getString(cursor.getColumnIndexOrThrow("apellidos"))
                mapa[id] = "$nombre $apellido"
            } while (cursor.moveToNext())
        }
        cursor.close()
        return mapa
    }
}