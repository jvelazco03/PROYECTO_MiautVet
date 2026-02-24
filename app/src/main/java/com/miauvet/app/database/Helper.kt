package com.miauvet.app.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

private const val DB_NAME = "miauvet.db"
private const val DB_VERSION = 3

class Helper(context: Context) : SQLiteOpenHelper(context, DB_NAME, null, DB_VERSION) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("""
            CREATE TABLE IF NOT EXISTS tb_veterinario (
                id_veterinario INTEGER PRIMARY KEY AUTOINCREMENT,
                nombres TEXT NOT NULL,
                apellidos TEXT NOT NULL,
                especialidad TEXT NOT NULL,
                estado TEXT NOT NULL,
                fecha_ingreso TEXT NOT NULL
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS tb_cliente (
                id_cliente INTEGER PRIMARY KEY AUTOINCREMENT,
                nombres TEXT NOT NULL,
                apellidos TEXT NOT NULL,
                dni TEXT NOT NULL,
                telefono TEXT NOT NULL,
                direccion TEXT NOT NULL,
                fecha_registro TEXT NOT NULL
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS tb_paciente (
                id_paciente INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                raza TEXT NOT NULL,
                edad TEXT NOT NULL,
                peso TEXT NOT NULL,
                id_cliente INTEGER NOT NULL,
                fecha_registro TEXT NOT NULL,
                FOREIGN KEY(id_cliente) REFERENCES tb_cliente(id_cliente)
            )
        """.trimIndent())

        db.execSQL("""
            CREATE TABLE IF NOT EXISTS tb_consulta (
                id_consulta INTEGER PRIMARY KEY AUTOINCREMENT,
                id_paciente INTEGER NOT NULL,
                id_veterinario INTEGER NOT NULL,
                motivo TEXT NOT NULL,
                diagnostico TEXT NOT NULL,
                precio TEXT NOT NULL,
                fecha TEXT NOT NULL,
                FOREIGN KEY(id_paciente) REFERENCES tb_paciente(id_paciente),
                FOREIGN KEY(id_veterinario) REFERENCES tb_veterinario(id_veterinario)
            )
        """.trimIndent())
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS tb_veterinario")
        db.execSQL("DROP TABLE IF EXISTS tb_cliente")
        db.execSQL("DROP TABLE IF EXISTS tb_paciente")
        db.execSQL("DROP TABLE IF EXISTS tb_consulta")
        onCreate(db)
    }
}