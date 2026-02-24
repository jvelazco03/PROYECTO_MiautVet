package com.miauvet.app.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.miauvet.app.databinding.FragmentRegistrarVeterinarioBinding
import com.miauvet.app.network.RetrofitClient
import com.miauvet.app.network.VeterinarioApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RegistrarVeterinarioFragment : Fragment() {

    private var _binding: FragmentRegistrarVeterinarioBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegistrarVeterinarioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configurarEventos()
    }

    private fun configurarEventos() {
        binding.btnGuardar.setOnClickListener {
            guardarDatos()
        }
        binding.btnCancelar.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun guardarDatos() {
        val nombres = binding.etNombres.text.toString().trim()
        val apellidos = binding.etApellidos.text.toString().trim()
        val especialidad = binding.etEspecialidad.text.toString().trim()
        val estadoElegido = binding.spEstado.selectedItem.toString()

        if (nombres.isEmpty() || apellidos.isEmpty() || especialidad.isEmpty()) {
            Toast.makeText(requireContext(), "Completa todos los datos", Toast.LENGTH_SHORT).show()
            return
        }

        val fechaDeHoy = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        val nuevoVet = VeterinarioApi(
            id = "",
            nombres = nombres,
            apellidos = apellidos,
            especialidad = especialidad,
            estado = estadoElegido,
            fechaIngreso = fechaDeHoy,
            foto = "https://i.pravatar.cc/150?u=${System.currentTimeMillis()}"
        )

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.registrarVeterinario(nuevoVet)
                }
                findNavController().popBackStack()
            } catch (e: Exception) {
                Log.e("RETROFIT", "falló servicio")
                Toast.makeText(requireContext(), "Error de conexión", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}