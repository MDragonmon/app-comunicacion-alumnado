package com.example.appcomunicacionalumnado

import androidx.room.*

@Dao
interface PictogramaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(pictograma: Pictograma): Long

    @Query("SELECT * FROM Pictograma")
    fun obtenerTodos(): List<Pictograma>
}