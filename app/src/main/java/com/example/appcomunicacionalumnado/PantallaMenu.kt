package com.example.appcomunicacionalumnado

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.appcomunicacionalumnado.data.ConfiguracionDataStore
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaMenu(navController: NavHostController, tipoUsuario: String) {
    val context = LocalContext.current
    val configuracionDataStore = remember { ConfiguracionDataStore(context) }
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(stringResource(R.string.menu_principal), style = MaterialTheme.typography.headlineSmall) }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { navController.navigate("comunicacion") },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(stringResource(R.string.comunicacion), style = MaterialTheme.typography.titleMedium)
            }

            Button(
                onClick = { navController.navigate("configuracion") },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(stringResource(R.string.configuracion_usuario), style = MaterialTheme.typography.titleMedium)
            }

            if (tipoUsuario.equals("Profesor", ignoreCase = true)) {
                Button(
                    onClick = { navController.navigate("usuarios") },
                    modifier = Modifier.fillMaxWidth().height(54.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(stringResource(R.string.pantalla_usuarios), style = MaterialTheme.typography.titleMedium)
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = {
                    scope.launch {
                        configuracionDataStore.cerrarSesion()
                        navController.navigate("login") {
                            popUpTo(0)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                shape = MaterialTheme.shapes.medium
            ) {
                Text(stringResource(R.string.cerrar_sesion), color = MaterialTheme.colorScheme.onError, style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}

