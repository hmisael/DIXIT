package com.example.dixit_app.repository

import com.example.dixit_app.model.DixitDatabase
import com.example.dixit_app.model.entidades.Categoria
import com.example.dixit_app.model.entidades.Frase

class FraseRepository (private val db: DixitDatabase) {
    suspend fun insertFrase(frase: Frase) = db.getDixitDao().insertFrase(frase)
    suspend fun deleteFrase(frase: Frase) = db.getDixitDao().deleteFrase(frase)
    suspend fun updateFrase(frase: Frase) = db.getDixitDao().updateFrase(frase)
    fun getAllFrases() = db.getDixitDao().getAllFrases()
    fun searchFrase(query: String?) = db.getDixitDao().searchFrase(query)

}