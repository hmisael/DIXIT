package com.example.dixit_app.model.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Entity
@Parcelize
data class Categoria (
    @PrimaryKey(autoGenerate = true)
    var idCategoria: Long = 0L,
    var nombreCategoria: String
) : Parcelable