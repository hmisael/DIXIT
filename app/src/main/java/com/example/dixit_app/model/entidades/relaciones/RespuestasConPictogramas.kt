package com.example.dixit_app.model.entidades.relaciones

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.dixit_app.model.entidades.*

//1 Respuesta = varios pictogramas -> c/pictograma es una respuesta posible
data class RespuestasConPictogramas (
    @Embedded val respuesta: Respuesta,
    @Relation(
        parentColumn = "idRespuesta",
        entityColumn = "idPictograma",
        associateBy = Junction(RespuestaPictogramaRC::class)
    )
    val pictogramas: List<Pictograma>
)