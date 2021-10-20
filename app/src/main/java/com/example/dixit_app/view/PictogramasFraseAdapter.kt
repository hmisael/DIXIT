package com.example.dixit_app.view

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dixit_app.R
import com.example.dixit_app.databinding.ItemChooseBinding
import com.example.dixit_app.databinding.ItemPictogramaBinding
import com.example.dixit_app.model.entidades.Pictograma
import kotlinx.coroutines.withContext
import java.io.File

//Muestra los pictogramas de una Frase determinada OK
class PictogramasFraseAdapter :  RecyclerView.Adapter<PictogramasFraseAdapter.PictogramaViewHolder>() {


    //El item xml se reutiliza
    class PictogramaViewHolder(val itemBinding: ItemPictogramaBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    private var imageUri: Uri? = null


    private var context : Context? = null

    private var layoutId = 0


    private val differCallback =
        object : DiffUtil.ItemCallback<Pictograma>() {
            override fun areItemsTheSame(oldItem: Pictograma, newItem: Pictograma): Boolean {
                return oldItem.idPictograma == newItem.idPictograma &&
                        oldItem.nombrePictograma == newItem.nombrePictograma
            }

            override fun areContentsTheSame(oldItem: Pictograma, newItem: Pictograma): Boolean {
                return oldItem == newItem
            }
        }

    val differ = AsyncListDiffer(this, differCallback)

    /**Llama a este método siempre que necesita crear una ViewHolder nueva. El método crea y, luego,
     * inicializa la ViewHolder y su View asociada, pero no completa el contenido de la vista;
     * aún no se vinculó la ViewHolder con datos específicos.**/

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PictogramaViewHolder {

        context = parent.context

        return PictogramaViewHolder(
            ItemPictogramaBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: PictogramaViewHolder, position: Int) {

        val pictogramaActual = differ.currentList[position]     // A


        if (layoutId == R.layout.item_choose){
             var chooseItem : ItemChooseBinding = ItemChooseBinding.bind(holder.itemView) // ???

        }


        //TEXTO DEL PICTOGRAMA: asigno nombre del pictograma a la View de texto
        holder.itemBinding.nombrePictograma.text = pictogramaActual.nombrePictograma

        //Recupero del almacenamiento físico el archivo de la imagen con el id del pictograma
        //VERIFICAR si se puede cambiar el directorio, pues tomo el de holder
        val file = File(
            holder.itemBinding.root.context.getExternalFilesDir(Environment.DIRECTORY_DCIM),
            pictogramaActual.nombrePictograma + ".jpeg")
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
            differ.currentList.removeAt(position)
            notifyItemRemoved(position);
            notifyItemRangeChanged(position,1);

        }

        

    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }




}

