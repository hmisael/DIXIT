package com.example.dixit_app.view.rutinas

import android.content.Context
import android.net.Uri
import android.os.Environment
import android.speech.tts.TextToSpeech
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dixit_app.databinding.ItemPictogramaBinding
import com.example.dixit_app.model.entities.Pictograma
import java.io.File
import java.util.*

class PictogramasRutinaAdapter :  RecyclerView.Adapter<PictogramasRutinaAdapter.PictogramaViewHolder>() {

    //Para verbalizar pictogramas
    lateinit var tts : TextToSpeech
    private val locale = Locale("es", "ES")

    //El item xml se reutiliza
    class PictogramaViewHolder(val itemBinding: ItemPictogramaBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    private var imageUri: Uri? = null
    private var context : Context? = null

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PictogramaViewHolder {

        context = parent.context

        return PictogramaViewHolder(
            ItemPictogramaBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }


    override fun onBindViewHolder(holder: PictogramaViewHolder, position: Int) {

        val pictogramaActual = differ.currentList[position]

        //TEXTO DEL PICTOGRAMA: asigno nombre del pictograma a la View de texto
        holder.itemBinding.nombrePictograma.text = pictogramaActual.nombrePictograma

        //Recupero del almacenamiento físico el archivo de la imagen con el id del pictograma
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
        //Finalmente, asignar la imagen a la View de la imagen del pictograma

        Glide.with(holder.itemView.context)
            .load(imageUri)
            .centerCrop()
            .into(holder.itemBinding.imageViewPictograma)

        //Asignar título de Frase
        holder.itemBinding.nombrePictograma.setText(pictogramaActual.nombrePictograma)

        //Setear el clickListener de cada ítem de la lista, para redirigir l fragmento  ModificarPictograma
        holder.itemView.setOnClickListener { view ->
            //Al hacer clic en un elemento (pictograma) de la Frase, se verbaliza
            tts = TextToSpeech(context, TextToSpeech.OnInitListener {
                if (it == TextToSpeech.SUCCESS) {
                    tts.language = locale
                    tts.setSpeechRate(1.0f)
                    //Speech el nombre del Pictograma
                    tts.speak(pictogramaActual.nombrePictograma, TextToSpeech.QUEUE_ADD, null)
                }
            })
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


}