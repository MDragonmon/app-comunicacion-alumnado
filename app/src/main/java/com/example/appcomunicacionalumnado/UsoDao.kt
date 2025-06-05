package com.example.appcomunicacionalumnado

import androidx.room.*

@Dao
interface UsoDao {
    @Insert
    suspend fun registrarUso(uso: Uso)

    @Query("SELECT * FROM Uso WHERE usuarioId = :usuarioId ORDER BY fechaUso DESC")
    fun obtenerPorUsuario(usuarioId: Int): List<Uso>
}