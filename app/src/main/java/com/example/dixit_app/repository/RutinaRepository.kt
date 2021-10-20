package com.example.dixit_app.repository

import com.example.dixit_app.model.DixitDatabase
import com.example.dixit_app.model.entidades.Rutina

class RutinaRepository (private val db: DixitDatabase) {
    suspend fun insertRutina(rutina: Rutina) = db.getDixitDao().insertRutina(rutina)
    suspend fun deleteRutina(rutina: Rutina) = db.getDixitDao().deleteRutina(rutina)
    suspend fun updateRutina(rutina: Rutina) = db.getDixitDao().updateRutina(rutina)
    fun getAllRutinas() = db.getDixitDao().getAllRutinas()
    fun searchRutina(query: String?) = db.getDixitDao().searchRutina(query)
}