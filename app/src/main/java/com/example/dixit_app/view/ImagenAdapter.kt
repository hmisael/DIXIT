package com.example.dixit_app.view

import android.net.Uri
import android.os.Environment
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dixit_app.databinding.ItemPictogramaBinding
import com.example.dixit_app.model.entidades.Imagen
import java.io.File


//Adapter para las imágenes guardadas en almacenamiento interno
// Es usado por FraseModificarFragment (muestra imágenes guardadas por carpeta/categoría)


class ImagenAdapter : Fragment(){}//NO ES FRAGMENT, SOLO IMPORTA LO SIGUIENTE:
/*: RecyclerView.Adapter<ImagenAdapter.ImagenViewHolder>() {


    //mismo Item Pictograma
    class ImagenViewHolder(val itemBinding: ItemPictogramaBinding) :
        RecyclerView.ViewHolder(itemBinding.root)



    private var imageUri: Uri? = null

    private val differCallback =
        object : DiffUtil.ItemCallback<Imagen>() {
            override fun areItemsTheSame(oldItem: Imagen, newItem: Imagen): Boolean {
                return oldItem.uri == newItem.uri &&
                        oldItem.nombre == newItem.nombre
            }

            override fun areContentsTheSame(oldItem: Imagen, newItem: Imagen): Boolean {
                return oldItem == newItem
            }
        }

    val differ = AsyncListDiffer(this, differCallback)

    /**Llama a este método siempre que necesita crear una ViewHolder nueva. El método crea y, luego,
     * inicializa la ViewHolder y su View asociada, pero no completa el contenido de la vista;
     * aún no se vinculó la ViewHolder con datos específicos.**/

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImagenViewHolder {
        return ImagenViewHolder(
            ItemPictogramaBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: ImagenViewHolder, position: Int) {
        val imagenActual = differ.currentList[position]

        //TEXTO DEL PICTOGRAMA: asigno nombre del pictograma a la View de texto
        //El binding corresponde al item Pictograma (info ídem a ítem Imagen)
        holder.itemBinding.nombrePictograma.text = imagenActual.nombre

        //Recupero del almacenamiento físico el archivo de la imagen con el id del pictograma
        //ESTABA ESTO

        //Guardo el path del directorio Descargas
        val dir = File(holder.itemBinding.root.context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)!!.absolutePath)

        //    holder.itemBinding.root.context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS),
        //    imagenActual.nombre + ".jpeg")

        //Si el directorio existe
        if (dir.exists()) {
            //Creo una lista de archivos en el directorio
            val files = dir.listFiles()

            for (file in files) {
                val absoluto = file.absolutePath
                val extension = absoluto.substring(absoluto.lastIndexOf("."))
                if (extension.equals(extension)) {
                    imageUri = Uri.fromFile(file)
                    //holder.itemBinding.imageViewPictograma.setImageURI(Uri.fromFile(file))

                }
            }
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
            val direction = CategoriaDetalleFragmentDirections
                .actionCategoriaDetalleFragmentToPictogramaModificarFragment(imagenActual)
            view.findNavController().navigate(direction)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }





}*/