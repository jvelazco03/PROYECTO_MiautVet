package com.miauvet.app.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.miauvet.app.R
import com.miauvet.app.adapters.Veterinario
import com.miauvet.app.adapters.VeterinarioAdapter
import com.miauvet.app.databinding.FragmentVeterinariosBinding
import com.miauvet.app.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class VeterinariosFragment : Fragment() {

    private var _binding: FragmentVeterinariosBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: VeterinarioAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentVeterinariosBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configurarRecyclerView()

        binding.btnNuevoVeterinario.setOnClickListener {
            findNavController().navigate(R.id.action_veterinariosFragment_to_registrarVeterinarioFragment)
        }
        binding.btnVolverMenu.setOnClickListener { findNavController().popBackStack() }

        cargarDatos()
    }

    private fun configurarRecyclerView() {
        adapter = VeterinarioAdapter(ArrayList<Veterinario>()) { doctor ->
            mostrarDialogoDeOpciones(doctor)
        }
        binding.rvVeterinarios.layoutManager = LinearLayoutManager(requireContext())
        binding.rvVeterinarios.adapter = adapter
    }

    private fun cargarDatos() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val listaNube = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.obtenerVeterinarios()
                }

                val listaAdaptada = ArrayList<Veterinario>()
                for (item in listaNube) {
                    val v = Veterinario(
                        id = item.id.toIntOrNull() ?: 0,
                        nombres = item.nombres,
                        apellidos = item.apellidos,
                        especialidad = item.especialidad,
                        estado = item.estado,
                        fechaIngreso = item.fechaIngreso
                    )
                    listaAdaptada.add(v)
                }
                adapter.actualizarLista(listaAdaptada)

            } catch (e: Exception) {
                Log.e("RETROFIT", "falló servicio")
            }
        }
    }

    private fun mostrarDialogoDeOpciones(doctor: Veterinario) {
        val opciones = arrayOf("Editar", "Eliminar")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Opciones para Dr/a. ${doctor.apellidos}")

        builder.setItems(opciones) { _, position ->
            if (position == 0) {
                val bundle = Bundle()
                bundle.putInt("id_veterinario", doctor.id)
                findNavController().navigate(R.id.action_veterinariosFragment_to_editarVeterinarioFragment, bundle)
            } else if (position == 1) {
                preguntarSiElimina(doctor)
            }
        }
        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

    private fun preguntarSiElimina(doctor: Veterinario) {
        val confirmacion = AlertDialog.Builder(requireContext())
        confirmacion.setTitle("Despedir Veterinario")
        confirmacion.setMessage("¿Estás seguro de que deseas eliminar al Dr/a. ${doctor.apellidos}?")
        confirmacion.setPositiveButton("Sí") { _, _ ->
            eliminarDeLaNube(doctor.id)
        }
        confirmacion.setNegativeButton("No", null)
        confirmacion.show()
    }

    private fun eliminarDeLaNube(id: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.eliminarVeterinario(id.toString())
                }
                cargarDatos()
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