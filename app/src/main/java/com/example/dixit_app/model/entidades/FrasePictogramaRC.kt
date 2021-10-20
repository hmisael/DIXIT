package com.example.dixit_app.model.entidades

import android.os.Parcelable
import androidx.room.Entity
import kotlinx.parcelize.Parcelize


@Entity (primaryKeys = ["idFrase", "idPictograma"])
data class FrasePictogramaRC(
    var idFrase: Int,
    var idPictograma: Int
)
