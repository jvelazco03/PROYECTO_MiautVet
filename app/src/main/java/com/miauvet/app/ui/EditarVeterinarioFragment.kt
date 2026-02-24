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
import com.miauvet.app.databinding.FragmentEditarVeterinarioBinding
import com.miauvet.app.network.RetrofitClient
import com.miauvet.app.network.VeterinarioApi
import com.miauvet.app.validators.ValidatorData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditarVeterinarioFragment : Fragment() {

    private var _binding: FragmentEditarVeterinarioBinding? = null
    private val binding get() = _binding!!
    private var idVetAEditar = -1
    private var fotoOriginal = ""
    private var fechaOriginal = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEditarVeterinarioBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        idVetAEditar = arguments?.getInt("id_veterinario") ?: -1

        if (idVetAEditar == -1) {
            findNavController().popBackStack()
            return
        }

        cargarDatos()

        binding.btnGuardar.setOnClickListener { actualizarDatos() }
        binding.btnCancelar.setOnClickListener { findNavController().popBackStack() }
    }

    private fun cargarDatos() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val vet = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.obtenerVeterinarioPorId(idVetAEditar.toString())
                }

                binding.etNombres.setText(vet.nombres)
                binding.etApellidos.setText(vet.apellidos)
                binding.etEspecialidad.setText(vet.especialidad)

                fotoOriginal = vet.foto
                fechaOriginal = vet.fechaIngreso

                for (i in 0 until binding.spEstado.count) {
                    if (binding.spEstado.getItemAtPosition(i).toString() == vet.estado) {
                        binding.spEstado.setSelection(i)
                        break
                    }
                }
            } catch (e: Exception) {
                Log.e("RETROFIT", "falló servicio")
            }
        }
    }

    private fun actualizarDatos() {
        val nom = binding.etNombres.text.toString().trim()
        val ape = binding.etApellidos.text.toString().trim()
        val esp = binding.etEspecialidad.text.toString().trim()
        val est = binding.spEstado.selectedItem.toString()

        if (!ValidatorData.esValido(nom, ape, esp)) {
            Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val vetActualizado = VeterinarioApi(
            id = idVetAEditar.toString(),
            nombres = nom,
            apellidos = ape,
            especialidad = esp,
            estado = est,
            fechaIngreso = fechaOriginal,
            foto = fotoOriginal
        )

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.actualizarVeterinario(idVetAEditar.toString(), vetActualizado)
                }
                findNavController().popBackStack()
            } catch (e: Exception) {
                Log.e("RETROFIT", "falló servicio")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}