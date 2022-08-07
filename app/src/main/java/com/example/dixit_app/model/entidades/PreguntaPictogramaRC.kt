package com.example.dixit_app.model.entidades

import androidx.room.Entity

@Entity(primaryKeys = ["idPregunta", "idPictograma"])
data class PreguntaPictogramaRC (
    var idPregunta: Long,
    var idPictograma: Long
)