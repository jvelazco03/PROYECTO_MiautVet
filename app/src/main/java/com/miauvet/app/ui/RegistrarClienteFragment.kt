package com.miauvet.app.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.miauvet.app.database.ClienteDao
import com.miauvet.app.databinding.FragmentRegistrarClienteBinding
import com.miauvet.app.validators.ValidatorData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RegistrarClienteFragment : Fragment() {

    private var _binding: FragmentRegistrarClienteBinding? = null
    private val binding get() = _binding!!
    private lateinit var dao: ClienteDao

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegistrarClienteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dao = ClienteDao(requireContext())

        binding.btnGuardar.setOnClickListener { guardarDatos() }
        binding.btnCancelar.setOnClickListener { findNavController().popBackStack() }
    }

    private fun guardarDatos() {
        val nombres = binding.etNombres.text.toString().trim()
        val apellidos = binding.etApellidos.text.toString().trim()
        val dni = binding.etDni.text.toString().trim()
        val telefono = binding.etTelefono.text.toString().trim()
        val direccion = binding.etDireccion.text.toString().trim()

        if (!ValidatorData.esValido(nombres, apellidos, dni, telefono, direccion)) {
            Toast.makeText(requireContext(), "Por favor, llena todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val fechaRegistro = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        val exito = dao.insertarCliente(nombres, apellidos, dni, telefono, direccion, fechaRegistro)

        if (exito) {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}