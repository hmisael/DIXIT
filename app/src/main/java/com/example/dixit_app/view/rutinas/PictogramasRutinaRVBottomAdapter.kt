package com.example.dixit_app.view.rutinas

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

class PictogramasRutinaRVBottomAdapter (private val bottomAdapter : BottomAdapterClickRutinaInterface) :
    RecyclerView.Adapter<PictogramasRutinaRVBottomAdapter.PictogramaViewHolder>(){

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

        //Recupero del almacenamiento físico el archivo de la imagen con el id del pictograma
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


        //Asignar título de Rutina
        //VERIFICAR
        //holder.itemBinding.nombrePictograma.setText(pictogramaActual.nombrePictograma)

        //Seteo el clickListener de cada ítem de la lista, para redirigir l fragmento  ModificarPictograma
        holder.itemView.setOnClickListener {
            bottomAdapter.onItemRVBottomClick(pictogramaActual)
            //Realizo copia de los resultados, casteando a una MutableList
            val copiedList = differBottom.currentList.toMutableList()
            //-----agregar a la lista de topRV el seleccionado
            //differBottom.currentList.add(differBottom.currentList[position])
            //Y elimino de la lista copiada el elemento de la posición clickeada
            //TODO chequear funcionamiento
            if (copiedList.isEmpty()) {
                differBottom.submitList(emptyList())
            }
            else{
                //Al hacer clic en item de bottomTV no se elimina, solo se agrega al topRV
                //Le envío la lista resultante al differ
                differBottom.submitList(copiedList)
                notifyItemRangeChanged(position, 1)
            }
        }
    }

    override fun getItemCount(): Int {
        return differBottom.currentList.size
    }


}

interface BottomAdapterClickRutinaInterface{
    fun onItemRVBottomClick(pictograma : Pictograma)
}
