package com.example.dixit_app.model.repository

import com.example.dixit_app.model.DixitDatabase
import com.example.dixit_app.model.entities.Pictograma

class PictogramaRepository (private val db: DixitDatabase) {
    suspend fun insertPictograma(pictograma: Pictograma) = db.getDixitDao().insertPictograma(pictograma)
    suspend fun deletePictograma(pictograma: Pictograma) = db.getDixitDao().deletePictograma(pictograma)
    suspend fun updatePictograma(pictograma: Pictograma) = db.getDixitDao().updatePictograma(pictograma)
    fun getPictogramasByCategoria(nombreCategoria: String) = db.getDixitDao().getPictogramasByCategoria(nombreCategoria)

    fun getPictogramasByFrase(nombreFrase: String) = db.getDixitDao().getPictogramasByFrase(nombreFrase)
    fun getPictogramasByRutina(nombreRutina: String) = db.getDixitDao().getPictogramasByRutina(nombreRutina)

    fun getPictogramasByPregunta(nombrePregunta: String) =
        db.getDixitDao().getPictogramasByPregunta(nombrePregunta)

    fun getPictogramasByRespuesta(nombrePregunta: String) =
        db.getDixitDao().getPictogramasByRespuesta(nombrePregunta)

    fun getRespuesta(nombrePregunta: String) =
        db.getDixitDao().searchRespuesta(nombrePregunta)

    fun getAllPictogramas() = db.getDixitDao().getAllPictogramas()
    fun searchPictograma(query: String?) = db.getDixitDao().searchPictograma(query)




}