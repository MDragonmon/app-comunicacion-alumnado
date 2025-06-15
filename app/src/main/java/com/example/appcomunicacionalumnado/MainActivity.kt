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
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
    val sesionUsuario by configuracionDataStore.sesionUsuario.collectAsState(initial = null)

    var idiomaSeleccionado by remember { mutableStateOf(idiomaGuardado) }
    var mostrarDialogoCambioContrasena by remember { mutableStateOf(false) }
    var nuevaContrasena by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    // Instancia DB y DAO
    val db = remember { AppDatabase.getInstance(context) }
    val usuarioDao = remember { db.usuarioDao() }

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
                stringResource(R.string.idioma_actual) + " " + idiomaGuardado,
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
                onClick = { mostrarDialogoCambioContrasena = true },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(stringResource(R.string.cambiar_contraseña))
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    scope.launch {
                        configuracionDataStore.guardarConfiguracion(idiomaSeleccionado)
                        Toast.makeText(context, R.string.idioma_actualizado, Toast.LENGTH_SHORT).show()
                    }
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

        // Diálogo para cambiar contraseña
        if (mostrarDialogoCambioContrasena) {
            var mostrarContrasena by remember { mutableStateOf(false) }
            var mostrarConfirmacionContrasena by remember { mutableStateOf(false) }
            var confirmacionContrasena by remember { mutableStateOf("") }
            var errorContrasena by remember { mutableStateOf(false) }
            val mensajeToast = stringResource(R.string.contraseña_cambiada)
            val nueva_contraseña = stringResource(R.string.nueva_contraseña)
            val confirmar_contrasena = stringResource(R.string.confirmar_contrasena)
            val contraseña_no_coincide = stringResource(R.string.contraseña_no_coincide)
            val cambiar_contraseña = stringResource(R.string.cambiar_contraseña)
            val guardar_configuracion = stringResource(R.string.guardar_configuracion)
            val cancelar = stringResource(R.string.cancelar)

            AlertDialog(
                onDismissRequest = {
                    mostrarDialogoCambioContrasena = false
                    nuevaContrasena = ""
                    confirmacionContrasena = ""
                    errorContrasena = false
                },
                title = { Text(cambiar_contraseña) },
                text = {
                    Column {
                        OutlinedTextField(
                            value = nuevaContrasena,
                            onValueChange = {
                                nuevaContrasena = it
                                errorContrasena = false
                            },
                            label = {Text(nueva_contraseña)},
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = if (mostrarContrasena) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { mostrarContrasena = !mostrarContrasena }) {
                                    Icon(
                                        imageVector = if (mostrarContrasena) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = if (mostrarContrasena) "Ocultar contraseña" else "Mostrar contraseña"
                                    )
                                }
                            },
                            isError = errorContrasena
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedTextField(
                            value = confirmacionContrasena,
                            onValueChange = {
                                confirmacionContrasena = it
                                errorContrasena = false
                            },
                            label = { Text(confirmar_contrasena) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            visualTransformation = if (mostrarConfirmacionContrasena) VisualTransformation.None else PasswordVisualTransformation(),
                            trailingIcon = {
                                IconButton(onClick = { mostrarConfirmacionContrasena = !mostrarConfirmacionContrasena }) {
                                    Icon(
                                        imageVector = if (mostrarConfirmacionContrasena) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                        contentDescription = if (mostrarConfirmacionContrasena) "Ocultar contraseña" else "Mostrar contraseña"
                                    )
                                }
                            },
                            isError = errorContrasena
                        )

                        if (errorContrasena) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = contraseña_no_coincide,
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = {


                        if (nuevaContrasena == confirmacionContrasena && nuevaContrasena.isNotBlank()) {
                            scope.launch {
                                configuracionDataStore.guardarContrasena(nuevaContrasena)
                                Toast.makeText(context, mensajeToast, Toast.LENGTH_SHORT).show()
                                mostrarDialogoCambioContrasena = false
                                nuevaContrasena = ""
                                confirmacionContrasena = ""
                                errorContrasena = false
                            }
                        } else {
                            errorContrasena = true
                        }
                    }) {
                        Text(guardar_configuracion)
                    }
                },
                dismissButton = {
                    TextButton(onClick = {
                        mostrarDialogoCambioContrasena = false
                        nuevaContrasena = ""
                        confirmacionContrasena = ""
                        errorContrasena = false
                    }) {
                        Text(cancelar)
                    }
                }
            )
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

