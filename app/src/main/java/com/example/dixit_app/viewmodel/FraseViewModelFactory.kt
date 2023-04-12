package com.example.dixit_app.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.dixit_app.model.repository.CategoriaRepository
import com.example.dixit_app.model.repository.FraseRepository

class FraseViewModelFactory (
    val app: Application,
    private val fraseRepository: FraseRepository
) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            return FraseViewModel(app, fraseRepository) as T
        }
}