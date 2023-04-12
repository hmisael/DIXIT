package com.example.dixit_app.view.categorias

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.dixit_app.databinding.ItemListaBinding
import com.example.dixit_app.model.entities.Categoria

class CategoriaAdapter (val categoriaClickDeleteInterface: CategoriaClickDeleteInterface,
                        val categoriaClickInterface: CategoriaClickInterface):
                    RecyclerView.Adapter<CategoriaAdapter.CategoriaViewHolder>() {

    class CategoriaViewHolder(val itemBinding: ItemListaBinding) :
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
            ItemListaBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: CategoriaViewHolder, position: Int) {
        val categoriaActual = differ.currentList[position]
        holder.itemBinding.txtTitulo.text = categoriaActual.nombreCategoria

        //Agregar click listener para el ícono de borrado de c/item
        holder.itemView.setOnClickListener {
            categoriaClickInterface.onCategoriaClick(categoriaActual)
        }

        //Listener de icono eliminar item
        holder.itemBinding.idIVDelete.setOnClickListener{
            categoriaClickDeleteInterface.onDeleteCategoriaClick(categoriaActual)
        }

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}

interface CategoriaClickDeleteInterface{
    fun onDeleteCategoriaClick(categoria : Categoria)
}

interface CategoriaClickInterface{
    fun onCategoriaClick(categoria : Categoria)
}
