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
import com.miauvet.app.databinding.FragmentRegistrarPacienteBinding
import com.miauvet.app.network.PacienteApi
import com.miauvet.app.network.RetrofitClient
import com.miauvet.app.validators.ValidatorData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegistrarPacienteFragment : Fragment() {

    private var _binding: FragmentRegistrarPacienteBinding? = null
    private val binding get() = _binding!!
    private lateinit var dao: ClienteDao
    private var mapaDuenios = HashMap<Int, String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentRegistrarPacienteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        dao = ClienteDao(requireContext())
        rellenarSelectorDeDuenios()
        configurarEventos()
    }

    private fun rellenarSelectorDeDuenios() {
        val datosDuenios = dao.listarNombresClientes()
        val nombresParaLista = ArrayList<String>()

        for (item in datosDuenios) {
            nombresParaLista.add(item.value)
            mapaDuenios[item.key] = item.value
        }

        if (nombresParaLista.isEmpty()) {
            nombresParaLista.add("Cliente General (Local)")
        }

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, nombresParaLista)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spDuenio.adapter = adapter
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
        val nombre = binding.etNombre.text.toString().trim()
        val raza = binding.etRaza.text.toString().trim()

        if (!ValidatorData.esValido(nombre, raza)) {
            Toast.makeText(requireContext(), "Nombre y Raza son obligatorios", Toast.LENGTH_SHORT).show()
            return
        }

        val nuevaMascota = PacienteApi(
            id = "",
            nombre = nombre,
            raza = raza,
            sexo = "Macho",
            foto = "https://loremflickr.com/320/240/cat?lock=${System.currentTimeMillis()}"
        )

        viewLifecycleOwner.lifecycleScope.launch {
            try {
                withContext(Dispatchers.IO) {
                    RetrofitClient.apiService.registrarPaciente(nuevaMascota)
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