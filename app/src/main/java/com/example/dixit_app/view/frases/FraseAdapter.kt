package com.example.dixit_app.view.frases

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.dixit_app.databinding.ItemListaBinding
import com.example.dixit_app.model.entities.Frase

//Este Adapter muestra los Pictogramas que pertenecen a una Frase
class FraseAdapter (private val fraseClickDeleteInterface: FraseClickDeleteInterface,
                    private val fraseClickInterface: FraseClickInterface):
                    RecyclerView.Adapter<FraseAdapter.FraseViewHolder>() {

    class FraseViewHolder(val itemBinding: ItemListaBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    private val differCallback = object : DiffUtil.ItemCallback<Frase>() {
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
            ItemListaBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: FraseViewHolder, position: Int) {
        val fraseActual = differ.currentList[position]
        holder.itemBinding.txtTitulo.text = fraseActual.nombreFrase

        //Agregar click listener para el ícono de borrado de c/item
        holder.itemView.setOnClickListener {
            fraseClickInterface.onFraseClick((fraseActual))
        }

        //Listener de botón eliminar item
        holder.itemBinding.idIVDelete.setOnClickListener{
            fraseClickDeleteInterface.onDeleteFraseClick(fraseActual)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}

interface FraseClickDeleteInterface{
    fun onDeleteFraseClick(frase : Frase)
}

interface FraseClickInterface{
    fun onFraseClick(frase : Frase)
}