package com.miauvet.app.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.miauvet.app.R
import com.miauvet.app.adapters.ConsultaAdapter
import com.miauvet.app.adapters.ConsultaMedica
import com.miauvet.app.database.ConsultaDao
import com.miauvet.app.databinding.FragmentConsultasBinding

class ConsultasFragment : Fragment() {

    private var _binding: FragmentConsultasBinding? = null
    private val binding get() = _binding!!

    private lateinit var dao: ConsultaDao
    private lateinit var adapter: ConsultaAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConsultasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dao = ConsultaDao(requireContext())

        configurarRecyclerView()
        configurarEventos()
        cargarDatos()
    }

    private fun configurarRecyclerView() {
        adapter = ConsultaAdapter(ArrayList<ConsultaMedica>()) { consulta ->
            mostrarMenuOpciones(consulta)
        }
        binding.rvConsultas.layoutManager = LinearLayoutManager(requireContext())
        binding.rvConsultas.adapter = adapter
    }

    private fun configurarEventos() {
        binding.btnNuevaConsulta.setOnClickListener {
            findNavController().navigate(R.id.action_consultasFragment_to_registrarConsultaFragment)
        }

        binding.btnVolverMenu.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun cargarDatos() {
        val cursor = dao.listarConsultas()
        val listaTemporal = ArrayList<ConsultaMedica>()

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id_consulta"))
                val pac = cursor.getString(cursor.getColumnIndexOrThrow("nombre_paciente"))
                val vet = cursor.getString(cursor.getColumnIndexOrThrow("nombre_veterinario"))
                val mot = cursor.getString(cursor.getColumnIndexOrThrow("motivo"))
                val dia = cursor.getString(cursor.getColumnIndexOrThrow("diagnostico"))
                val pre = cursor.getString(cursor.getColumnIndexOrThrow("precio"))
                val fec = cursor.getString(cursor.getColumnIndexOrThrow("fecha"))

                val item = ConsultaMedica(id, pac, vet, mot, dia, pre, fec)
                listaTemporal.add(item)

            } while (cursor.moveToNext())
        }
        cursor.close()

        adapter.actualizarConsultas(listaTemporal)
    }

    private fun mostrarMenuOpciones(consulta: ConsultaMedica) {
        val opciones = arrayOf("Ver/Editar Detalles", "Borrar Consulta")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Consulta de ${consulta.nombrePaciente}")
        builder.setItems(opciones) { _, position ->
            if (position == 0) {
                irAEditar(consulta)
            } else if (position == 1) {
                confirmarBorrado(consulta)
            }
        }
        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

    private fun irAEditar(consulta: ConsultaMedica) {
        val bundle = Bundle()
        bundle.putInt("id_consulta", consulta.id)
        findNavController().navigate(R.id.action_consultasFragment_to_editarConsultaFragment, bundle)
    }

    private fun confirmarBorrado(consulta: ConsultaMedica) {
        val alerta = AlertDialog.Builder(requireContext())
        alerta.setTitle("Confirmación")
        alerta.setMessage("¿Estás seguro de que deseas eliminar esta consulta?")
        alerta.setPositiveButton("Sí, eliminar") { _, _ ->
            val exito = dao.eliminarConsulta(consulta.id)
            if (exito) {
                Toast.makeText(requireContext(), "Consulta eliminada", Toast.LENGTH_SHORT).show()
                cargarDatos()
            }
        }
        alerta.setNegativeButton("No", null)
        alerta.show()
    }

    override fun onResume() {
        super.onResume()
        cargarDatos()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}