package mx.tec.empleados.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import mx.tec.empleados.ui.screens.EmpleadoFormScreen
import mx.tec.empleados.ui.screens.EmpleadosScreen
import mx.tec.empleados.ui.viewmodel.EmpleadosViewModel

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    // Un solo ViewModel para la lista y el formulario: los dos ven los mismos empleados.
    val vm: EmpleadosViewModel = viewModel()
    val estado by vm.estado.collectAsState()

    NavHost(navController = navController, startDestination = Rutas.LISTA) {

        composable(Rutas.LISTA) {
            EmpleadosScreen(
                empleados = estado.empleados,
                enLinea = estado.enLinea,
                onRecargar = { vm.cargar() },
                onAgregar = { navController.navigate(Rutas.formulario(0)) },
                onEditar = { id -> navController.navigate(Rutas.formulario(id)) },
            )
        }

        composable(
            route = Rutas.FORMULARIO,
            arguments = listOf(navArgument("id") { type = NavType.IntType }),
        ) { entrada ->
            val id = entrada.arguments?.getInt("id") ?: 0
            // null = empleado nuevo. remember: se toma una sola vez al abrir el formulario
            // (así no cambia a "Nuevo" mientras la pantalla se cierra después de borrar).
            val empleado = remember(id) { estado.empleados.find { it.id == id } }

            EmpleadoFormScreen(
                empleado = empleado,
                onGuardar = { datos -> vm.guardar(datos) { navController.popBackStack() } },
                onEliminar = { vm.eliminar(id) { navController.popBackStack() } },
                onRegresar = { navController.popBackStack() },
            )
        }
    }
}
