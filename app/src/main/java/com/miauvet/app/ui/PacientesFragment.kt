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
import com.miauvet.app.adapters.Paciente
import com.miauvet.app.adapters.PacienteAdapter
import com.miauvet.app.databinding.FragmentPacientesBinding
import com.miauvet.app.network.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PacientesFragment : Fragment() {

    private var _binding: FragmentPacientesBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: PacienteAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentPacientesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configurarRecyclerView()
        configurarEventos()
        cargarDatos()
    }

    private fun configurarRecyclerView() {
        adapter = PacienteAdapter(ArrayList<Paciente>()) { paciente ->
            mostrarDialogoDeOpciones(paciente)
        }
        binding.rvPacientes.layoutManager = LinearLayoutManager(requireContext())
        binding.rvPacientes.adapter = adapter
    }

    private fun configurarEventos() {
        binding.btnNuevoPaciente.setOnClickListener {
            findNavController().navigate(R.id.action_pacientesFragment_to_registrarPacienteFragment)
        }
        binding.btnVolverMenu.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun cargarDatos() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val listaNube = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.obtenerPacientes()
                }

                val listaAdaptada = ArrayList<Paciente>()
                for (item in listaNube) {
                    val p = Paciente(
                        id = item.id.toIntOrNull() ?: 0,
                        nombre = item.nombre,
                        raza = item.raza,
                        edad = "2",
                        peso = item.sexo,
                        duenio = "Online",
                        fotoUrl = item.foto
                    )
                    listaAdaptada.add(p)
                }
                adapter.updateData(listaAdaptada)

            } catch (e: Exception) {
                Log.e("RETROFIT", "falló servicio")
            }
        }
    }

    private fun mostrarDialogoDeOpciones(paciente: Paciente) {
        val opciones = arrayOf("Editar", "Eliminar")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Opciones para ${paciente.nombre}")

        builder.setItems(opciones) { _, position ->
            if (position == 0) {
                val bundle = Bundle()
                bundle.putInt("id_paciente", paciente.id)
                findNavController().navigate(R.id.action_pacientesFragment_to_editarPacienteFragment, bundle)
            } else if (position == 1) {
                preguntarSiElimina(paciente)
            }
        }
        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

    private fun preguntarSiElimina(paciente: Paciente) {
        val confirmacion = AlertDialog.Builder(requireContext())
        confirmacion.setTitle("Eliminar Mascota")
        confirmacion.setMessage("¿Estás seguro de que deseas eliminar a ${paciente.nombre} de la nube?")
        confirmacion.setPositiveButton("Sí") { _, _ ->
            eliminarDeLaNube(paciente.id)
        }
        confirmacion.setNegativeButton("No", null)
        confirmacion.show()
    }

    private fun eliminarDeLaNube(id: Int) {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.eliminarPaciente(id.toString())
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