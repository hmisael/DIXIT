package com.example.dixit_app.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.dixit_app.databinding.ItemFraseBinding
import com.example.dixit_app.model.entidades.Frase
import com.example.dixit_app.model.entidades.Rutina
import com.google.android.material.snackbar.Snackbar

class FraseAdapter : RecyclerView.Adapter<FraseAdapter.FraseViewHolder>() {

    class FraseViewHolder(val itemBinding: ItemFraseBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    private val differCallback =
        object : DiffUtil.ItemCallback<Frase>() {
            override fun areItemsTheSame(oldItem: Frase, newItem: Frase): Boolean {
                return oldItem.idFrase == newItem.idFrase &&
                        oldItem.nombreFrase == newItem.nombreFrase
            }

            override fun areContentsTheSame(oldItem: Frase, newItem: Frase): Boolean {
                return oldItem == newItem
            }

        }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FraseViewHolder {
        return FraseViewHolder(
            ItemFraseBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: FraseViewHolder, position: Int) {
        val fraseActual = differ.currentList[position]
        holder.itemBinding.nombreFrase.text = fraseActual.nombreFrase
        holder.itemView.setOnClickListener { view ->
            Snackbar.make(
                view, "Nombre: "+fraseActual.nombreFrase+" - ID: "+fraseActual.idFrase,
                Snackbar.LENGTH_SHORT
            ).show()
            val direction = FrasesFragmentDirections
                .actionFrasesFragmentToFraseDetalleFragment(fraseActual)
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

