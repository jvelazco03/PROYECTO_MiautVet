package com.miauvet.app.validators

object LoginValidator {
    fun esValido(dni: String): Boolean {
        return dni.count() == 8
    }
}