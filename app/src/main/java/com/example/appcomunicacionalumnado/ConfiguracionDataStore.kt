package com.example.appcomunicacionalumnado.data

import android.content.Context
import androidx.datastore.core.DataStore // Asegúrate de tener esta importación si no estaba explícita
import androidx.datastore.preferences.core.Preferences // Y esta
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "configuracion_usuario")

object PreferenciasKeys {
    val IDIOMA = stringPreferencesKey("idioma")

}

class ConfiguracionDataStore(private val context: Context) {

    val idioma: Flow<String> = context.dataStore.data.map { preferences ->
        preferences[PreferenciasKeys.IDIOMA] ?: "Castellano"
    }

    suspend fun guardarConfiguracion(idioma: String) {
        context.dataStore.edit { prefs ->
            prefs[PreferenciasKeys.IDIOMA] = idioma
        }
    }
}