package com.ahorro.familiar.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ahorro.familiar.ui.screens.CrearMetaScreen
import com.ahorro.familiar.ui.screens.DetalleMetaScreen
import com.ahorro.familiar.ui.screens.ListaMetasScreen
import com.ahorro.familiar.ui.screens.RegistrarPagoScreen
import com.ahorro.familiar.ui.viewmodel.MetasViewModel


object Rutas {
    const val LISTA_METAS   = "lista_metas"
    const val DETALLE_META  = "detalle_meta/{metaId}"
    const val REGISTRAR_PAGO = "registrar_pago/{metaId}"
    const val CREAR_META    = "crear_meta"

    fun detalleMetaRuta(metaId: Int)    = "detalle_meta/$metaId"
    fun registrarPagoRuta(metaId: Int)  = "registrar_pago/$metaId"
}

@Composable
fun AhorroNavHost() {
    val navController = rememberNavController()
    val viewModel: MetasViewModel = viewModel()

    NavHost(
        navController = navController,
        startDestination = Rutas.LISTA_METAS
    ) {

        composable(Rutas.LISTA_METAS) {
            ListaMetasScreen(
                viewModel  = viewModel,
                onMetaClick = { metaId -> navController.navigate(Rutas.detalleMetaRuta(metaId)) },
                onCrearMeta = { navController.navigate(Rutas.CREAR_META) }
            )
        }


        composable(
            route     = Rutas.DETALLE_META,
            arguments = listOf(navArgument("metaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val metaId = backStackEntry.arguments?.getInt("metaId") ?: return@composable
            DetalleMetaScreen(
                viewModel       = viewModel,
                metaId          = metaId,
                onRegistrarPago = { navController.navigate(Rutas.registrarPagoRuta(metaId)) },
                onVolver        = { navController.popBackStack() }
            )
        }


        composable(
            route     = Rutas.REGISTRAR_PAGO,
            arguments = listOf(navArgument("metaId") { type = NavType.IntType })
        ) { backStackEntry ->
            val metaId = backStackEntry.arguments?.getInt("metaId") ?: return@composable
            RegistrarPagoScreen(
                viewModel = viewModel,
                metaId    = metaId,
                onVolver  = { navController.popBackStack() }
            )
        }


        composable(Rutas.CREAR_META) {
            CrearMetaScreen(
                viewModel = viewModel,
                onVolver  = { navController.popBackStack() }
            )
        }
    }
}