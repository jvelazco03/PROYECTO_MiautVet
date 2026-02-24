package com.miauvet.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.miauvet.app.databinding.ItemConsultaBinding

data class ConsultaMedica(
    val id: Int,
    val nombrePaciente: String,
    val nombreVeterinario: String,
    val motivo: String,
    val diagnostico: String,
    val precio: String,
    val fecha: String
)

class ConsultaAdapter(
    private var listaDeConsultas: List<ConsultaMedica>,
    private val alTocarOpciones: (ConsultaMedica) -> Unit
) : RecyclerView.Adapter<ConsultaAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemConsultaBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewBinding = ItemConsultaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val consulta = listaDeConsultas[position]
        
        holder.binding.tvPacienteConsulta.text = "Paciente: ${consulta.nombrePaciente}"
        holder.binding.tvVetConsulta.text = "Atendido por: ${consulta.nombreVeterinario}"
        holder.binding.tvMotivoConsulta.text = "Motivo: ${consulta.motivo}"
        holder.binding.tvFechaPrecio.text = "Fecha: ${consulta.fecha} | Costo: S/ ${consulta.precio}"
        
        holder.binding.btnOpciones.setOnClickListener { 
            alTocarOpciones(consulta) 
        }
    }

    override fun getItemCount(): Int {
        return listaDeConsultas.count()
    }

    fun actualizarConsultas(nuevaLista: List<ConsultaMedica>) {
        this.listaDeConsultas = nuevaLista
        notifyDataSetChanged()
    }
}