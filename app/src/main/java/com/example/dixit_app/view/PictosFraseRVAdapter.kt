package com.example.dixit_app.view

import android.net.Uri
import android.os.Environment
import android.view.*
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dixit_app.databinding.ItemPictogramaBinding
import com.example.dixit_app.databinding.ItemPictogramaSelectedBinding
import com.example.dixit_app.helper.BaseViewHolder
import com.example.dixit_app.model.entidades.Pictograma
import java.io.File


class PictosFraseRVAdapter:
    RecyclerView.Adapter<PictosFraseRVAdapter.PictogramaViewHolder>(){

    //-----------------------------------


    //El item xml se reutiliza (BottomRecyclerView)
    class PictogramaViewHolder(val itemBinding: ItemPictogramaBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    private var imageUri: Uri? = null


    //-----------------------------------

    //Creo el Differ para los pictogramas del RV Bottom (Todos los pictos)
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


    //-----------------------------------


    /**Llama a este método siempre que necesita crear una ViewHolder nueva. El método crea y, luego,
     * inicializa la ViewHolder y su View asociada, pero no completa el contenido de la vista;
     * aún no se vinculó la ViewHolder con datos específicos.**/


    //Este método devuelve un holder. Yo tengo dos tipos (el pictograma y pictogramaselected)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PictogramaViewHolder {

        return PictogramaViewHolder(ItemPictogramaBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false)
                )

    }


    override fun onBindViewHolder(holder: PictogramaViewHolder, position: Int) {

        val pictogramaActual = differBottom.currentList[position]

        //TEXTO DEL PICTOGRAMA: asigno nombre del pictograma a la View de texto
        holder.itemBinding.nombrePictograma.text = pictogramaActual.nombrePictograma

        //Recupero del almacenamiento físico el archivo de la imagen con el id del pictograma
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


        //Seteo el clickListener de cada ítem de la lista, para redirigir l fragmento  ModificarPictograma
        holder.itemView.setOnClickListener { view ->
            //Realizo copia de los resultados, casteando a una MutableList
            val copiedList = differBottom.currentList.toMutableList()
            //Elimino de la lista copiada el elemento de la posición clickeada
            copiedList.removeAt(position)
            //Le envío la lista resultante al differ
            differBottom.submitList(copiedList)


            //notifyItemRangeChanged(position, 1)
        }



    }



    override fun getItemCount(): Int {
        return differBottom.currentList.size
    }





}

