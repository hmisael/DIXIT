package com.example.dixit_app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dixit_app.model.entidades.Categoria
import com.example.dixit_app.model.entidades.Frase
import com.example.dixit_app.repository.CategoriaRepository
import com.example.dixit_app.repository.FraseRepository
import kotlinx.coroutines.launch

class FraseViewModel(
    app: Application,
    private val fraseRepository: FraseRepository
    ) : AndroidViewModel(app) {

        fun insertFrase(frase: Frase) =
            viewModelScope.launch {
                fraseRepository.insertFrase(frase)
            }

        fun deleteFrase(frase: Frase) =
            viewModelScope.launch {
                fraseRepository.deleteFrase(frase)
            }

        fun updateFrase(frase: Frase) =
            viewModelScope.launch {
                fraseRepository.updateFrase(frase)
            }

        fun getAllFrases() = fraseRepository.getAllFrases()

        fun searchFrase(query: String?) =
            fraseRepository.searchFrase(query)

    }