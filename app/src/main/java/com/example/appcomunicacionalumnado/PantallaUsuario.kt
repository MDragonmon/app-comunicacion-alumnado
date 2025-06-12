package com.example.appcomunicacionalumnado

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Close
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
import com.example.appcomunicacionalumnado.validarContrasena

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaUsuarios(navController: NavHostController, db: AppDatabase) {
    val scope = rememberCoroutineScope()
    var usuariosTemporales by remember { mutableStateOf(mutableListOf(UsuarioTemporal())) }
    val snackbarHostState = remember { SnackbarHostState() }
    var showReviewDialog by remember { mutableStateOf(false) }

    val aceptados = remember { mutableStateListOf(false) }
    val scrollState = rememberScrollState()

    fun validarUsuario(usuario: UsuarioTemporal): Boolean {
        return usuario.nombre.isNotBlank()
                && usuario.usuario.isNotBlank()
                && usuario.tipo.isNotBlank()
                && validarContrasena(usuario.contrasena)
    }

    suspend fun existenUsuariosEnBD(usuarios: List<UsuarioTemporal>): List<Boolean> {
        return usuarios.map { usuarioTemp ->
            db.usuarioDao().obtenerPorNombre(usuarioTemp.usuario) != null
        }
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.pantalla_usuarios)) },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.volver)
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { navController.navigate("registroUsuario") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(stringResource(R.string.registrar_usuario_titulo))
            }

            Button(
                onClick = { navController.navigate("verUsuarios") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
            ) {
                Text(stringResource(R.string.ver_usuarios))
            }

            Divider(modifier = Modifier.padding(vertical = 8.dp))

            Text(
                stringResource(R.string.crear_usuarios_lote),
                style = MaterialTheme.typography.titleMedium
            )

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
            ) {
                usuariosTemporales.forEachIndexed { index, usuarioTemp ->
                    EditableUsuarioCard(
                        usuarioTemp = usuarioTemp,
                        onValueChange = { nuevosValores ->
                            usuariosTemporales = usuariosTemporales.toMutableList().apply {
                                this[index] = nuevosValores
                            }
                        }
                    )
                }
            }
            val textoErrorUltimoUsuario = stringResource(R.string.error_ultimo_usuario)
            val textoNoHayUsuarios = stringResource(R.string.no_hay_usuarios)
            val textoCorrigeUsuariosInvalidos = stringResource(R.string.corrige_usuarios_invalidos)
            val textoUsuariosYaExisten = stringResource(R.string.usuarios_ya_existen)
            val textoAceptaAlMenosUnUsuario = stringResource(R.string.acepta_al_menos_un_usuario)
            val textoUsuariosGuardadosCorrectamente = stringResource(R.string.usuarios_guardados_correctamente)

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = {
                        val ultimo = usuariosTemporales.lastOrNull()
                        if (ultimo != null && !validarUsuario(ultimo)) {
                            scope.launch {
                                snackbarHostState.showSnackbar(textoErrorUltimoUsuario)
                            }
                            return@Button
                        }
                        usuariosTemporales = usuariosTemporales.toMutableList().apply {
                            add(UsuarioTemporal())
                        }
                        aceptados.add(false)
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.agregar_otro))
                }

                Button(
                    onClick = {
                        if (usuariosTemporales.isNotEmpty()) {
                            usuariosTemporales = usuariosTemporales.toMutableList().apply {
                                removeAt(size - 1)
                                if (isEmpty()) add(UsuarioTemporal())
                            }
                            if (aceptados.isNotEmpty()) {
                                aceptados.removeAt(aceptados.size - 1)
                                if (aceptados.size < usuariosTemporales.size) {
                                    aceptados.add(false)
                                }
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.eliminar_ultimo_usuario))
                }

                Button(
                    onClick = {
                        if (usuariosTemporales.isEmpty()) {
                            scope.launch {
                                snackbarHostState.showSnackbar(textoNoHayUsuarios)
                            }
                            return@Button
                        }

                        scope.launch {
                            val invalidos = usuariosTemporales.filterNot { validarUsuario(it) }
                            if (invalidos.isNotEmpty()) {
                                snackbarHostState.showSnackbar(textoCorrigeUsuariosInvalidos)
                                return@launch
                            }

                            val yaExisten = existenUsuariosEnBD(usuariosTemporales)
                            if (yaExisten.any { it }) {
                                snackbarHostState.showSnackbar(textoUsuariosYaExisten)
                                return@launch
                            }

                            aceptados.clear()
                            aceptados.addAll(usuariosTemporales.map { validarUsuario(it) })

                            showReviewDialog = true
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(stringResource(R.string.guardar_todos))
                }
            }


            if (showReviewDialog) {
                ReviewUsuariosDialog(
                    usuarios = usuariosTemporales,
                    aceptados = aceptados,
                    onAceptar = { index ->
                        if (validarUsuario(usuariosTemporales[index])) {
                            aceptados[index] = true
                        }
                    },
                    onModificar = {
                        showReviewDialog = false
                    },
                    onEliminar = { index ->
                        usuariosTemporales = usuariosTemporales.toMutableList().apply {
                            removeAt(index)
                            if (isEmpty()) add(UsuarioTemporal())
                        }
                        aceptados.removeAt(index)
                        if (aceptados.size < usuariosTemporales.size) {
                            aceptados.add(false)
                        }
                    },
                    onAceptarTodos = {
                        val usuariosParaGuardar = usuariosTemporales.filterIndexed { i, _ -> aceptados.getOrNull(i) == true }

                        scope.launch {
                            if (usuariosParaGuardar.isEmpty()) {
                                snackbarHostState.showSnackbar(textoAceptaAlMenosUnUsuario)
                            } else {
                                usuariosParaGuardar.forEach { usuario ->
                                    db.usuarioDao().insertar(
                                        Usuario(
                                            nombre = usuario.nombre,
                                            usuario = usuario.usuario,
                                            contrasena = usuario.contrasena,
                                            tipo = usuario.tipo
                                        )
                                    )
                                }
                                usuariosTemporales = mutableListOf(UsuarioTemporal())
                                aceptados.clear()
                                aceptados.add(false)
                                showReviewDialog = false
                                snackbarHostState.showSnackbar(textoUsuariosGuardadosCorrectamente)
                            }
                        }
                    },
                    onDismissRequest = {
                        showReviewDialog = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditableUsuarioCard(
    usuarioTemp: UsuarioTemporal,
    onValueChange: (UsuarioTemporal) -> Unit
) {
    var passwordVisible by remember { mutableStateOf(false) }
    var expanded by remember { mutableStateOf(false) }
    val userTypes = listOf(
        stringResource(R.string.tipo_profesor),
        stringResource(R.string.tipo_alumno)
    )

    val nombreError = usuarioTemp.nombre.isBlank()
    val usuarioError = usuarioTemp.usuario.isBlank()
    val tipoError = usuarioTemp.tipo.isBlank()
    val contrasenaError = !validarContrasena(usuarioTemp.contrasena)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            OutlinedTextField(
                value = usuarioTemp.nombre,
                onValueChange = { onValueChange(usuarioTemp.copy(nombre = it)) },
                label = { Text(stringResource(R.string.nombre_completo)) },
                isError = nombreError,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = usuarioTemp.usuario,
                onValueChange = { onValueChange(usuarioTemp.copy(usuario = it)) },
                label = { Text(stringResource(R.string.usuario_label)) },
                isError = usuarioError,
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = usuarioTemp.contrasena,
                onValueChange = { onValueChange(usuarioTemp.copy(contrasena = it)) },
                label = { Text(stringResource(R.string.contrasena_label)) },
                isError = contrasenaError,
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(
                            imageVector = if (passwordVisible) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                            contentDescription = if (passwordVisible) stringResource(R.string.ocultar_contrasena) else stringResource(R.string.mostrar_contrasena)
                        )
                    }
                }
            )

            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = !expanded }
            ) {
                OutlinedTextField(
                    value = usuarioTemp.tipo,
                    onValueChange = {},
                    readOnly = true,
                    label = { Text(stringResource(R.string.tipo_usuario_label)) },
                    isError = tipoError,
                    trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                    modifier = Modifier
                        .menuAnchor()
                        .fillMaxWidth()
                )
                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    userTypes.forEach { tipo ->
                        DropdownMenuItem(
                            text = { Text(tipo) },
                            onClick = {
                                onValueChange(usuarioTemp.copy(tipo = tipo))
                                expanded = false
                            }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewUsuariosDialog(
    usuarios: List<UsuarioTemporal>,
    aceptados: List<Boolean>,
    onAceptar: (Int) -> Unit,
    onModificar: () -> Unit,
    onEliminar: (Int) -> Unit,
    onAceptarTodos: () -> Unit,
    onDismissRequest: () -> Unit
) {
    val scrollState = rememberScrollState()

    fun validarUsuario(usuario: UsuarioTemporal): Boolean {
        return usuario.nombre.isNotBlank()
                && usuario.usuario.isNotBlank()
                && usuario.tipo.isNotBlank()
                && validarContrasena(usuario.contrasena)
    }

    AlertDialog(
        onDismissRequest = onDismissRequest,
        title = { Text(stringResource(R.string.revisar_usuarios_guardar)) },
        text = {
            Column(
                modifier = Modifier
                    .heightIn(max = 400.dp)
                    .fillMaxWidth()
                    .verticalScroll(scrollState)
            ) {
                usuarios.forEachIndexed { index, usuario ->
                    val esValido = validarUsuario(usuario)
                    val borderColor = if (esValido) Color(0xFF4CAF50) else Color.Red

                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp)
                            .border(2.dp, borderColor, RoundedCornerShape(8.dp)),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(8.dp)
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(text = "${stringResource(R.string.nombre_completo)}: ${usuario.nombre}")
                                Text(text = "${stringResource(R.string.usuario_label)}: ${usuario.usuario}")
                                Text(text = "${stringResource(R.string.tipo_usuario_label)}: ${usuario.tipo}")
                            }

                            if (aceptados.getOrNull(index) == true) {
                                Icon(
                                    imageVector = Icons.Outlined.CheckCircle,
                                    contentDescription = stringResource(R.string.usuario_aceptado),
                                    tint = Color.Green,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                            } else {
                                Icon(
                                    imageVector = Icons.Outlined.Close,
                                    contentDescription = stringResource(R.string.usuario_no_aceptado),
                                    tint = Color.Red,
                                    modifier = Modifier.padding(end = 8.dp)
                                )
                            }

                            if (!aceptados.getOrNull(index)!!) {
                                IconButton(onClick = { onAceptar(index) }) {
                                    Icon(
                                        imageVector = Icons.Filled.Check,
                                        contentDescription = stringResource(R.string.aceptar_usuario)
                                    )
                                }
                            }

                            IconButton(onClick = onModificar) {
                                Icon(
                                    imageVector = Icons.Filled.Edit,
                                    contentDescription = stringResource(R.string.modificar_usuario)
                                )
                            }

                            IconButton(onClick = { onEliminar(index) }) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = stringResource(R.string.eliminar_usuario)
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = onAceptarTodos) {
                Text(stringResource(R.string.aceptar_todos))
            }
        },
        dismissButton = {
            Button(onClick = onDismissRequest) {
                Text(stringResource(R.string.cancelar))
            }
        }
    )
}

data class UsuarioTemporal(
    val nombre: String = "",
    val usuario: String = "",
    val contrasena: String = "",
    val tipo: String = ""
)