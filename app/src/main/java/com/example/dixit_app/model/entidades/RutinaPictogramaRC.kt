package com.example.dixit_app.model.entidades

import androidx.room.Entity

@Entity(primaryKeys = ["idRutina", "idPictograma"])
data class RutinaPictogramaRC(
    var idRutina: Long,
    var idPictograma: Long
)