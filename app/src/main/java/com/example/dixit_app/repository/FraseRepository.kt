package com.example.dixit_app.repository

import com.example.dixit_app.model.DixitDatabase
import com.example.dixit_app.model.entidades.Frase
import com.example.dixit_app.model.entidades.FrasePictogramaRC

class FraseRepository (private val db: DixitDatabase) {
    //suspend fun insertFrase(frases: Frase) = db.getDixitDao().insertFrase(frases)
    suspend fun insertFrase(frase: Frase) : Long {
        return db.getDixitDao().insertFrase(frase)
    }

    suspend fun deleteFrase(frase: Frase) = db.getDixitDao().deleteFrase(frase)
    suspend fun updateFrase(frase: Frase) = db.getDixitDao().updateFrase(frase)
    fun getAllFrases() = db.getDixitDao().getAllFrases()
    fun searchFrase(query: String?) = db.getDixitDao().searchFrase(query)

    fun getIdFraseByNombre(nombre: String) = db.getDixitDao().getIdFraseByNombre(nombre)

    fun getAllPictogramas() = db.getDixitDao().getAllPictogramas()

    //Obtener los pictogramas de una frases
    fun getFrasePictogramas(frase : String) = db.getDixitDao().getFrasePictogramas(frase)

    suspend fun insertPictogramasFrase(pictofrase : FrasePictogramaRC) =
        db.getDixitDao().insertPictogramasFrase(pictofrase)
}