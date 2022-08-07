package com.example.dixit_app.model.entidades.relaciones

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.dixit_app.model.entidades.*

//1 pregunta = varios pictogramas -> representan una frase
data class PreguntasConPictogramas (
    @Embedded val pregunta: Pregunta,
    @Relation(
        parentColumn = "idPregunta",
        entityColumn = "idPictograma",
        associateBy = Junction(PreguntaPictogramaRC::class)
    )
    val pictogramas: List<Pictograma>
)