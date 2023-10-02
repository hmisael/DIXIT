package com.example.dixit_app.view.preguntas

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

class PictogramasPreguntaRVBottomAdapter (private val bottomAdapter : BottomAdapterClickPreguntaInterface) :
    RecyclerView.Adapter<PictogramasPreguntaRVBottomAdapter.PictogramaViewHolder>(){

    //El item xml se reutiliza (BottomRecyclerView) - Ambos RV lo usan
    class PictogramaViewHolder(val itemBinding: ItemPictogramaBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    private var imageUri: Uri? = null

    //Crear el Differ para los pictogramas del RV Bottom (Todos los pictos)
    private val differBottomCallback = object : DiffUtil.ItemCallback<Pictograma>() {
        override fun areItemsTheSame(oldItem: Pictograma, newItem: Pictograma): Boolean {
            return oldItem.idPictograma == newItem.idPictograma &&
                    oldItem.nombrePictograma == newItem.nombrePictograma
        }

        override fun areContentsTheSame(oldItem: Pictograma, newItem: Pictograma): Boolean {
            return oldItem == newItem
        }
    }

    val differBottom = AsyncListDiffer(this, differBottomCallback)

    //Este método devuelve un holder. Usa itemPictograma, una view de item ídentico para RV distintos
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PictogramaViewHolder {
        return PictogramaViewHolder(
            ItemPictogramaBinding.inflate(
            LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun onBindViewHolder(holder: PictogramaViewHolder, position: Int) {

        val pictogramaActual = differBottom.currentList[position]

        //Asignar nombre del pictograma a la View de texto
        holder.itemBinding.nombrePictograma.text = pictogramaActual.nombrePictograma

        //Recuperar del almacenamiento físico el archivo de la imagen con el id del pictograma
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
        //Finalmente, asigno la imagen a la View de la imagen del pictograma
        Glide.with(holder.itemView.context)
            .load(imageUri)
            .centerCrop()
            .into(holder.itemBinding.imageViewPictograma)

        //Asignar título de Pregunta
        //VERIFICAR
        //holder.itemBinding.nombrePictograma.setText(pictogramaActual.nombrePictograma)

        //Setear el clickListener de cada ítem de la lista, para redirigir l fragmento  ModificarPictograma
        holder.itemView.setOnClickListener { view ->
            bottomAdapter.onItemRVBottomClick(pictogramaActual)
            //Realizo copia de los resultados, casteando a una MutableList
            val copiedList = differBottom.currentList.toMutableList()
            //-----AGREGAR A LA LISTA de TOP el seleccionado
            //differBottom.currentList.add(differBottom.currentList[position])

            //Eliminar de la lista copiada el elemento de la posición clickeada

            if (copiedList.isEmpty()) {
                differBottom.submitList(emptyList())
            }
            else{
                //No borrar items del bottomTV
                //copiedList.removeAt(position)
                //Enviar la lista resultante al differ
                differBottom.submitList(copiedList)
                notifyItemRangeChanged(position, 1)
            }

        }
    }

    override fun getItemCount(): Int {
        return differBottom.currentList.size
    }


}

interface BottomAdapterClickPreguntaInterface{
    fun onItemRVBottomClick(pictograma : Pictograma)
}
