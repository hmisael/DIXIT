package com.example.dixit_app.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.example.dixit_app.model.entidades.Pictograma
import com.example.dixit_app.model.entidades.Rutina
import com.example.dixit_app.model.entidades.RutinaPictogramaRC


//data
class PictogramasConRutinas{}/*(
        @Embedded val pictograma: Pictograma,
        @Relation(
                parentColumn = "idPictograma",
                entityColumn = "idRutina",
                associateBy = Junction(RutinaPictogramaRC::class)
        )
        val rutinas: List<Rutina>

)
*/