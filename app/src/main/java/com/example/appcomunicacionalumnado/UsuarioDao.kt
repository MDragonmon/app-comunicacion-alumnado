package com.example.appcomunicacionalumnado

import androidx.room.*
import com.example.appcomunicacionalumnado.data.Usuario

@Dao
interface UsuarioDao {
    @Insert
    suspend fun insertar(usuario: Usuario)

    @Query("SELECT * FROM usuarios WHERE usuario = :usuario AND contrasena = :contrasena")
    suspend fun login(usuario: String, contrasena: String): Usuario?

    @Query("SELECT * FROM usuarios WHERE tipo = 'profesor' LIMIT 1")
    suspend fun obtenerPrimerProfesor(): Usuario?
}
