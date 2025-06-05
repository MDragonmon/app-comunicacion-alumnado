package com.example.appcomunicacionalumnado

import android.app.Activity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.appcomunicacionalumnado.data.ConfiguracionDataStore
import com.example.appcomunicacionalumnado.ui.theme.AppComunicacionAlumnadoTheme
import com.example.appcomunicacionalumnado.util.updateLocale
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    private lateinit var configuracionDataStore: ConfiguracionDataStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        configuracionDataStore = ConfiguracionDataStore(this)

        setContent {
            val idioma by configuracionDataStore.idioma.collectAsState(initial = "Castellano")
            val localizedContext = remember(idioma) { baseContext.updateLocale(idioma) }

            CompositionLocalProvider(LocalContext provides localizedContext) {
                AppComunicacionAlumnadoTheme {
                    AppComunicacionNav()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaConfiguracion(navController: NavHostController) {
    val context = LocalContext.current
    val configuracionDataStore = remember { ConfiguracionDataStore(context) }
    val idiomaGuardado by configuracionDataStore.idioma.collectAsState(initial = "Castellano")
    var idiomaSeleccionado by remember { mutableStateOf(idiomaGuardado) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(idiomaGuardado) {
        idiomaSeleccionado = idiomaGuardado
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.configuracion_usuario)) },
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
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                stringResource(R.string.configuracion_usuario),
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.align(Alignment.Start)
            )
            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                stringResource(R.string.idioma_actual) + idiomaGuardado,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Start)
            )
            IdiomaSelector(
                selected = idiomaSeleccionado,
                onIdiomaSeleccionado = { nuevoIdioma ->
                    idiomaSeleccionado = nuevoIdioma
                }
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    scope.launch {
                        configuracionDataStore.guardarConfiguracion(idiomaSeleccionado)
                        Toast.makeText(context, R.string.idioma_actualizado, Toast.LENGTH_SHORT).show() }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.guardar_configuracion))
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { navController.popBackStack() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.volver_menu_boton))
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PantallaConfiguracionPreview() {
    AppComunicacionAlumnadoTheme {
        val navController = rememberNavController()
        PantallaConfiguracion(navController = navController)
    }
}
