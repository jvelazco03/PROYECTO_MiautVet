package com.miauvet.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.miauvet.app.databinding.ItemClienteBinding

data class DueñoMascota(
    val id: Int,
    val nombres: String,
    val apellidos: String,
    val dni: String,
    val telefono: String,
    val direccion: String,
    val fechaRegistro: String
)

class ClienteAdapter(
    private var listaDeDuenios: List<DueñoMascota>,
    private val alElegirOpcion: (DueñoMascota) -> Unit
) : RecyclerView.Adapter<ClienteAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemClienteBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vBinding = ItemClienteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(vBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val persona = listaDeDuenios[position]
        
        holder.binding.tvNombreCliente.text = "${persona.nombres} ${persona.apellidos}"
        holder.binding.tvDniTelefono.text = "DNI: ${persona.dni} | Tel: ${persona.telefono}"
        holder.binding.tvDireccion.text = "Dirección: ${persona.direccion}"
        
        holder.binding.btnOpciones.setOnClickListener { 
            alElegirOpcion(persona) 
        }
    }

    override fun getItemCount(): Int {
        return listaDeDuenios.count()
    }

    fun refrescarLista(nuevaLista: List<DueñoMascota>) {
        this.listaDeDuenios = nuevaLista
        notifyDataSetChanged()
    }
}