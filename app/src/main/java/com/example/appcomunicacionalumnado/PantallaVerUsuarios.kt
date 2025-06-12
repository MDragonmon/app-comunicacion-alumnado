package com.example.appcomunicacionalumnado

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.appcomunicacionalumnado.data.Usuario
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaVerUsuarios(navController: NavHostController, db: AppDatabase) {
    var usuarios by remember { mutableStateOf<List<Usuario>>(emptyList()) }
    val scope = rememberCoroutineScope()

    var usuarioEnEdicion by remember { mutableStateOf<Usuario?>(null) }
    var nombreEditado by remember { mutableStateOf("") }
    var tipoEditado by remember { mutableStateOf("") }
    var nuevaContrasena by remember { mutableStateOf("") }
    var usuarioEditado by remember { mutableStateOf("") }

    // Strings desde recursos
    val tituloListaUsuarios = stringResource(R.string.pantalla_usuarios) // "Gestionar usuarios"
    val textoNoHayUsuarios = stringResource(R.string.no_hay_usuarios) // "No hay usuarios registrados."
    val descripcionVolver = stringResource(R.string.cerrar_sesion) // Para usar como contentDescription del botón volver (si quieres puedes crear otro string específico)
    val textoModificar = stringResource(R.string.modificar_usuario) // "Modificar usuario"
    val textoEliminar = stringResource(R.string.eliminar_usuario) // "Eliminar usuario"
    val textoGuardar = stringResource(R.string.guardar_configuracion) // "Guardar configuración" (puedes crear uno específico)
    val textoCancelar = stringResource(R.string.cancelar) // "Cancelar"
    val labelNombreCompleto = stringResource(R.string.nombre_completo) // "Nombre completo"
    val labelUsuario = stringResource(R.string.usuario_label) // "Usuario"
    val labelTipo = stringResource(R.string.tipo_label, "") // "Tipo: " (aquí sin parámetro)
    val labelNuevaContrasena = stringResource(R.string.contrasena_label) // "Contraseña"

    fun cargarUsuarios() {
        scope.launch {
            usuarios = db.usuarioDao().getAll()
        }
    }

    LaunchedEffect(Unit) {
        cargarUsuarios()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(tituloListaUsuarios) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = androidx.compose.material.icons.Icons.Filled.ArrowBack,
                            contentDescription = descripcionVolver
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (usuarios.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(textoNoHayUsuarios)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(usuarios) { usuario ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("${stringResource(R.string.nombre_label)}: ${usuario.nombre}", style = MaterialTheme.typography.bodyLarge)
                            Text("${stringResource(R.string.tipo_label, usuario.tipo)}", style = MaterialTheme.typography.bodyMedium)

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(
                                horizontalArrangement = Arrangement.End,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                IconButton(
                                    onClick = {
                                        usuarioEnEdicion = usuario
                                        usuarioEditado = usuario.usuario
                                        nombreEditado = usuario.nombre
                                        tipoEditado = usuario.tipo
                                        nuevaContrasena = ""
                                    }
                                ) {
                                    Icon(
                                        imageVector = androidx.compose.material.icons.Icons.Filled.Edit,
                                        contentDescription = textoModificar
                                    )
                                }

                                IconButton(
                                    onClick = {
                                        scope.launch {
                                            db.usuarioDao().eliminar(usuario)
                                            cargarUsuarios()
                                        }
                                    }
                                ) {
                                    Icon(
                                        imageVector = androidx.compose.material.icons.Icons.Filled.Delete,
                                        contentDescription = textoEliminar,
                                        tint = Color.Red
                                    )
                                }
                            }
                        }
                    }
                }
            }
            if (usuarioEnEdicion != null) {
                AlertDialog(
                    onDismissRequest = { usuarioEnEdicion = null },
                    title = { Text(textoModificar) },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                scope.launch {
                                    val actualizado = usuarioEnEdicion!!.copy(
                                        nombre = nombreEditado,
                                        usuario = usuarioEditado,
                                        tipo = tipoEditado,
                                        contrasena = if (nuevaContrasena.isNotBlank()) nuevaContrasena else usuarioEnEdicion!!.contrasena
                                    )
                                    db.usuarioDao().actualizar(actualizado)
                                    cargarUsuarios()
                                    usuarioEnEdicion = null
                                }
                            }
                        ) {
                            Text(textoGuardar)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { usuarioEnEdicion = null }) {
                            Text(textoCancelar)
                        }
                    },
                    text = {
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            OutlinedTextField(
                                value = nombreEditado,
                                onValueChange = { nombreEditado = it },
                                label = { Text(labelNombreCompleto) },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = usuarioEditado,
                                onValueChange = { usuarioEditado = it },
                                label = { Text(labelUsuario) },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = tipoEditado,
                                onValueChange = { tipoEditado = it },
                                label = { Text(labelTipo) },
                                modifier = Modifier.fillMaxWidth()
                            )
                            OutlinedTextField(
                                value = nuevaContrasena,
                                onValueChange = { nuevaContrasena = it },
                                label = { Text(labelNuevaContrasena) },
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                )
            }
        }
    }
}
