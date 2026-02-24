package com.miauvet.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.miauvet.app.databinding.ItemVeterinarioBinding

data class Veterinario(
    val id: Int,
    val nombres: String,
    val apellidos: String,
    val especialidad: String,
    val estado: String,
    val fechaIngreso: String
)

class VeterinarioAdapter(
    private var listaVeterinarios: List<Veterinario>,
    private val alTocarOpciones: (Veterinario) -> Unit
) : RecyclerView.Adapter<VeterinarioAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemVeterinarioBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binder = ItemVeterinarioBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binder)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val vet = listaVeterinarios[position]
        
        holder.binding.tvNombreVeterinario.text = "${vet.nombres} ${vet.apellidos}"
        holder.binding.tvEspecialidad.text = "Especialidad: ${vet.especialidad}"
        holder.binding.tvEstado.text = "Estado: ${vet.estado}"
        
        holder.binding.btnOpciones.setOnClickListener { 
            alTocarOpciones(vet) 
        }
    }

    override fun getItemCount(): Int {
        return listaVeterinarios.count()
    }

    fun actualizarLista(nuevaLista: List<Veterinario>) {
        this.listaVeterinarios = nuevaLista
        notifyDataSetChanged()
    }
}