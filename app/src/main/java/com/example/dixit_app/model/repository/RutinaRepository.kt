package com.example.dixit_app.model.repository

import com.example.dixit_app.model.DixitDatabase
import com.example.dixit_app.model.entities.Rutina
import com.example.dixit_app.model.entities.RutinaPictogramaRC

class RutinaRepository (private val db: DixitDatabase) {
    //suspend fun insertRutina(rutina: Rutina) = db.getDixitDao().insertRutina(rutina)
    suspend fun insertRutina(rutina: Rutina) : Long {
        return db.getDixitDao().insertRutina(rutina)
    }

    suspend fun deleteRutina(rutina: Rutina) = db.getDixitDao().deleteRutina(rutina)

    suspend fun updateRutina(rutina: Rutina) = db.getDixitDao().updateRutina(rutina)

    fun searchRutina(query: String?) = db.getDixitDao().searchRutina(query)

    fun getAllRutinas() = db.getDixitDao().getAllRutinas()

    fun getIdRutinaByNombre(nombre: String) = db.getDixitDao().getIdRutinaByNombre(nombre)

    //Obtener los pictogramas de una frases
    fun getRutinaPictogramas(frase : String) = db.getDixitDao().getRutinaPictogramas(frase)

    suspend fun insertPictogramasRutina(pictorutina : RutinaPictogramaRC) =
        db.getDixitDao().insertPictogramasRutina(pictorutina)

    fun getAllPictogramas() = db.getDixitDao().getAllPictogramas()

    fun searchPictograma(query: String?) = db.getDixitDao().searchPictograma(query)
}