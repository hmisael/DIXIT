package com.example.dixit_app.model.entidades

import androidx.room.Entity

@Entity(primaryKeys = ["idRespuesta", "idPictograma"])
data class RespuestaPictogramaRC (
    var idRespuesta: Long,
    var idPictograma: Long
)