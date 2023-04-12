package com.example.dixit_app.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dixit_app.model.repository.CategoriaRepository
import com.example.dixit_app.model.repository.PictogramaRepository

class PictogramaViewModelFactory (val app: Application,
    private val pictogramaRepository: PictogramaRepository
):ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PictogramaViewModel(app, pictogramaRepository) as T
    }
}
