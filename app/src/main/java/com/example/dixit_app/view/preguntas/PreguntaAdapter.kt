package com.example.dixit_app.view.preguntas

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.dixit_app.databinding.ItemListaBinding
import com.example.dixit_app.model.entities.Pregunta

class PreguntaAdapter (val preguntaClickDeleteInterface: PreguntaClickDeleteInterface,
                       val preguntaClickInterface: PreguntaClickInterface):
                        RecyclerView.Adapter<PreguntaAdapter.PreguntaViewHolder>() {

    class PreguntaViewHolder(val itemBinding: ItemListaBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    private val differCallback =
        object : DiffUtil.ItemCallback<Pregunta>() {
            override fun areItemsTheSame(oldItem: Pregunta, newItem: Pregunta): Boolean {
                return oldItem.idPregunta == newItem.idPregunta &&
                        oldItem.nombrePregunta == newItem.nombrePregunta
            }
            override fun areContentsTheSame(oldItem: Pregunta, newItem: Pregunta): Boolean {
                return oldItem == newItem
            }
        }

    val differ = AsyncListDiffer(this, differCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PreguntaViewHolder {
        return PreguntaViewHolder(
            ItemListaBinding.inflate(
                LayoutInflater.from(parent.context), parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: PreguntaViewHolder, position: Int) {
        val preguntaActual = differ.currentList[position]
        holder.itemBinding.txtTitulo.text = preguntaActual.nombrePregunta
        holder.itemView.setOnClickListener { view ->
            preguntaClickInterface.onPreguntaClick(preguntaActual)
            /*val direction =
                PreguntasFragmentDirections
                    .actionPreguntasFragmentToPreguntaDetalleFragment(preguntaActual)
            view.findNavController().navigate(direction)*/
        }

        holder.itemBinding.idIVDelete.setOnClickListener{
            preguntaClickDeleteInterface.onDeletePreguntaClick(preguntaActual)
        }
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}

interface PreguntaClickDeleteInterface{
    fun onDeletePreguntaClick(pregunta : Pregunta)
}

interface PreguntaClickInterface{
    fun onPreguntaClick(pregunta : Pregunta)
}