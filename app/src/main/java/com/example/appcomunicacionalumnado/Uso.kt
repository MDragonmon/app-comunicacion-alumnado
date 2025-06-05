package com.example.appcomunicacionalumnado

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Uso(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val usuarioId: Int,
    val pictogramaId: Int,
    val fechaUso: Long = System.currentTimeMillis()
)