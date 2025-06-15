package com.example.appcomunicacionalumnado

import android.content.Context
import androidx.lifecycle.ViewModelProvider.NewInstanceFactory.Companion.instance
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.appcomunicacionalumnado.data.Usuario
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Usuario::class, Pictograma::class], version = 2)
abstract class AppDatabase : RoomDatabase() {
    abstract fun usuarioDao(): UsuarioDao
    abstract fun pictogramaDao(): PictogramaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                    .fallbackToDestructiveMigration(true)
                    .build()
                INSTANCE = instance

                // Insertar usuario admin una vez que la base está lista
                CoroutineScope(Dispatchers.IO).launch {
                    val usuarioDao = instance.usuarioDao()
                    val adminExistente = usuarioDao.obtenerPorNombre("admin")
                    if (adminExistente == null) {
                        usuarioDao.insertar(
                            Usuario(
                                nombre = "Admin",
                                usuario = "admin",
                                contrasena = "admin123",
                                tipo = "profesor"
                            )
                        )
                    }
                }

                instance
            }
        }

    }
}
