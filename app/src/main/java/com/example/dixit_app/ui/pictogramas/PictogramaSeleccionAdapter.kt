package com.example.dixit_app.ui.pictogramas

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.dixit_app.databinding.ItemPictogramaBinding
import com.example.dixit_app.model.entidades.Pictograma
import java.io.File

class PictogramaSeleccionAdapter: RecyclerView.Adapter<PictogramaSeleccionAdapter.PictogramaViewHolder>() {

    var selectedList = mutableListOf<Int>()

    class PictogramaViewHolder(val itemBinding: ItemPictogramaBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    private var imageUri: Uri? = null

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





    //---------------------------------------------------------------

    //método para armar una lista de Pictograma seleccionados

    private var listData: MutableList<Pictograma> = differ.currentList as MutableList<Pictograma>
    private var listSelected: MutableList<Boolean> = ArrayList()
    //---------------------------------------------------------------

    /**Llama a este método siempre que necesita crear una ViewHolder nueva. El método crea y, luego,
     * inicializa la ViewHolder y su View asociada, pero no completa el contenido de la vista;
     * aún no se vinculó la ViewHolder con datos específicos.**/

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PictogramaViewHolder {
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
        //EDIT 5 de abril, necesito el uri de pictograma guardado
        //val file = File(holder.itemBinding.root.context.filesDir, pictogramaActual.idPictograma.toString())
        val file = File(holder.itemBinding.root.context.filesDir, pictogramaActual.imagen)

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
        holder.itemBinding.imageViewPictograma.setImageURI(imageUri)


        //---------------------------------------------------------------
        //Al presionar el ítem un tiempo largo, se selecciona
        holder.itemView.setOnLongClickListener {
            markSelectedItem(position)
        }
        //Al presionar el ítem, se deselecciona
        holder.itemView.setOnClickListener() {
            deselectedItem(position)
        }
        //---------------------------------------------------------------
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }



    //---------------------------------------------------------------
    //Métodos para seleccionar un ítem o varios
    fun deselectedItem(index: Int){
        if (selectedList.contains(index)){
            selectedList.remove(index)

            //listData[index].selected = false
            //lo reemplazo por...
            listSelected[index] = false

            notifyDataSetChanged()
            showHideDelete(selectedList.isNotEmpty())
        }
    }

    fun markSelectedItem(index: Int): Boolean{
        //for (item in listData){
        //Lo reemplazo por
        for (item in listSelected){
          //  item = false
        }

        if (!selectedList.contains(index)) {
            selectedList.add(index)
        }

        selectedList.forEach{

            //listData[it].selected = true
            //Lo reemplazo por
            listSelected[index] = true
        }

        notifyDataSetChanged()
        showHideDelete(true)
        return true
    }

    fun deleteSelectedItem(){
        if (selectedList.isNotEmpty()){
            //listData.removeAll(item -> item.selected == true)
        }
        notifyDataSetChanged()
    }

    private fun showHideDelete(show: Boolean){
        //mainMenu?.findItem(R.id.menu_delete)?.isVisible = show
    }
    //---------------------------------------------------------------




}

