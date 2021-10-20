package com.example.dixit_app.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.dixit_app.databinding.ItemCategoriaBinding
import com.example.dixit_app.model.entidades.Categoria
import com.google.android.material.snackbar.Snackbar

class CategoriaAdapter: RecyclerView.Adapter<CategoriaAdapter.CategoriaViewHolder>() {

    class CategoriaViewHolder(val itemBinding: ItemCategoriaBinding) :
            RecyclerView.ViewHolder(itemBinding.root)

    private val differCallback =
            object : DiffUtil.ItemCallback<Categoria>() {
                override fun areItemsTheSame(oldItem: Categoria, newItem: Categoria): Boolean {
                    return oldItem.idCategoria == newItem.idCategoria &&
                            oldItem.nombreCategoria == newItem.nombreCategoria
                }
                override fun areContentsTheSame(oldItem: Categoria, newItem: Categoria): Boolean {
                    return oldItem == newItem
                }
            }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoriaViewHolder {
        return CategoriaViewHolder(
            ItemCategoriaBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: CategoriaViewHolder, position: Int) {
        val categoriaActual = differ.currentList[position]
        holder.itemBinding.nombreCategoria.text = categoriaActual.nombreCategoria
        holder.itemView.setOnClickListener { view ->
            Snackbar.make(
                    view, "Nombre: "+categoriaActual.nombreCategoria+" - ID: "+categoriaActual.idCategoria,
                    Snackbar.LENGTH_SHORT
            ).show()
            val direction = CategoriasFragmentDirections
                .actionCategoriasFragmentToCategoriaDetalleFragment(categoriaActual)
            view.findNavController().navigate(direction)
        }

    /*METODO PARA tres puntos MODIFICACION CATEGORIA
        holder.itemView.setOnClickListener { view ->
            val direction = RutinasFragmentDirections
                .actionRutinasFragmentToRutinaModificarFragment(rutinaActual)
            view.findNavController().navigate(direction)
        }
    */
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}