package com.miauvet.app.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.miauvet.app.database.ConsultaDao
import com.miauvet.app.database.PacienteDao
import com.miauvet.app.database.VeterinarioDao
import com.miauvet.app.databinding.FragmentEditarConsultaBinding
import com.miauvet.app.validators.ValidatorData

class EditarConsultaFragment : Fragment() {

    private var _binding: FragmentEditarConsultaBinding? = null
    private val binding get() = _binding!!
    private lateinit var dao: ConsultaDao
    private var idConsulta = -1
    private var mapaPacientes = HashMap<Int, String>()
    private var mapaVeterinarios = HashMap<Int, String>()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentEditarConsultaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dao = ConsultaDao(requireContext())
        idConsulta = arguments?.getInt("id_consulta") ?: -1

        if (idConsulta == -1) {
            findNavController().popBackStack()
            return
        }

        configurarRecyclerView()
        cargarDatos()

        binding.btnActualizar.setOnClickListener { actualizarDatos() }
        binding.btnCancelar.setOnClickListener { findNavController().popBackStack() }
    }

    private fun configurarRecyclerView() {
        val pDao = PacienteDao(requireContext())
        val cursorP = pDao.listarPacientes()
        val nombresP = ArrayList<String>()
        if (cursorP.moveToFirst()) {
            do {
                val id = cursorP.getInt(cursorP.getColumnIndexOrThrow("id_paciente"))
                val nom = cursorP.getString(cursorP.getColumnIndexOrThrow("nombre"))
                nombresP.add(nom)
                mapaPacientes[id] = nom
            } while (cursorP.moveToNext())
        }
        cursorP.close()
        binding.spPaciente.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, nombresP)

        val vDao = VeterinarioDao(requireContext())
        val cursorV = vDao.listarVeterinarios()
        val nombresV = ArrayList<String>()
        if (cursorV.moveToFirst()) {
            do {
                val id = cursorV.getInt(cursorV.getColumnIndexOrThrow("id_veterinario"))
                val nom = cursorV.getString(cursorV.getColumnIndexOrThrow("nombres"))
                nombresV.add(nom)
                mapaVeterinarios[id] = nom
            } while (cursorV.moveToNext())
        }
        cursorV.close()
        binding.spVeterinario.adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, nombresV)
    }

    private fun cargarDatos() {
        val cursor = dao.listarConsultas()
        if (cursor.moveToFirst()) {
            do {
                if (cursor.getInt(cursor.getColumnIndexOrThrow("id_consulta")) == idConsulta) {
                    binding.etMotivo.setText(cursor.getString(cursor.getColumnIndexOrThrow("motivo")))
                    binding.etDiagnostico.setText(cursor.getString(cursor.getColumnIndexOrThrow("diagnostico")))
                    binding.etPrecio.setText(cursor.getString(cursor.getColumnIndexOrThrow("precio")))
                    binding.etFecha.setText(cursor.getString(cursor.getColumnIndexOrThrow("fecha")))

                    val nomPac = cursor.getString(cursor.getColumnIndexOrThrow("nombre_paciente"))
                    val nomVet = cursor.getString(cursor.getColumnIndexOrThrow("nombre_veterinario"))

                    for (i in 0 until binding.spPaciente.count) {
                        if (binding.spPaciente.getItemAtPosition(i).toString() == nomPac) binding.spPaciente.setSelection(i)
                    }
                    for (i in 0 until binding.spVeterinario.count) {
                        if (binding.spVeterinario.getItemAtPosition(i).toString() == nomVet) binding.spVeterinario.setSelection(i)
                    }
                    break
                }
            } while (cursor.moveToNext())
        }
        cursor.close()
    }

    private fun actualizarDatos() {
        val mot = binding.etMotivo.text.toString().trim()
        val dia = binding.etDiagnostico.text.toString().trim()
        val pre = binding.etPrecio.text.toString().trim()
        val fec = binding.etFecha.text.toString().trim()

        if (!ValidatorData.esValido(mot, dia, pre, fec)) {
            Toast.makeText(requireContext(), "Llena todos los campos", Toast.LENGTH_SHORT).show()
            return
        }

        var idPac = -1
        val selPac = binding.spPaciente.selectedItem.toString()
        for (item in mapaPacientes) { if (item.value == selPac) idPac = item.key }

        var idVet = -1
        val selVet = binding.spVeterinario.selectedItem.toString()
        for (item in mapaVeterinarios) { if (item.value == selVet) idVet = item.key }

        if (dao.actualizarConsulta(idConsulta, idPac, idVet, mot, dia, pre, fec)) {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}