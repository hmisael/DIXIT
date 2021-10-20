package com.example.dixit1.model.entidades.relaciones

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.Junction
import androidx.room.Relation
import com.example.dixit_app.model.entidades.Frase
import com.example.dixit_app.model.entidades.FrasePictogramaRC
import com.example.dixit_app.model.entidades.Pictograma
import kotlinx.parcelize.Parcelize


data class FrasesConPictogramas(
    @Embedded val frase: Frase,
    @Relation(
        parentColumn = "idFrase",
        entityColumn = "idPictograma",
        associateBy = Junction(FrasePictogramaRC::class)
    )
    val pictogramas: List<Pictograma>
)