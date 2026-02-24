package com.miauvet.app.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.miauvet.app.database.ConsultaDao
import com.miauvet.app.database.PacienteDao
import com.miauvet.app.database.VeterinarioDao
import com.miauvet.app.databinding.FragmentRegistrarConsultaBinding
import com.miauvet.app.network.RetrofitClient
import com.miauvet.app.validators.ValidatorData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class RegistrarConsultaFragment : Fragment() {

    private var _binding: FragmentRegistrarConsultaBinding? = null
    private val binding get() = _binding!!
    private lateinit var dao: ConsultaDao

    private var listaNombresPacientes = ArrayList<String>()
    private var listaNombresVeterinarios = ArrayList<String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegistrarConsultaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dao = ConsultaDao(requireContext())

        cargarDatos()
        configurarEventos()
        crearDatosLocalesFicticios()
    }

    private fun crearDatosLocalesFicticios() {
        try {
            val vetDao = VeterinarioDao(requireContext())
            val pacDao = PacienteDao(requireContext())
            val cursorVet = vetDao.listarVeterinarios()
            if (cursorVet.count == 0) {
                vetDao.insertarVeterinario("Veterinario", "Online", "General", "Activo", "2026-02-24")
            }
            cursorVet.close()
            val cursorPac = pacDao.listarPacientes()
            if (cursorPac.count == 0) {
                pacDao.insertarPaciente("Mascota", "Nube", "2", "4kg", 1, "2026-02-24")
            }
            cursorPac.close()
        } catch (e: Exception) {
        }
    }

    private fun cargarDatos() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val vets = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.obtenerVeterinarios()
                }
                listaNombresVeterinarios.clear()
                vets.forEach { vet ->
                    listaNombresVeterinarios.add("${vet.nombres} ${vet.apellidos}")
                }
                actualizarSpinner(binding.spVeterinario, listaNombresVeterinarios)

                val pacs = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.obtenerPacientes()
                }
                listaNombresPacientes.clear()
                pacs.forEach { pac ->
                    listaNombresPacientes.add(pac.nombre)
                }
                actualizarSpinner(binding.spPaciente, listaNombresPacientes)

            } catch (e: Exception) {
                Log.e("RETROFIT", "falló servicio")
            }
        }
    }

    private fun actualizarSpinner(spinner: android.widget.Spinner, lista: List<String>) {
        if (context == null) return
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, lista)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    private fun configurarEventos() {
        binding.btnGuardar.setOnClickListener { guardarDatos() }
        binding.btnCancelar.setOnClickListener { findNavController().popBackStack() }
    }

    private fun guardarDatos() {
        val motivo = binding.etMotivo.text.toString().trim()
        val diagnostico = binding.etDiagnostico.text.toString().trim()
        val precio = binding.etPrecio.text.toString().trim()

        if (!ValidatorData.esValido(motivo, diagnostico, precio)) {
            Toast.makeText(requireContext(), "Completa todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        val idPacienteFijo = 1
        val idVeterinarioFijo = 1
        val fechaHoy = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

        if (dao.insertarConsulta(idPacienteFijo, idVeterinarioFijo, motivo, diagnostico, precio, fechaHoy)) {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}