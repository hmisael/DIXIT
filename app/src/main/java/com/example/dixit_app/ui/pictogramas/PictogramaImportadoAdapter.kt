package com.example.dixit_app.ui.pictogramas

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dixit_app.databinding.ItemPictogramaBinding
import java.io.File

//class PictogramaImportadoAdapter (private val uris: List<Uri>):
class PictogramaImportadoAdapter (private val uris: LinkedHashSet<Uri>):
RecyclerView.Adapter<PictogramaImportadoAdapter.PictogramaViewHolder>() {


    //Vista para el item
    class PictogramaViewHolder(val itemBinding: ItemPictogramaBinding) :
        RecyclerView.ViewHolder(itemBinding.root){
            fun bind(uri : Uri){
                itemBinding.nombrePictograma.text = "ALgún valor"
                itemBinding.imageViewPictograma.setImageURI(uri)
                /*
                Glide.with(itemBinding.root.context)
                    .load(File(uri.getPath()!!))
                    .centerCrop()
                    .into(itemBinding.imageViewPictograma)*/
            }

        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PictogramaViewHolder {
        return PictogramaViewHolder(ItemPictogramaBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: PictogramaViewHolder, position: Int) {

        //holder.bind(uris[position])


        holder.bind(uris.elementAt(position))

        /*
        Glide.with(contexto)
            .load(uris[position].path)
            .centerCrop()
            .into(holder.itemBinding.imageViewPictograma)
        */
        Log.i("PUTA"," ADAPTER: "+uris.elementAt(position).path)

    }

    override fun getItemCount() = uris.size


/*
    fun updateList(newList: List<Uri?>) {
        //Limpiar el listado
        uriList.clear()
        //Agregar una nueva lista
        uriList.addAll(newList)
        //Método para notificar al adapter la actualización del listado
        //notifyDataSetChanged()
    }
*/
}

