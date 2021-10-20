package com.example.dixit_app.repository

import com.example.dixit_app.model.DixitDatabase
import com.example.dixit_app.model.entidades.Categoria

class CategoriaRepository (private val db: DixitDatabase) {
    suspend fun insertCategoria(categoria: Categoria) = db.getDixitDao().insertCategoria(categoria)
    suspend fun deleteCategoria(categoria: Categoria) = db.getDixitDao().deleteCategoria(categoria)
    suspend fun updateCategoria(categoria: Categoria) = db.getDixitDao().updateCategoria(categoria)
    fun getAllCategorias() = db.getDixitDao().getAllCategorias()
    fun searchCategoria(query: String?) = db.getDixitDao().searchCategoria(query)
}