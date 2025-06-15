package com.example.appcomunicacionalumnado

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.*


@Composable
fun AppComunicacionNav() {
    val navController = rememberNavController()
    val context = LocalContext.current
    val db = remember { AppDatabase.getInstance(context) }

    NavHost(navController = navController, startDestination = "login") {
        composable("login") { PantallaLogin(navController, db) }
        composable("menu/{tipoUsuario}") { backStackEntry ->
            val tipoUsuario = backStackEntry.arguments?.getString("tipoUsuario") ?: "alumno"
            PantallaMenu(navController, tipoUsuario)
        }
        composable("comunicacion") {
            PantallaComunicacion(navController)
        }
        composable("configuracion") { PantallaConfiguracion(navController) }
        composable("usuarios") { PantallaUsuarios(navController, db) }
        composable("verUsuarios") { PantallaVerUsuarios(navController, db) }
    }
}
