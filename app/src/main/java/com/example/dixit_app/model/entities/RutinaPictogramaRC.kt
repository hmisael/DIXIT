package com.example.dixit_app.model.entities

import androidx.room.Entity

@Entity(primaryKeys = ["idRutinaPictogramaRC", "idRutina", "idPictograma"])
data class RutinaPictogramaRC(
    var idRutinaPictogramaRC: Long = 0L,
    var idRutina: Long,
    var idPictograma: Long
)