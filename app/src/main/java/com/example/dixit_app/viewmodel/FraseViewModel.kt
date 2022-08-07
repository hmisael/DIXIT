package com.example.dixit_app.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.dixit_app.model.entidades.Frase
import com.example.dixit_app.model.entidades.FrasePictogramaRC
import com.example.dixit_app.repository.FraseRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FraseViewModel(app: Application, private val fraseRepository: FraseRepository)
        : AndroidViewModel(app) {

    suspend fun insertFrase(frase: Frase) = fraseRepository.insertFrase(frase)

    fun deleteFrase(frase: Frase) =
        viewModelScope.launch {
            fraseRepository.deleteFrase(frase)
    }

    fun updateFrase(frase: Frase) =
        viewModelScope.launch {
            fraseRepository.updateFrase(frase)
    }

    fun getAllFrases() = fraseRepository.getAllFrases()

    //Obtener todos los pictogramas
    fun getAllPictogramas() = fraseRepository.getAllPictogramas()

    //Obtener los pictogramas de una Frase
    fun getFrasePictogramas(frase : String) = fraseRepository.getFrasePictogramas(frase)

    fun searchFrase(query: String?) =
            fraseRepository.searchFrase(query)

    fun insertPictogramasFrase(pictosfrase : FrasePictogramaRC) =
        viewModelScope.launch {
            fraseRepository.insertPictogramasFrase(pictosfrase)
        }

    //obtener id de una Frase según su nombre
    fun getIdFraseByNombre(nombre: String) = fraseRepository.getIdFraseByNombre(nombre)
}