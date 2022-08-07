package com.example.dixit_app.model.entidades

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Entity (primaryKeys = ["idFrasePictogramaRC","idFrase", "idPictograma"])
data class FrasePictogramaRC(
    var idFrasePictogramaRC: Long = 0L,
    var idFrase: Long,
    var idPictograma: Long
)
