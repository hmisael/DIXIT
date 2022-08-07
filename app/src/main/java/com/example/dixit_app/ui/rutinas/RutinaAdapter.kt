package com.example.dixit_app.ui.rutinas

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.dixit_app.databinding.ItemListaBinding
import com.example.dixit_app.model.entidades.Frase
import com.example.dixit_app.model.entidades.Rutina
import com.example.dixit_app.ui.frases.FraseClickDeleteInterface
import com.example.dixit_app.ui.frases.FraseClickInterface
import com.google.android.material.snackbar.Snackbar

//Este Adapter muestra los Pictogramas que pertenecen a una Rutina
class RutinaAdapter (val rutinaClickDeleteInterface: RutinaClickDeleteInterface,
                     val rutinaClickInterface: RutinaClickInterface):
                     RecyclerView.Adapter<RutinaAdapter.RutinaViewHolder>() {

    class RutinaViewHolder(val itemBinding: ItemListaBinding) :
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
            ItemListaBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: RutinaViewHolder, position: Int) {
        val rutinaActual = differ.currentList[position]
        holder.itemBinding.txtTitulo.text = rutinaActual.nombreRutina

        //Agregar click listener para el ícono de borrado de c/item
        holder.itemView.setOnClickListener { view ->
            /*Snackbar.make(
                view, "Nombre: "+rutinaActual.nombreRutina+" - ID: "+rutinaActual.idRutina,
                Snackbar.LENGTH_SHORT
            ).show()*/

            rutinaClickInterface.onRutinaClick(rutinaActual)

        }


        //Listener de icono eliminar item
        holder.itemBinding.idIVDelete.setOnClickListener{
            rutinaClickDeleteInterface.onDeleteRutinaClick(rutinaActual)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}



interface RutinaClickDeleteInterface{
    fun onDeleteRutinaClick(rutina : Rutina)
}

interface RutinaClickInterface{
    fun onRutinaClick(rutina : Rutina)
}