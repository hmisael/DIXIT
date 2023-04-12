package com.example.dixit_app.model.entities

import androidx.room.Entity

@Entity(primaryKeys = ["idPreguntaPictogramaRC", "idPregunta", "idPictograma"])
data class PreguntaPictogramaRC (
    var idPreguntaPictogramaRC: Long = 0L,
    var idPregunta: Long,
    var idPictograma: Long
)