package com.example.dixit_app.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dixit_app.repository.FraseRepository
import com.example.dixit_app.repository.PreguntaRepository

class PreguntaViewModelFactory (
    val app: Application,
    private val preguntaRepository: PreguntaRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return PreguntaViewModel(app, preguntaRepository) as T
    }
}