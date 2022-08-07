package com.example.dixit_app.repository

import com.example.dixit_app.model.DixitDatabase
import com.example.dixit_app.model.entidades.Frase
import com.example.dixit_app.model.entidades.FrasePictogramaRC
import com.example.dixit_app.model.entidades.Rutina
import com.example.dixit_app.model.entidades.RutinaPictogramaRC

class RutinaRepository (private val db: DixitDatabase) {
    //suspend fun insertRutina(rutina: Rutina) = db.getDixitDao().insertRutina(rutina)
    suspend fun insertRutina(rutina: Rutina) : Long {
        return db.getDixitDao().insertRutina(rutina)
    }

    suspend fun deleteRutina(rutina: Rutina) = db.getDixitDao().deleteRutina(rutina)
    suspend fun updateRutina(rutina: Rutina) = db.getDixitDao().updateRutina(rutina)
    fun getAllRutinas() = db.getDixitDao().getAllRutinas()
    fun searchRutina(query: String?) = db.getDixitDao().searchRutina(query)



    fun getIdRutinaByNombre(nombre: String) = db.getDixitDao().getIdRutinaByNombre(nombre)

    fun getAllPictogramas() = db.getDixitDao().getAllPictogramas()

    //Obtener los pictogramas de una frases
    fun getRutinaPictogramas(frase : String) = db.getDixitDao().getRutinaPictogramas(frase)

    suspend fun insertPictogramasRutina(pictorutina : RutinaPictogramaRC) =
        db.getDixitDao().insertPictogramasRutina(pictorutina)

}