package com.example.dixit_app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.dixit_app.model.entidades.Pregunta
import com.example.dixit_app.model.entidades.PreguntaPictogramaRC
import com.example.dixit_app.model.entidades.Respuesta
import com.example.dixit_app.model.entidades.RespuestaPictogramaRC
import com.example.dixit_app.repository.PreguntaRepository
import kotlinx.coroutines.launch

class PreguntaViewModel(app: Application, private val preguntaRepository: PreguntaRepository)
    : AndroidViewModel(app)  {

    suspend fun insertPregunta(pregunta: Pregunta) = preguntaRepository.insertPregunta(pregunta)

    fun deletePregunta(pregunta: Pregunta) =
        viewModelScope.launch {
            preguntaRepository.deletePregunta(pregunta)
        }

    fun updatePregunta(pregunta: Pregunta) =
        viewModelScope.launch {
            preguntaRepository.updatePregunta(pregunta)
        }

    fun getAllPreguntas() = preguntaRepository.getAllPreguntas()

    //Obtener todos los pictogramas
    fun getAllPictogramas() = preguntaRepository.getAllPictogramas()

    //Obtener los pictogramas de una Pregunta
    fun getPictogramasByPregunta(pregunta : String) = preguntaRepository.getPictogramasByPregunta(pregunta)


    //Obtener los pictogramas de una Respuesta
    fun getPictogramasByRespuesta(pregunta : String) = preguntaRepository.getPictogramasByRespuesta(pregunta)


    fun searchPregunta(query: String?) =
        preguntaRepository.searchPregunta(query)

    fun insertPictogramasPregunta(pictospregunta : PreguntaPictogramaRC) =
        viewModelScope.launch {
            preguntaRepository.insertPictogramasPregunta(pictospregunta)
        }



    suspend fun insertRespuesta(respuesta: Respuesta) =
        preguntaRepository.insertRespuesta(respuesta)


    fun insertPictogramasRespuesta(pictosrespuesta : RespuestaPictogramaRC) =
        viewModelScope.launch {
            preguntaRepository.insertPictogramasRespuesta(pictosrespuesta)
        }
    //obtener id de una Pregunta según su nombre
    fun getIdPreguntaByNombre(nombre: String) = preguntaRepository.getIdPreguntaByNombre(nombre)


    fun searchPictograma(query: String?) =
        preguntaRepository.searchPictograma(query)

}