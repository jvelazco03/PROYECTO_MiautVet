package com.miauvet.app.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.miauvet.app.R
import com.miauvet.app.databinding.FragmentMenuPrincipalBinding

class MenuPrincipalFragment : Fragment() {

    private var _binding: FragmentMenuPrincipalBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMenuPrincipalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configurarBotones()
    }

    private fun configurarBotones() {
        binding.cardVeterinarios.setOnClickListener {
            findNavController().navigate(R.id.action_menuPrincipalFragment_to_veterinariosFragment)
        }

        binding.cardClientes.setOnClickListener {
            findNavController().navigate(R.id.action_menuPrincipalFragment_to_clientesFragment)
        }

        binding.cardPacientes.setOnClickListener {
            findNavController().navigate(R.id.action_menuPrincipalFragment_to_pacientesFragment)
        }

        binding.cardConsultas.setOnClickListener {
            findNavController().navigate(R.id.action_menuPrincipalFragment_to_consultasFragment)
        }

        binding.btnSalir.setOnClickListener {
            val prefs = requireContext().getSharedPreferences("personal_data", Context.MODE_PRIVATE)
            prefs.edit().clear().apply()

            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)

            requireActivity().finish()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}