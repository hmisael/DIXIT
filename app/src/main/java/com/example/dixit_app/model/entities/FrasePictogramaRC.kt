package com.example.dixit_app.model.entities

import androidx.room.Entity

@Entity (primaryKeys = ["idFrasePictogramaRC","idFrase", "idPictograma"])
data class FrasePictogramaRC(
    var idFrasePictogramaRC: Long = 0L,
    var idFrase: Long,
    var idPictograma: Long
)
