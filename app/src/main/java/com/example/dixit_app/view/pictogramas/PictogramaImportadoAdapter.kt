package com.example.dixit_app.view.pictogramas

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dixit_app.databinding.ItemPictogramaBinding
import java.io.File

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
        //holder.bind(uris.elementAt(position), names.elementAt(position))
        holder.bind(uris.elementAt(position), getLetras(names.elementAt(position)))

        Log.i("MOD"," ADAPTER: "+uris.elementAt(position).path+" - nombre: "+names.elementAt(position))
    }

    override fun getItemCount() = uris.size




    private fun getLetras(cadena: String): String {
        val result : MutableList<String> = mutableListOf<String>()
        var numberStr = ""
        for(i : Int in 0 until cadena.length){
            val c: Char = cadena[i]
            //Eliminar guión bajo para lectura correcta
            if (c == '_') {
                numberStr += ' '
                if (i == cadena.length - 1) {
                    result.add(numberStr.toString())
                }
            }
            //No se eliminan números al realizar un importe masivo de imágenes,
            //puesto que para un término pueden existir más de 1 imagen relacionada
            /*else {
                //Eliminar números
                if (c !in '0'..'9') {
                    numberStr += c
                    if (i == cadena.length - 1) {
                        result.add(numberStr.toString())
                    }
                    //Y finalmente si está vacía la cadena
                } else if (!numberStr.isNullOrBlank()) {
                    result.add(numberStr.toString())
                    numberStr = ""
                }
            }*/
        }
        return result.joinToString(File.separator, "")
    }


}

