package com.example.dixit_app.view.frases

import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dixit_app.databinding.ItemPictogramaBinding
import com.example.dixit_app.model.entities.Pictograma
import java.io.File


class PictogramasFraseRVTopAdapter (val topAdapter : TopAdapterClickFraseInterface) :
    RecyclerView.Adapter<PictogramasFraseRVTopAdapter.PictogramaTopViewHolder>(){

    private var imageUri: Uri? = null

    //Holder para itemPictogramaSelected (TopRecyclerView)
    class PictogramaTopViewHolder(val itemBinding: ItemPictogramaBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    //Creo el Differ para los pictogramas del RV Top (pictos guardados en Frase)

    private val differTopCallback = object : DiffUtil.ItemCallback<Pictograma>() {
        override fun areItemsTheSame(oldItem: Pictograma, newItem: Pictograma): Boolean {
            return oldItem.idPictograma == newItem.idPictograma &&
                    oldItem.nombrePictograma == newItem.nombrePictograma
        }

        override fun areContentsTheSame(oldItem: Pictograma, newItem: Pictograma): Boolean {
            return oldItem == newItem
        }
    }

    val differTop = AsyncListDiffer(this, differTopCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PictogramaTopViewHolder {
        return PictogramaTopViewHolder(
            ItemPictogramaBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: PictogramaTopViewHolder, position: Int) {

        val pictogramaActual = differTop.currentList[position]

        //TEXTO DEL PICTOGRAMA: asigno nombre del pictograma a la View de texto
        holder.itemBinding.nombrePictograma.text = pictogramaActual.nombrePictograma

        //Recuperar del almacenamiento físico el archivo de la imagen con el id del pictograma
        //VERIFICAR si se puede cambiar el directorio, pues tomo el de holder
        val file = File(
            holder.itemBinding.root.context.getExternalFilesDir(Environment.DIRECTORY_DCIM),
            pictogramaActual.nombrePictograma + ".jpeg"
        )
        //Si el archivo existe, obtengo su URI
        if (file.exists()) {
            imageUri = Uri.fromFile(file)
        }
        //Y si no existe, asigno una imagen por defecto
        else {
            imageUri =
                Uri.parse("android.resource://com.example.dixit_app/drawable/placeholder")
        }
        //IMAGEN PICTOGRAMA: Finalmente, asigno la imagen a la View de la imagen del pictograma
        //holder.itemBinding.imageViewPictograma.setImageURI(imageUri)
        Glide.with(holder.itemView.context)
            .load(imageUri)
            .centerCrop()
            .into(holder.itemBinding.imageViewPictograma)

        //Asignar título de Frase
        holder.itemBinding.nombrePictograma.setText(pictogramaActual.nombrePictograma)

        //Seteo el clickListener de cada ítem de la lista, para redirigir l fragmento  ModificarPictograma
        holder.itemView.setOnClickListener {

            topAdapter.onItemRVTopClick(pictogramaActual)
            //Realizo copia de los resultados, casteando a una MutableList
            val copiedList = differTop.currentList.toMutableList()

            if (copiedList.isEmpty()) {
                differTop.submitList(emptyList())
            }
            else {
                //Elimino de la lista copiada el elemento de la posición clickeada
                copiedList.removeAt(position)
                //Enviar la lista resultante al differ
                differTop.submitList(emptyList())
                differTop.submitList(copiedList)


                //TODO verificar implementación
                //notifyItemRemoved(position)
                //notifyItemRangeChanged(position, 1)

            }
        }
    }

    override fun getItemCount(): Int {
        return differTop.currentList.size
    }
}


interface TopAdapterClickFraseInterface{
    fun onItemRVTopClick(pictograma : Pictograma)
}