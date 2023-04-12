package com.example.dixit_app.model


import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.dixit_app.model.entities.*
import com.example.dixit_app.model.entities.relaciones.FrasesConPictogramas
import com.example.dixit_app.model.entities.relaciones.PreguntasConPictogramas
import com.example.dixit_app.model.entities.relaciones.RespuestasConPictogramas
import com.example.dixit_app.model.entities.relaciones.RutinasConPictogramas


@Dao
interface DixitDAO{

    /**
     * PICTOGRAMAS
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPictograma(pictograma: Pictograma)
    @Delete
    suspend fun deletePictograma(pictograma: Pictograma)
    @Update
    suspend fun updatePictograma(pictograma: Pictograma)

    //Obtener todos los Pictogrmas
    @Query("SELECT * FROM pictograma")
    fun getAllPictogramas(): LiveData<List<Pictograma>>

    //DEBE SER Pictograma con nombreCategoria determinado dentro de ese acceso
    @Query("SELECT * FROM pictograma WHERE nombrePictograma LIKE :query")
    fun searchPictograma(query: String?): LiveData<List<Pictograma>>

    /*
    @Query("SELECT * FROM pictograma where idCategoria = :id")
    fun getPictogramasByCategoria(id: Long): LiveData<List<Pictograma>>

    @Query("SELECT * FROM pictograma WHERE idPictograma = :id")
     fun getPictograma(id: Long): LiveData<Pictograma>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
     fun insertAllPictogramas(vararg pictogramas: Pictograma): List<Long>
    */

    /**
     * CATEGORIAS
     */

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategoria(categoria: Categoria)

    @Update
    suspend fun updateCategoria(categoria: Categoria)

    @Delete
    suspend fun deleteCategoria(categoria: Categoria)

    //Obtener Pictogramas de una determinada Categoría
    @Query("SELECT * FROM pictograma WHERE nombreCategoria = :nombreCategoria")
    fun getPictogramasByCategoria(nombreCategoria: String?): LiveData<List<Pictograma>>

    @Query("SELECT * FROM categoria")
    fun getAllCategorias(): LiveData<List<Categoria>>

    @Query("SELECT * FROM categoria WHERE nombreCategoria LIKE :query")
    fun searchCategoria(query: String?): LiveData<List<Categoria>>

    /*
   @Query("SELECT * FROM categoria ORDER BY idcategoria DESC LIMIT 1")
   fun getCategoria(): Categoria?


    @Query("DELETE FROM categoria")
    fun deleteAllCategorias()

   @Query("SELECT * FROM categoria WHERE idcategoria = :id")
   fun getCategoriaById(id: Long): LiveData<Categoria>

*/

    /**
     * RUTINAS
     */

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRutina(rutina: Rutina) : Long

    @Update
    suspend fun updateRutina(rutina: Rutina)

    @Delete
    suspend fun deleteRutina(rutina: Rutina)

    @Query("SELECT * FROM rutina")
    fun getAllRutinas(): LiveData<List<Rutina>>

    @Query("SELECT * FROM rutina WHERE nombreRutina LIKE :query")
    fun searchRutina(query: String?): LiveData<List<Rutina>>

    //Obtener el id según el nombre de la Rutina
    @Query("SELECT idRutina FROM rutina WHERE nombreRutina LIKE :query")
    fun getIdRutinaByNombre(query: String?): Long

    @Transaction
    @Query("SELECT * FROM rutina WHERE nombreRutina = :nombreRutina")
    fun getRutinaPictogramas(nombreRutina: String?): LiveData<List<RutinasConPictogramas>>

    @Transaction
    @Query("SELECT * FROM rutina WHERE nombreRutina = :nombreRutina")
    fun getPictogramasByRutina(nombreRutina: String?): LiveData<List<RutinasConPictogramas>>

    //Agregar Entidad RutinaPictogramaRC (ref. cruz. = par de rutina.id y pictograma)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPictogramasRutina(rutinasPictogramas: RutinaPictogramaRC)

    /**
     * FRASES
     */

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    //suspend fun insertFrase(frases: Frase)
    suspend fun insertFrase(frase: Frase) : Long

    @Update
    suspend fun updateFrase(frase: Frase)

    @Delete
    suspend fun deleteFrase(frase: Frase)

    @Query("SELECT * FROM frase")
    fun getAllFrases(): LiveData<List<Frase>>

    //Obtener el id según el nombre de la frases
    @Query("SELECT idFrase FROM frase WHERE nombreFrase = :query")
    fun getIdFraseByNombre(query: String?): LiveData<Long>

    @Query("SELECT * FROM frase WHERE nombreFrase LIKE :query")
    fun searchFrase(query: String?): LiveData<List<Frase>>

    //Agregar Entidad FrasePictogramaRC (ref. cruz. = par de frases.id y pictograma)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPictogramasFrase(frasesPictogramas: FrasePictogramaRC)

    //Obtener pictogramas de Frase (es idéntico al método getPictogramasByFrase
    //solo que se usan en contextos distintos (repositorio, viewmodel, etc)
    @Transaction
    @Query("SELECT * FROM frase WHERE nombreFrase = :nombreFrase")
    fun getFrasePictogramas(nombreFrase: String?): LiveData<List<FrasesConPictogramas>>

    //Bien, acá uso RC. Ver que Frase y Pictograma es una relación N-M
    //La RC es reversible. Me interesa obtener una lista de Pictogramas...
    // ...que pertenecen a una Frase en concreto (por nombreFrase)
    //Conclusión: Esta Query devuelve una lista de Pictogramas
    @Transaction
    @Query("SELECT * FROM frase WHERE nombreFrase = :nombreFrase")
    fun getPictogramasByFrase(nombreFrase: String?): LiveData<List<FrasesConPictogramas>>



    /**
     * PREGUNTAS
     */

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertPregunta(pregunta: Pregunta) : Long

    @Update
    suspend fun updatePregunta(pregunta: Pregunta)

    @Delete
    suspend fun deletePregunta(pregunta: Pregunta)

    @Query("SELECT * FROM pregunta WHERE nombrePregunta LIKE :query")
    fun searchPregunta(query: String?): LiveData<List<Pregunta>>

    @Query("SELECT * FROM pregunta")
    fun getAllPreguntas(): LiveData<List<Pregunta>>

    //Obtener el id según el nombre de la pregunta
    @Query("SELECT idPregunta FROM pregunta WHERE nombrePregunta = :query")
    fun getIdPreguntaByNombre(query: String?): LiveData<Long>

    @Transaction
    @Query("SELECT * FROM pregunta WHERE nombrePregunta = :nombrePregunta")
    fun getPictogramasByPregunta(nombrePregunta: String?): LiveData<List<PreguntasConPictogramas>>

    //Agregar Entidad PreguntaPictogramaRC (ref. cruz. = par de frases.id y pictograma)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPictogramasPregunta(preguntasPictogramas: PreguntaPictogramaRC)

    /**
     * RESPUESTAS
     */

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertRespuesta(respuesta: Respuesta) : Long

    @Update
    suspend fun updateRespuesta(respuesta: Respuesta)

    @Delete
    suspend fun deleteRespuesta(respuesta: Respuesta)

    @Query("SELECT * FROM respuesta WHERE nombrePregunta LIKE :query")
    fun searchRespuesta(query: String?): LiveData<List<Respuesta>>

    @Transaction
    @Query("SELECT * FROM respuesta WHERE nombrePregunta = :nombrePregunta")
    fun getPictogramasByRespuesta(nombrePregunta: String?): LiveData<List<RespuestasConPictogramas>>



    //Agregar Entidad PreguntaPictogramaRC (ref. cruz. = par de frases.id y pictograma)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPictogramasRespuesta(respuestasPictogramas: RespuestaPictogramaRC)

}