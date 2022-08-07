package com.example.dixit_app.helper

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.util.Base64.*
import androidx.annotation.RequiresApi
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream
import java.util.*

class Converters {
/*
    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun bitMapToString(bitmap : Bitmap) : String?{
        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos)
        val b = baos.toByteArray()
//        val temp = Base64.getEncoder().encodeToString(b, Base64.)

        val encoder : Base64.Encoder = Base64.getEncoder()
        val temp: String = encoder.encodeToString(b.toString())

        if (temp == null) {
            return null
        } else
            return temp
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @TypeConverter
    fun stringToBitMap(encodedString : String):Bitmap? {
        try {
            val encoder : Base64.Encoder = Base64.getEncoder()

            val encodeByte = encoder.encodeToString(encodedString)
            val bitmap = BitmapFactory.decodeByteArray (encodeByte, 0, encodeByte.length);
            if (bitmap == null) {
                return null
            } else {
                return bitmap
            }

        } catch (e: Exception) {
            e.toString()
            return null
        }
    }*/
}