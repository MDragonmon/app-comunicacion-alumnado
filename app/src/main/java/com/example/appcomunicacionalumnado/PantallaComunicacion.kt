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
        TextToSpeech(context) { status ->
        }
    }

    LaunchedEffect(idiomaSeleccionado) {
        val locale = when (idiomaSeleccionado) {
            "Castellano" -> Locale("es", "ES")
            "Galego" -> Locale("pt", "PT")  // si usas portugués para gallego
            "English" -> Locale("en", "US")
            else -> Locale.getDefault()
        }
        val result = tts.setLanguage(locale)
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            println("Idioma $idiomaSeleccionado no soportado o faltan datos")
        } else {
            println("Idioma $idiomaSeleccionado configurado correctamente")
        }
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
                title = { Text(stringResource(R.string.comunicacion)) },
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
                .padding(16.dp)
        ) {
            // Fila de categorías
            LazyRow(modifier = Modifier.fillMaxWidth()) {
                lazyItems(categorias) { categoria ->
                    Button(
                        onClick = { categoriaSeleccionada = categoria },
                        modifier = Modifier
                            .padding(end = 8.dp)
                    ) {
                        Text(categoria.nombre)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                lazyItems(fraseConstruida) { pictograma ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Image(
                            painter = painterResource(pictograma.imagenResId),
                            contentDescription = pictograma.nombre,
                            modifier = Modifier.size(64.dp)
                        )
                        Text(pictograma.nombre)
                    }
                }
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(
                    onClick = { fraseConstruida.clear() },
                    modifier = Modifier.weight(1f).padding(end = 8.dp)
                ) {
                    Text(stringResource(R.string.limpiar))
                }

                Button(
                    onClick = {
                        val texto = fraseConstruida.joinToString(" ") { it.nombre }
                        tts.speak(texto, TextToSpeech.QUEUE_FLUSH, null, null)
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text(stringResource(R.string.hablar))
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Grid de pictogramas
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 100.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(categoriaSeleccionada.pictogramas) { pictograma ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()
                    ) {
                        Button(
                            onClick = { fraseConstruida.add(pictograma) },
                            modifier = Modifier.size(80.dp)
                        ) {
                            Image(
                                painter = painterResource(pictograma.imagenResId),
                                contentDescription = pictograma.nombre,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        Text(pictograma.nombre)
                    }
                }
            }
        }
    }
}
