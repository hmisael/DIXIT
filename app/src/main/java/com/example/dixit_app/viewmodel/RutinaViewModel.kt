package com.example.dixit_app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dixit_app.model.entities.Rutina
import com.example.dixit_app.model.entities.RutinaPictogramaRC
import com.example.dixit_app.model.repository.RutinaRepository
import kotlinx.coroutines.launch

class RutinaViewModel (app: Application, private val rutinaRepository: RutinaRepository)
    : AndroidViewModel(app) {

    suspend fun insertRutina(rutina: Rutina) = rutinaRepository.insertRutina(rutina)

    fun deleteRutina(rutina: Rutina) =
        viewModelScope.launch {
            rutinaRepository.deleteRutina(rutina)
    }

    fun updateRutina(rutina: Rutina) =
        viewModelScope.launch {
            rutinaRepository.updateRutina(rutina)
    }

    fun getAllRutinas() = rutinaRepository.getAllRutinas()

    //Obtener todos los pictogramas
    fun getAllPictogramas() = rutinaRepository.getAllPictogramas()

    //Obtener los pictogramas de una Frase
    fun getRutinaPictogramas(rutina : String) = rutinaRepository.getRutinaPictogramas(rutina)

    fun searchRutina(query: String?) = rutinaRepository.searchRutina(query)

    fun insertPictogramasRutina(pictosrutina : RutinaPictogramaRC) =
        viewModelScope.launch {
            rutinaRepository.insertPictogramasRutina(pictosrutina)
        }

    //obtener id de una Frase según su nombre
    fun getIdRutinaByNombre(nombre: String) = rutinaRepository.getIdRutinaByNombre(nombre)


    fun searchPictograma(query: String?) =
        rutinaRepository.searchPictograma(query)

}