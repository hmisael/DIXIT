package com.example.dixit_app.model.entidades.relaciones

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.dixit_app.model.entidades.Pictograma
import com.example.dixit_app.model.entidades.Rutina
import com.example.dixit_app.model.entidades.RutinaPictogramaRC

//PODRIA hacer PictogramasconRutinas, pero no necesito un listado de rutinas donde está un pictograma
data class RutinasConPictogramas(
    @Embedded val rutina: Rutina,
    @Relation(
        parentColumn = "idRutina",
        entityColumn = "idPictograma",
        associateBy = Junction(RutinaPictogramaRC::class)
    )
        val pictogramas: List<Pictograma>
)