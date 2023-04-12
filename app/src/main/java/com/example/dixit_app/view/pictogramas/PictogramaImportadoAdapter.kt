package com.example.dixit_app.view.pictogramas

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dixit_app.databinding.ItemPictogramaBinding

//class PictogramaImportadoAdapter (private val uris: LinkedHashSet<Uri>, private val names: LinkedHashSet<String>):
class PictogramaImportadoAdapter (private val uris: ArrayList<Uri>, private val names: ArrayList<String>):
RecyclerView.Adapter<PictogramaImportadoAdapter.PictogramaViewHolder>() {

    //Vista para el item
    class PictogramaViewHolder(val itemBinding: ItemPictogramaBinding) :
        RecyclerView.ViewHolder(itemBinding.root){
            fun bind(uri : Uri, nombre : String){
                itemBinding.nombrePictograma.text = nombre
                itemBinding.imageViewPictograma.setImageURI(uri)
            }
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PictogramaViewHolder {
        return PictogramaViewHolder(ItemPictogramaBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: PictogramaViewHolder, position: Int) {
        holder.bind(uris.elementAt(position), names.elementAt(position))
        Log.i("MOD"," ADAPTER: "+uris.elementAt(position).path+" - nombre: "+names.elementAt(position))
    }

    override fun getItemCount() = uris.size

}

