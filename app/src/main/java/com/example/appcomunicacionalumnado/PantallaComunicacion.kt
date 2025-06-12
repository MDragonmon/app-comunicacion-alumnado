package com.example.appcomunicacionalumnado

import android.speech.tts.TextToSpeech
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items as lazyItems
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.appcomunicacionalumnado.Pictograma
import com.example.appcomunicacionalumnado.data.ConfiguracionDataStore
import java.util.*

data class Categoria(val nombre: String, val pictogramas: List<Pictograma>)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaComunicacion(navController: NavHostController) {
    val context = LocalContext.current
    val configuracionDataStore = remember { ConfiguracionDataStore(context) }

    val idiomaSeleccionado by configuracionDataStore.idioma.collectAsState(initial = "Castellano")

    val categorias = listOf(
        Categoria(
            stringResource(R.string.acciones), listOf(
                Pictograma(1, stringResource(R.string.comer), R.drawable.comer),
                Pictograma(2, stringResource(R.string.beber), R.drawable.beber),
                Pictograma(3, stringResource(R.string.ir), R.drawable.ir),
                Pictograma(4, stringResource(R.string.escuchar), R.drawable.escuchar),
                Pictograma(5, stringResource(R.string.gustar), R.drawable.gustar),
                Pictograma(6, stringResource(R.string.no_gustar), R.drawable.no_gustar),
                Pictograma(7, stringResource(R.string.hablar), R.drawable.hablar),
                Pictograma(8, stringResource(R.string.dormir), R.drawable.dormir),
                Pictograma(9, stringResource(R.string.jugar), R.drawable.que_juego_quieres),
                Pictograma(10, stringResource(R.string.leer), R.drawable.leer),
                Pictograma(11, stringResource(R.string.silencio), R.drawable.silencio),
                Pictograma(20, stringResource(R.string.no), R.drawable.no),
                Pictograma(21, stringResource(R.string.sí), R.drawable.si),
            )
        ),
        Categoria(
            stringResource(R.string.sujetos), listOf(
                Pictograma(12, stringResource(R.string.yo), R.drawable.yo),
                Pictograma(13, stringResource(R.string.tu), R.drawable.tu),
                Pictograma(14, stringResource(R.string.nosotros), R.drawable.nosotros),
                Pictograma(15, stringResource(R.string.profesora), R.drawable.profesora)
            )
        ),
        Categoria(
            stringResource(R.string.lugares), listOf(
                Pictograma(16, stringResource(R.string.casa), R.drawable.casa),
                Pictograma(17, stringResource(R.string.colegio), R.drawable.colegio),
                Pictograma(18, stringResource(R.string.baño), R.drawable.bano),
                Pictograma(19, stringResource(R.string.gimnasio), R.drawable.gimnasio),
            )
        )
    )

    var categoriaSeleccionada by remember { mutableStateOf(categorias.first()) }
    val fraseConstruida = remember { mutableStateListOf<Pictograma>() }

    val tts = remember {
        TextToSpeech(context) { /* Init callback vacío */ }
    }

    LaunchedEffect(idiomaSeleccionado) {
        val locale = when (idiomaSeleccionado) {
            "Castellano" -> Locale("es", "ES")
            "Galego" -> Locale("pt", "PT")  // Puedes ajustar el locale a gallego correcto si existe
            "English" -> Locale("en", "US")
            else -> Locale.getDefault()
        }
        tts.setLanguage(locale)
    }

    DisposableEffect(Unit) {
        onDispose {
            tts.stop()
            tts.shutdown()
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.comunicacion), style = MaterialTheme.typography.headlineSmall) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp)
        ) {
            // Fila categorías
            LazyRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                lazyItems(categorias) { categoria ->
                    val isSelected = categoria == categoriaSeleccionada
                    Button(
                        onClick = { categoriaSeleccionada = categoria },
                        shape = MaterialTheme.shapes.medium,
                        colors = if (isSelected) {
                            ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
                        } else {
                            ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        },
                        modifier = Modifier.height(40.dp)
                    ) {
                        Text(
                            categoria.nombre,
                            style = MaterialTheme.typography.labelLarge,
                            color = if (isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Frase construida
            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(96.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                lazyItems(fraseConstruida) { pictograma ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(end = 4.dp)
                    ) {
                        Surface(
                            tonalElevation = 4.dp,
                            shape = MaterialTheme.shapes.small,
                            modifier = Modifier.size(64.dp)
                        ) {
                            Image(
                                painter = painterResource(pictograma.imagenResId),
                                contentDescription = pictograma.nombre,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            pictograma.nombre,
                            style = MaterialTheme.typography.labelSmall
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Botones limpiar y hablar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = { fraseConstruida.clear() },
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(stringResource(R.string.limpiar), style = MaterialTheme.typography.titleMedium)
                }
                Button(
                    onClick = {
                        val texto = fraseConstruida.joinToString(" ") { it.nombre }
                        tts.speak(texto, TextToSpeech.QUEUE_FLUSH, null, null)
                    },
                    modifier = Modifier.weight(1f),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(stringResource(R.string.hablar), style = MaterialTheme.typography.titleMedium)
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Grid pictogramas
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 100.dp),
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 16.dp)
            ) {
                items(categoriaSeleccionada.pictogramas) { pictograma ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Surface(
                            shape = MaterialTheme.shapes.medium,
                            tonalElevation = 6.dp,
                            modifier = Modifier.size(80.dp)
                        ) {
                            Button(
                                onClick = { fraseConstruida.add(pictograma) },
                                modifier = Modifier.fillMaxSize(),
                                shape = MaterialTheme.shapes.medium,
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surface)
                            ) {
                                Image(
                                    painter = painterResource(pictograma.imagenResId),
                                    contentDescription = pictograma.nombre,
                                    modifier = Modifier.fillMaxSize()
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            pictograma.nombre,
                            style = MaterialTheme.typography.labelMedium,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

