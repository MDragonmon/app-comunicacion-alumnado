package com.example.appcomunicacionalumnado

import androidx.room.*
import com.example.appcomunicacionalumnado.data.Usuario

@Dao
interface UsuarioDao {
    @Query("SELECT * FROM usuarios WHERE usuario = :usuario LIMIT 1")
    suspend fun obtenerPorNombre(usuario: String): Usuario?

    @Insert
    suspend fun insertar(usuario: Usuario)

    @Query("SELECT * FROM usuarios WHERE usuario = :usuario AND contrasena = :contrasena")
    suspend fun login(usuario: String, contrasena: String): Usuario?

    @Query("SELECT * FROM usuarios WHERE tipo = 'profesor' LIMIT 1")
    suspend fun obtenerPrimerProfesor(): Usuario?

    @Query("SELECT * FROM usuarios")
    suspend fun getAll(): List<Usuario>

    @Delete
    suspend fun eliminar(usuario: Usuario)

    @Update
    suspend fun actualizar(usuario: Usuario)

}
