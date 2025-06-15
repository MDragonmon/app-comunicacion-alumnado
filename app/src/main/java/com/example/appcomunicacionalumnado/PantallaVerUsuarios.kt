package com.example.appcomunicacionalumnado

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
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
    var confirmarContrasena by remember { mutableStateOf("") }
    var usuarioEditado by remember { mutableStateOf("") }

    var nuevaContrasenaVisible by remember { mutableStateOf(false) }
    var confirmarContrasenaVisible by remember { mutableStateOf(false) }

    var errorContrasena by remember { mutableStateOf<String?>(null) }

    // Strings desde recursos
    val tituloListaUsuarios = stringResource(R.string.pantalla_usuarios)
    val textoNoHayUsuarios = stringResource(R.string.no_hay_usuarios)
    val descripcionVolver = stringResource(R.string.cerrar_sesion)
    val textoModificar = stringResource(R.string.modificar_usuario)
    val textoEliminar = stringResource(R.string.eliminar_usuario)
    val textoGuardar = stringResource(R.string.guardar_configuracion)
    val textoCancelar = stringResource(R.string.cancelar)
    val labelNombreCompleto = stringResource(R.string.nombre_completo)
    val labelUsuario = stringResource(R.string.usuario_label)
    val labelTipo = stringResource(R.string.tipo_label, "")
    val labelNuevaContrasena = stringResource(R.string.contrasena_label)
    val labelConfirmarContrasena = stringResource(R.string.confirmar_contrasena)
    val validacion_contraseña = stringResource(R.string.validacion_contraseña)
    val contraseña_no_coincide = stringResource(R.string.contraseña_no_coincide)

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
                            imageVector = Icons.Filled.ArrowBack,
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
                                        confirmarContrasena = ""
                                        errorContrasena = null
                                    }
                                ) {
                                    Icon(
                                        imageVector = Icons.Filled.Edit,
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
                                        imageVector = Icons.Filled.Delete,
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
                            enabled = errorContrasena == null,
                            onClick = {
                                scope.launch {
                                    val contrasenaValida = nuevaContrasena.isBlank() || validarContrasena(nuevaContrasena)
                                    if (contrasenaValida) {
                                        val actualizado = usuarioEnEdicion!!.copy(
                                            nombre = nombreEditado,
                                            usuario = usuarioEditado,
                                            tipo = tipoEditado,
                                            contrasena = if (nuevaContrasena.isNotBlank()) nuevaContrasena else usuarioEnEdicion!!.contrasena
                                        )
                                        db.usuarioDao().actualizar(actualizado)
                                        cargarUsuarios()
                                        usuarioEnEdicion = null
                                    } else {
                                        errorContrasena = validacion_contraseña
                                    }
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
                                onValueChange = {
                                    nuevaContrasena = it
                                    // Validación al cambiar contraseña
                                    errorContrasena = when {
                                        nuevaContrasena.isNotEmpty() && !validarContrasena(nuevaContrasena) -> validacion_contraseña
                                        nuevaContrasena.isNotEmpty() && nuevaContrasena != confirmarContrasena -> contraseña_no_coincide
                                        else -> null
                                    }
                                },
                                label = { Text(labelNuevaContrasena) },
                                modifier = Modifier.fillMaxWidth(),
                                visualTransformation = if (nuevaContrasenaVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = {
                                    val icon = if (nuevaContrasenaVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                                    IconButton(onClick = { nuevaContrasenaVisible = !nuevaContrasenaVisible }) {
                                        Icon(imageVector = icon, contentDescription = if (nuevaContrasenaVisible) "Ocultar contraseña" else "Mostrar contraseña")
                                    }
                                },
                                isError = errorContrasena != null
                            )
                            OutlinedTextField(
                                value = confirmarContrasena,
                                onValueChange = {
                                    confirmarContrasena = it
                                    // Validación al cambiar confirmación
                                    errorContrasena = when {
                                        nuevaContrasena.isNotEmpty() && confirmarContrasena != nuevaContrasena -> contraseña_no_coincide
                                        else -> null
                                    }
                                },
                                label = { Text(labelConfirmarContrasena) },
                                modifier = Modifier.fillMaxWidth(),
                                visualTransformation = if (confirmarContrasenaVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                trailingIcon = {
                                    val icon = if (confirmarContrasenaVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                                    IconButton(onClick = { confirmarContrasenaVisible = !confirmarContrasenaVisible }) {
                                        Icon(imageVector = icon, contentDescription = if (confirmarContrasenaVisible) "Ocultar contraseña" else "Mostrar contraseña")
                                    }
                                },
                                isError = errorContrasena != null
                            )
                            if (errorContrasena != null) {
                                Text(
                                    text = errorContrasena!!,
                                    color = MaterialTheme.colorScheme.error,
                                    style = MaterialTheme.typography.bodySmall,
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                )
            }
        }
    }
}
