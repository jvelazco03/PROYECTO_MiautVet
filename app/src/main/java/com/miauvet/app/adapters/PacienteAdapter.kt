package com.miauvet.app.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide // 📸 Importamos Glide
import com.miauvet.app.R
import com.miauvet.app.databinding.ItemPacienteBinding

data class Paciente(
    val id: Int,
    val nombre: String,
    val raza: String,
    val edad: String,
    val peso: String,
    val duenio: String,
    val fotoUrl: String = ""
)

class PacienteAdapter(
    private var listaDePacientes: List<Paciente>,
    private val alTocarOpciones: (Paciente) -> Unit
) : RecyclerView.Adapter<PacienteAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemPacienteBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val viewBinding = ItemPacienteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(viewBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val elGatito = listaDePacientes[position]

        holder.binding.tvNombreMascota.text = elGatito.nombre
        holder.binding.tvRazaEdad.text = "${elGatito.raza} • ${elGatito.edad} años"
        holder.binding.tvDuenio.text = "Dueño: ${elGatito.duenio}"

        if (elGatito.fotoUrl.isNotEmpty()) {
            Glide.with(holder.itemView.context)
                .load(elGatito.fotoUrl)
                .circleCrop()
                .into(holder.binding.imgMascota)
        } else {
            holder.binding.imgMascota.setImageResource(R.drawable.img_cat)
        }

        holder.binding.btnOpciones.setOnClickListener {
            alTocarOpciones(elGatito)
        }

        holder.itemView.setOnClickListener {
            alTocarOpciones(elGatito)
        }
    }

    override fun getItemCount(): Int = listaDePacientes.size

    fun updateData(nuevaLista: List<Paciente>) {
        this.listaDePacientes = nuevaLista
        notifyDataSetChanged()
    }
}