package com.example.dixit_app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dixit_app.model.entidades.Categoria
import com.example.dixit_app.repository.CategoriaRepository
import kotlinx.coroutines.launch

class CategoriaViewModel (
    app: Application,
    private val categoriaRepository: CategoriaRepository
) : AndroidViewModel(app) {

    fun insertCategoria(categoria: Categoria) =
        viewModelScope.launch {
            categoriaRepository.insertCategoria(categoria)
        }

    fun deleteCategoria(categoria: Categoria) =
        viewModelScope.launch {
            categoriaRepository.deleteCategoria(categoria)
        }

    fun updateCategoria(categoria: Categoria) =
        viewModelScope.launch {
            categoriaRepository.updateCategoria(categoria)
        }

    fun getAllCategorias() = categoriaRepository.getAllCategorias()

    fun searchCategoria(query: String?) =
        categoriaRepository.searchCategoria(query)
}