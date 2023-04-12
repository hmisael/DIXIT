package com.example.dixit_app.model.entities.relaciones

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.dixit_app.model.entities.Frase
import com.example.dixit_app.model.entities.FrasePictogramaRC
import com.example.dixit_app.model.entities.Pictograma

data class FrasesConPictogramas(
    @Embedded val frase: Frase,
    @Relation(
        parentColumn = "idFrase",
        entityColumn = "idPictograma",
        associateBy = Junction(FrasePictogramaRC::class)
    )
    val pictogramas: List<Pictograma>
)