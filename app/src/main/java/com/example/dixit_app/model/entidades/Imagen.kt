package com.example.dixit_app.model.entidades

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Imagen (
     @PrimaryKey(autoGenerate = true)
     var idImagen: Long = 0,
     var nombre: String,
     var uri: String
)