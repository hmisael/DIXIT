package com.example.dixit_app.model.entities

import androidx.room.Entity

@Entity(primaryKeys = ["idRespuestaPictogramaRC", "idRespuesta", "idPictograma"])
data class RespuestaPictogramaRC (
    var idRespuestaPictogramaRC: Long = 0L,
    var idRespuesta: Long,
    var idPictograma: Long
)