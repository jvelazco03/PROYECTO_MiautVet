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
import com.miauvet.app.databinding.FragmentEditarClienteBinding
import com.miauvet.app.validators.ValidatorData
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class EditarClienteFragment : Fragment() {

    private var _binding: FragmentEditarClienteBinding? = null
    private val binding get() = _binding!!
    private lateinit var dao: ClienteDao
    private var idCliente = -1
    private var fechaOriginal = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEditarClienteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dao = ClienteDao(requireContext())
        idCliente = arguments?.getInt("id_cliente") ?: -1

        if (idCliente == -1) {
            findNavController().popBackStack()
            return
        }

        cargarDatos()
        binding.btnGuardar.setOnClickListener { actualizarDatos() }
        binding.btnCancelar.setOnClickListener { findNavController().popBackStack() }
    }

    private fun cargarDatos() {
        val cursor = dao.listarClientes()
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getInt(cursor.getColumnIndexOrThrow("id_cliente")) == idCliente) {
                    binding.etNombres.setText(cursor.getString(cursor.getColumnIndexOrThrow("nombres")))
                    binding.etApellidos.setText(cursor.getString(cursor.getColumnIndexOrThrow("apellidos")))
                    binding.etDni.setText(cursor.getString(cursor.getColumnIndexOrThrow("dni")))
                    binding.etTelefono.setText(cursor.getString(cursor.getColumnIndexOrThrow("telefono")))
                    binding.etDireccion.setText(cursor.getString(cursor.getColumnIndexOrThrow("direccion")))
                    fechaOriginal = cursor.getString(cursor.getColumnIndexOrThrow("fecha_registro"))
                    break
                }
            } while (cursor.moveToNext())
        }
        cursor.close()
    }

    private fun actualizarDatos() {
        val nom = binding.etNombres.text.toString().trim()
        val ape = binding.etApellidos.text.toString().trim()
        val dni = binding.etDni.text.toString().trim()
        val tel = binding.etTelefono.text.toString().trim()
        val dir = binding.etDireccion.text.toString().trim()

        if (!ValidatorData.esValido(nom, ape, dni, tel, dir)) {
            Toast.makeText(requireContext(), "No puedes dejar datos en blanco", Toast.LENGTH_SHORT).show()
            return
        }

        if (fechaOriginal.isEmpty()) {
            fechaOriginal = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        }

        if (dao.actualizarCliente(idCliente, nom, ape, dni, tel, dir, fechaOriginal)) {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}