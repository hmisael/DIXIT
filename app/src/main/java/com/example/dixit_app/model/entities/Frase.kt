package com.example.dixit_app.model.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class Frase(
    @PrimaryKey(autoGenerate = true)
    var idFrase: Long = 0L,
    var nombreFrase: String
): Parcelable