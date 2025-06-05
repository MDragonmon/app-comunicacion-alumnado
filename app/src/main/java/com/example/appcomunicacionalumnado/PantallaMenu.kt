package com.example.appcomunicacionalumnado

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.compose.ui.res.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaMenu(navController: NavHostController, tipoUsuario: String) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text(stringResource(R.string.menu_principal)) })
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(onClick = { navController.navigate("comunicacion") }, modifier = Modifier.fillMaxWidth().height(56.dp)) {
                Text(stringResource(R.string.comunicacion))
            }
            Button(onClick = { navController.navigate("configuracion") }, modifier = Modifier.fillMaxWidth().height(56.dp)) {
                Text(stringResource(R.string.configuracion_usuario))
            }

            if (tipoUsuario.equals("Profesor", ignoreCase = true)) {
                Button(
                    onClick = { navController.navigate("registroUsuario") },
                    modifier = Modifier.fillMaxWidth().height(56.dp)
                ) {
                    Text(stringResource(R.string.registrar_usuario_titulo))
                }
            }
        }
    }
}
