package com.example.appcomunicacionalumnado

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.appcomunicacionalumnado.data.ConfiguracionDataStore
import com.example.appcomunicacionalumnado.data.Usuario
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaRegistroUsuario(navController: NavHostController, db: AppDatabase) {
    val context = LocalContext.current
    val configuracionDataStore = remember { ConfiguracionDataStore(context) }
    val idiomaActual by configuracionDataStore.idioma.collectAsState(initial = "Castellano")

    var nombre by remember { mutableStateOf("") }
    var usuario by remember { mutableStateOf("") }
    var contrasena by remember { mutableStateOf("") }

    val tipoUsuarioAlumnoLocalizado = stringResource(R.string.tipo_alumno)
    var tipoUsuarioSeleccionado by remember { mutableStateOf(tipoUsuarioAlumnoLocalizado) }
    var expanded by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(tipoUsuarioAlumnoLocalizado) {
        tipoUsuarioSeleccionado = tipoUsuarioAlumnoLocalizado
    }

    val tiposDeUsuarioLocalizados = listOf(tipoUsuarioAlumnoLocalizado)

    val scope = rememberCoroutineScope()

    key(idiomaActual) { // Clave para forzar recomposición cuando el idioma cambie
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text(stringResource(R.string.registrar_usuario_titulo)) },
                    navigationIcon = {
                        IconButton(onClick = { navController.popBackStack() }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.volver_menu_boton)
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
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                TextField(
                    value = nombre,
                    onValueChange = { nombre = it },
                    label = { Text(stringResource(R.string.nombre_label)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = usuario,
                    onValueChange = { usuario = it },
                    label = { Text(stringResource(R.string.usuario_label)) },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = contrasena,
                    onValueChange = { contrasena = it },
                    label = { Text(stringResource(R.string.contrasena_label)) },
                    modifier = Modifier.fillMaxWidth(),
                    visualTransformation = PasswordVisualTransformation()
                )
                Spacer(modifier = Modifier.height(8.dp))
                Box {
                    Button(
                        onClick = { expanded = true },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(stringResource(R.string.tipo_label, tipoUsuarioSeleccionado))
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        tiposDeUsuarioLocalizados.forEach { tipo ->
                            DropdownMenuItem(
                                text = { Text(tipo) },
                                onClick = {
                                    tipoUsuarioSeleccionado = tipo
                                    expanded = false
                                }
                            )
                        }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                errorMessage?.let {
                    Text(text = it, color = MaterialTheme.colorScheme.error)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Button(
                    onClick = {
                        if (nombre.isBlank() || usuario.isBlank() || contrasena.isBlank()) {
                            errorMessage = context.getString(R.string.completar_campos_error)
                            return@Button
                        }
                        errorMessage = null // Limpiar error si los campos están llenos
                        scope.launch {
                            try {
                                db.usuarioDao().insertar(
                                    Usuario(
                                        nombre = nombre,
                                        usuario = usuario,
                                        contrasena = contrasena,
                                        // Guardamos el tipo en un formato no localizado (ej. "alumno")
                                        // si es necesario, o el valor localizado si así lo prefieres.
                                        // Para consistencia, es mejor guardar un valor clave no localizado.
                                        // Aquí asumimos que "alumno" es la clave interna.
                                        tipo = "alumno" // tipoUsuarioSeleccionado.lowercase() podría depender del idioma
                                    )
                                )
                                navController.navigate("menu/Profesor") {
                                    popUpTo("registroUsuario") { inclusive = true }
                                }
                            } catch (e: Exception) {
                                errorMessage = context.getString(R.string.error_registrar_usuario, e.localizedMessage ?: "Error desconocido")
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                ) {
                    Text(stringResource(R.string.registrar_boton))
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        navController.popBackStack()
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.volver_menu_boton))
                }
            }
        }
    }
}