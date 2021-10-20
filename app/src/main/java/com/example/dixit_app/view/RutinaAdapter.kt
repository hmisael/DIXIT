package com.example.dixit_app.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.dixit_app.databinding.ItemRutinaBinding
import com.example.dixit_app.model.entidades.Rutina
import com.google.android.material.snackbar.Snackbar

class RutinaAdapter : RecyclerView.Adapter<RutinaAdapter.RutinaViewHolder>() {

    class RutinaViewHolder(val itemBinding: ItemRutinaBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    private val differCallback =
        object : DiffUtil.ItemCallback<Rutina>() {
            override fun areItemsTheSame(oldItem: Rutina, newItem: Rutina): Boolean {
                return oldItem.idRutina == newItem.idRutina &&
                        oldItem.nombreRutina == newItem.nombreRutina
            }

            override fun areContentsTheSame(oldItem: Rutina, newItem: Rutina): Boolean {
                return oldItem == newItem
            }

        }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RutinaViewHolder {
        return RutinaViewHolder(
            ItemRutinaBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RutinaViewHolder, position: Int) {
        val rutinaActual = differ.currentList[position]
        holder.itemBinding.nombreRutina.text = rutinaActual.nombreRutina
        holder.itemView.setOnClickListener { view ->
            Snackbar.make(
                view, "Nombre: "+rutinaActual.nombreRutina+" - ID: "+rutinaActual.idRutina,
                Snackbar.LENGTH_SHORT
            ).show()
            val direction = RutinasFragmentDirections
                .actionRutinasFragmentToRutinaDetalleFragment(rutinaActual)
            view.findNavController().navigate(direction)
        }

        /*METODO PARA TRES PUNTOS MODIFICACION
        holder.itemView.setOnClickListener { view ->
            val direction = RutinasFragmentDirections
                .actionRutinasFragmentToRutinaModificarFragment(rutinaActual)
            view.findNavController().navigate(direction)
        }*/
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

}

