package com.example.appcomunicacionalumnado

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp

@Composable
fun IdiomaSelector(selected: String, onIdiomaSeleccionado: (String) -> Unit) {
    val idiomas = listOf("Castellano", "Galego", "English")

    Column {
        Text(stringResource(R.string.seleccionar_idioma), style = MaterialTheme.typography.labelLarge, modifier = Modifier.padding(bottom = 8.dp))
        idiomas.forEach { idioma ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = idioma == selected,
                    onClick = { onIdiomaSeleccionado(idioma) }
                )
                Text(idioma, modifier = Modifier.padding(start = 8.dp), style = MaterialTheme.typography.bodyLarge)
            }
        }
    }
}