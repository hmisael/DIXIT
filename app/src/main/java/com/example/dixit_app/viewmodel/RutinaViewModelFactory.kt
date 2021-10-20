package com.example.dixit_app.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dixit_app.repository.CategoriaRepository
import com.example.dixit_app.repository.RutinaRepository

class RutinaViewModelFactory(
    val app: Application,
    private val rutinaRepository: RutinaRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return RutinaViewModel(app, rutinaRepository) as T
    }
}