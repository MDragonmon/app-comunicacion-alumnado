package com.example.appcomunicacionalumnado.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "usuarios")
data class Usuario(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val usuario: String,
    val contrasena: String,
    val tipo: String, // "profesor", "alumno", "familia"
    val fechaCreacion: Long = System.currentTimeMillis()
)
