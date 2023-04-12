package com.example.dixit_app.model.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class Pregunta(
    @PrimaryKey(autoGenerate = true)
    var idPregunta: Long = 0L,
    var nombrePregunta: String
) : Parcelable