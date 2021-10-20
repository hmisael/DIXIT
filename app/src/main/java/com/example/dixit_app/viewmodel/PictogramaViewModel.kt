package com.example.dixit_app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dixit_app.model.entidades.Pictograma
import com.example.dixit_app.repository.PictogramaRepository
import kotlinx.coroutines.launch
import java.io.File

class PictogramaViewModel(app: Application,
                          private val pictogramaRepository: PictogramaRepository
) : AndroidViewModel(app) {

    fun insertPictograma(pictograma: Pictograma) =
            viewModelScope.launch {
                pictogramaRepository.insertPictograma(pictograma)
            }

    fun deletePictograma(pictograma: Pictograma) =
            viewModelScope.launch {
                pictogramaRepository.deletePictograma(pictograma)
            }

    fun updatePictograma(pictograma: Pictograma) =
            viewModelScope.launch {
                pictogramaRepository.updatePictograma(pictograma)
            }

    fun getPictogramasByCategoria(nombreCategoria: String) = pictogramaRepository.getPictogramasByCategoria(nombreCategoria)

    fun getPictogramasByFrase(nombreFrase: String) = pictogramaRepository.getPictogramasByFrase(nombreFrase)


    fun getAllPictogramas() = pictogramaRepository.getAllPictogramas()



    fun searchPictograma(query: String?) =
            pictogramaRepository.searchPictograma(query)

}

