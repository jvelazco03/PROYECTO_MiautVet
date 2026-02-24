package com.miauvet.app.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.miauvet.app.databinding.ActivityLoginBinding
import com.miauvet.app.validators.LoginValidator

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val prefs = getSharedPreferences("personal_data", Context.MODE_PRIVATE)
        val document = prefs.getString("Document", "") ?: ""

        if (document.count() == 8) {
            irAlHome()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarBotonIngreso()
    }

    private fun configurarBotonIngreso() {
        binding.btnIngresar.setOnClickListener {
            val dni = binding.etDni.text.toString().trim()

            if (dni.isEmpty()) {
                Toast.makeText(this, "Ingresa tu DNI", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (LoginValidator.esValido(dni)) {
                val prefs = getSharedPreferences("personal_data", Context.MODE_PRIVATE)
                prefs.edit().putString("Document", dni).apply()

                irAlHome()
            } else {
                Toast.makeText(this, "El DNI debe tener exactamente 8 números", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun irAlHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}