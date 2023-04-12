package com.example.dixit_app.model.repository

import com.example.dixit_app.model.DixitDatabase
import com.example.dixit_app.model.entities.Pregunta
import com.example.dixit_app.model.entities.PreguntaPictogramaRC
import com.example.dixit_app.model.entities.Respuesta
import com.example.dixit_app.model.entities.RespuestaPictogramaRC


class PreguntaRepository (private val db: DixitDatabase) {
    //suspend fun insertPregunta(pregunta: Pregunta) = db.getDixitDao().insertPregunta(pregunta)
    suspend fun insertPregunta(pregunta: Pregunta) : Long {
        return db.getDixitDao().insertPregunta(pregunta)
    }

    suspend fun deletePregunta(pregunta: Pregunta) = db.getDixitDao().deletePregunta(pregunta)
    suspend fun updatePregunta(pregunta: Pregunta) = db.getDixitDao().updatePregunta(pregunta)
    fun getAllPreguntas() = db.getDixitDao().getAllPreguntas()
    fun searchPregunta(query: String?) = db.getDixitDao().searchPregunta(query)

    fun getIdPreguntaByNombre(nombre: String) = db.getDixitDao().getIdPreguntaByNombre(nombre)

    fun getAllPictogramas() = db.getDixitDao().getAllPictogramas()

    //Obtener los pictogramas de una pregunta
    //fun getPreguntaPictogramas(pregunta : String) =
    //    db.getDixitDao().getPreguntaPictogramas(pregunta)

    fun getPictogramasByPregunta(nombrePregunta: String) =
        db.getDixitDao().getPictogramasByPregunta(nombrePregunta)


    //Obtener los pictogramas de una respuesta (con el nombre de pregunta)
//    fun getRespuestaPictogramas(pregunta : String) = db.getDixitDao().getPictogramasByRespuesta(pregunta)

    fun getPictogramasByRespuesta(pregunta : String) =
        db.getDixitDao().getPictogramasByRespuesta(pregunta)

    suspend fun insertPictogramasPregunta(pictopregunta : PreguntaPictogramaRC) =
        db.getDixitDao().insertPictogramasPregunta(pictopregunta)

    suspend fun insertRespuesta(respuesta: Respuesta) : Long {
        return db.getDixitDao().insertRespuesta(respuesta)
    }

    suspend fun insertPictogramasRespuesta(pictorespuesta : RespuestaPictogramaRC) =
        db.getDixitDao().insertPictogramasRespuesta(pictorespuesta)

    fun searchPictograma(query: String?) = db.getDixitDao().searchPictograma(query)
}