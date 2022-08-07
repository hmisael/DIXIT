package com.example.dixit_app.model.entidades.relaciones

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.dixit_app.model.entidades.Frase
import com.example.dixit_app.model.entidades.FrasePictogramaRC
import com.example.dixit_app.model.entidades.Pictograma

data class FrasesConPictogramas(
    @Embedded val frase: Frase,
    @Relation(
        parentColumn = "idFrase",
        entityColumn = "idPictograma",
        associateBy = Junction(FrasePictogramaRC::class)
    )
    val pictogramas: List<Pictograma>
)