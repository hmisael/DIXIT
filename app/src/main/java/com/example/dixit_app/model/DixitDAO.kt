package com.example.dixit_app.model


import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.dixit1.model.entidades.relaciones.FrasesConPictogramas
import com.example.dixit_app.model.entidades.Categoria
import com.example.dixit_app.model.entidades.Frase
import com.example.dixit_app.model.entidades.Pictograma
import com.example.dixit_app.model.entidades.Rutina


@Dao
interface DixitDAO{
    //PICTOGRAMA
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPictograma(pictograma: Pictograma)
    @Delete
    suspend fun deletePictograma(pictograma: Pictograma)
    @Update
    suspend fun updatePictograma(pictograma: Pictograma)

    @Query("SELECT * FROM pictograma WHERE nombreCategoria = :nombreCategoria")
    fun getPictogramasByCategoria(nombreCategoria: String?): LiveData<List<Pictograma>>

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

     /*
    @Query("SELECT * FROM categoria ORDER BY idcategoria DESC LIMIT 1")
    fun getCategoria(): Categoria?


     @Query("DELETE FROM categoria")
     fun deleteAllCategorias()

   @Query("SELECT * FROM categoria WHERE idcategoria = :id")
    fun getCategoriaById(id: Long): LiveData<Categoria>

*/
    /**
     * Selecciona y devuelve todas las filas en una tabla,
     * en orden alfabetico descendente
     */
    @Query("SELECT * FROM categoria")
    fun getAllCategorias(): LiveData<List<Categoria>>

    @Query("SELECT * FROM categoria WHERE nombreCategoria LIKE :query")
    fun searchCategoria(query: String?): LiveData<List<Categoria>>





    /**
     * RUTINAS
     */

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRutina(rutina: Rutina)

    @Update
    suspend fun updateRutina(rutina: Rutina)

    @Delete
    suspend fun deleteRutina(rutina: Rutina)


    @Query("SELECT * FROM rutina")
    fun getAllRutinas(): LiveData<List<Rutina>>

    @Query("SELECT * FROM rutina WHERE nombreRutina LIKE :query")
    fun searchRutina(query: String?): LiveData<List<Rutina>>


    /**
     * FRASES
     */

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFrase(frase: Frase)

    @Update
    suspend fun updateFrase(frase: Frase)

    @Delete
    suspend fun deleteFrase(frase: Frase)


    @Query("SELECT * FROM frase")
    fun getAllFrases(): LiveData<List<Frase>>

    @Query("SELECT * FROM frase WHERE nombreFrase LIKE :query")
    fun searchFrase(query: String?): LiveData<List<Frase>>



    //Bien, acá uso RC. Ver que Frase y Pictograma es una relación N-M
    //La RC es reversible. Me interesa obtener una lista de Pictogramas...
    // ...que pertenecen a una Frase en concreto (por nombreFrase)
    @Transaction
    @Query("SELECT * FROM frase WHERE nombreFrase = :nombreFrase")
    fun getPictogramasByFrase(nombreFrase: String?): LiveData<List<FrasesConPictogramas>>



    /*
    @Transaction
    @Query("SELECT * FROM rutina WHERE idRutina = :idRutina")
    //fun getRutinasConPictogramas(): List<RutinasConPictogramas>
    suspend fun getPictogramasDeRutinas(idRutina: Int): List<RutinasConPictogramas>

    @Transaction
    @Query("SELECT * FROM pictograma")
    suspend fun getPictogramasConRutinas(): List<PictogramasConRutinas>

    //RELACION 1 a m (categoria y pictogramas)


    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun agregarPictograma(pictograma: Pictograma)

    @Transaction
    @Query("SELECT * FROM categoria WHERE idCategoria = :idCategoria")
    suspend fun getCategoriaConPictogramas(idCategoria: Int): List<CategoriaConPictogramas>


    // RELACION n a m (rutinas y pictogramas)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun agregarRutina(rutina: Rutina)

    @Transaction //me interesan los pictogramas de una rutina particular
    @Query("SELECT * FROM rutina WHERE idRutina = :idRutina")
    suspend fun getPictogramasDeRutina(idRutina: Int): List<RutinasConPictogramas>

    //RELACION n a m (frases y pictogramas)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun agregarFrase(frase: Frase)

    @Transaction //me interesan los pictogramas de una frase particular
    @Query("SELECT * FROM frase WHERE idFrase = :idFrase")
    suspend fun getPictogramasDeFrase(idFrase: Int): List<FrasesConPictogramas>


*/


}