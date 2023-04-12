package com.example.dixit_app.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dixit_app.model.repository.CategoriaRepository

class CategoriaViewModelFactory(
    val app: Application,
    private val categoriaRepository: CategoriaRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CategoriaViewModel(app, categoriaRepository) as T
    }
}