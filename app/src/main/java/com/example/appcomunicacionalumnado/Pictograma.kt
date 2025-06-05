package com.example.appcomunicacionalumnado

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Pictograma(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val nombre: String,
    val imagenResId: Int,
)
