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
import com.miauvet.app.database.ClienteDao
import com.miauvet.app.databinding.FragmentEditarPacienteBinding
import com.miauvet.app.network.PacienteApi
import com.miauvet.app.network.RetrofitClient
import com.miauvet.app.validators.ValidatorData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditarPacienteFragment : Fragment() {

    private var _binding: FragmentEditarPacienteBinding? = null
    private val binding get() = _binding!!
    private lateinit var dao: ClienteDao
    private var idPacienteParaEditar = -1
    private var mapaDuenios = HashMap<Int, String>()
    private var fotoOriginal = ""

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEditarPacienteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dao = ClienteDao(requireContext())
        idPacienteParaEditar = arguments?.getInt("id_paciente") ?: -1

        if (idPacienteParaEditar == -1) {
            findNavController().popBackStack()
            return
        }

        rellenarDuenios()
        cargarDatos()

        binding.btnGuardar.setOnClickListener { actualizarDatos() }
        binding.btnCancelar.setOnClickListener { findNavController().popBackStack() }
    }

    private fun rellenarDuenios() {
        val datosDuenios = dao.listarNombresClientes()
        val nombresParaLista = ArrayList<String>()
        for (item in datosDuenios) {
            nombresParaLista.add(item.value)
            mapaDuenios[item.key] = item.value
        }
        if (nombresParaLista.isEmpty()) nombresParaLista.add("Cliente Local")

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, nombresParaLista)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spDuenio.adapter = adapter
    }

    private fun cargarDatos() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val paciente = withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.obtenerPacientePorId(idPacienteParaEditar.toString())
                }

                binding.etNombre.setText(paciente.nombre)
                binding.etRaza.setText(paciente.raza)
                binding.etEdad.setText("2")
                binding.etPeso.setText("3kg")
                fotoOriginal = paciente.foto

            } catch (e: Exception) {
                Log.e("RETROFIT", "falló servicio")
            }
        }
    }

    private fun actualizarDatos() {
        val nom = binding.etNombre.text.toString().trim()
        val raz = binding.etRaza.text.toString().trim()

        if (!ValidatorData.esValido(nom, raz)) {
            Toast.makeText(requireContext(), "Nombre y raza son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        val pacienteActualizado = PacienteApi(
            id = idPacienteParaEditar.toString(),
            nombre = nom,
            raza = raz,
            sexo = "Macho",
            foto = fotoOriginal
        )

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.actualizarPaciente(idPacienteParaEditar.toString(), pacienteActualizado)
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