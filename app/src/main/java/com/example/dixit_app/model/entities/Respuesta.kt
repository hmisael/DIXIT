package com.example.dixit_app.model.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class Respuesta (
    @PrimaryKey(autoGenerate = true)
    var idRespuesta: Long = 0L,
    var nombrePregunta : String
) : Parcelable