package com.example.dixit_app.model.entidades

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize
import java.io.Serializable


@Entity
@Parcelize
data class Pictograma(
        @PrimaryKey(autoGenerate = true)
        var idPictograma: Long = 0L,
        var nombrePictograma:String,
        var imagen: String,
        var nombreCategoria: String
    ) : Parcelable

