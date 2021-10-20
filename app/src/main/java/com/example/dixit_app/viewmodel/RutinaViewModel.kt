package com.example.dixit_app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dixit_app.model.entidades.Categoria
import com.example.dixit_app.model.entidades.Rutina
import com.example.dixit_app.repository.RutinaRepository
import kotlinx.coroutines.launch

class RutinaViewModel (
    app: Application,
    private val rutinaRepository: RutinaRepository
    ) : AndroidViewModel(app) {

    fun insertRutina(rutina: Rutina) =
        viewModelScope.launch {
            rutinaRepository.insertRutina(rutina)
        }

    fun deleteRutina(rutina: Rutina) =
        viewModelScope.launch {
            rutinaRepository.deleteRutina(rutina)
        }

    fun updateRutina(rutina: Rutina) =
        viewModelScope.launch {
            rutinaRepository.updateRutina(rutina)
        }

    fun getAllRutinas() = rutinaRepository.getAllRutinas()

    fun searchRutina(query: String?) =
        rutinaRepository.searchRutina(query)
}