package com.miauvet.app.validators

object ValidatorData {
    fun esValido(vararg campos: String): Boolean {
        for (campo in campos) {
            if (campo.isEmpty()) return false
        }
        return true
    }
}