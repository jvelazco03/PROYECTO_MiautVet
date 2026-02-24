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
import com.miauvet.app.adapters.ClienteAdapter
import com.miauvet.app.adapters.DueñoMascota
import com.miauvet.app.database.ClienteDao
import com.miauvet.app.databinding.FragmentClientesBinding

class ClientesFragment : Fragment() {

    private var _binding: FragmentClientesBinding? = null
    private val binding get() = _binding!!

    private lateinit var bd: ClienteDao
    private lateinit var adapter: ClienteAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentClientesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        bd = ClienteDao(requireContext())

        configurarRecyclerView()
        configurarEventos()
        cargarDatos()
    }

    private fun configurarRecyclerView() {
        adapter = ClienteAdapter(ArrayList<DueñoMascota>()) { persona ->
            mostrarMenuOpciones(persona)
        }
        binding.rvClientes.layoutManager = LinearLayoutManager(requireContext())
        binding.rvClientes.adapter = adapter
    }

    private fun configurarEventos() {
        binding.btnNuevoCliente.setOnClickListener {
            findNavController().navigate(R.id.action_clientesFragment_to_registrarClienteFragment)
        }

        binding.btnVolverMenu.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun cargarDatos() {
        val cursor = bd.listarClientes()
        val listaTemporal = ArrayList<DueñoMascota>()

        if (cursor.moveToFirst()) {
            do {
                val id = cursor.getInt(cursor.getColumnIndexOrThrow("id_cliente"))
                val nom = cursor.getString(cursor.getColumnIndexOrThrow("nombres"))
                val ape = cursor.getString(cursor.getColumnIndexOrThrow("apellidos"))
                val dni = cursor.getString(cursor.getColumnIndexOrThrow("dni"))
                val tel = cursor.getString(cursor.getColumnIndexOrThrow("telefono"))
                val dir = cursor.getString(cursor.getColumnIndexOrThrow("direccion"))
                val fec = cursor.getString(cursor.getColumnIndexOrThrow("fecha_registro"))

                val d = DueñoMascota(id, nom, ape, dni, tel, dir, fec)
                listaTemporal.add(d)

            } while (cursor.moveToNext())
        }
        cursor.close()

        adapter.refrescarLista(listaTemporal)
    }

    private fun mostrarMenuOpciones(persona: DueñoMascota) {
        val opciones = arrayOf("Editar Datos", "Borrar del Sistema")
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Acciones para ${persona.nombres}")
        builder.setItems(opciones) { _, position ->
            if (position == 0) {
                irAEditar(persona)
            } else if (position == 1) {
                confirmarBorrado(persona)
            }
        }
        builder.setNegativeButton("Cancelar", null)
        builder.show()
    }

    private fun irAEditar(persona: DueñoMascota) {
        val bundle = Bundle()
        bundle.putInt("id_cliente", persona.id)
        findNavController().navigate(R.id.action_clientesFragment_to_editarClienteFragment, bundle)
    }

    private fun confirmarBorrado(persona: DueñoMascota) {
        val alerta = AlertDialog.Builder(requireContext())
        alerta.setTitle("¡Atención!")
        alerta.setMessage("¿Estás seguro de quitar a ${persona.nombres} de la base de datos?")
        alerta.setPositiveButton("Sí, quitar") { _, _ ->
            val exito = bd.eliminarCliente(persona.id)
            if (exito) {
                Toast.makeText(requireContext(), "Dueño eliminado correctamente", Toast.LENGTH_SHORT).show()
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