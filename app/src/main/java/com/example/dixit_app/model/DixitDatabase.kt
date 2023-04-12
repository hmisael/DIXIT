package com.example.dixit_app.model


import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.dixit_app.model.entities.*


@Database(entities =
        [Categoria::class, Pictograma::class,
        Rutina::class,  RutinaPictogramaRC::class,
        Frase::class, FrasePictogramaRC::class,
        Pregunta:: class, PreguntaPictogramaRC::class,
        Respuesta::class, RespuestaPictogramaRC::class],
        version = 1)

abstract class DixitDatabase : RoomDatabase() {

    abstract fun getDixitDao(): DixitDAO

    //solo es posible acceder a la clase a través del Companion Object
    companion object {
        @Volatile
        private var instance: DixitDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?:
        synchronized(LOCK) {
            instance ?:
            createDatabase(context).also { instance = it }
        }

        private fun createDatabase(context: Context) =
            Room.databaseBuilder(
                context.applicationContext,
                DixitDatabase::class.java,
                "dixit_db"
            ).build()
    }

}