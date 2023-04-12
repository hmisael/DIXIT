package com.example.dixit_app.model.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class Rutina(
    @PrimaryKey(autoGenerate = true)
    var idRutina: Long = 0L,
    var nombreRutina: String
): Parcelable
