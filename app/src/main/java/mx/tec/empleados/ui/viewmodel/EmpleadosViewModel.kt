package mx.tec.empleados.ui.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import mx.tec.empleados.data.model.Empleado

// Todo lo que las pantallas necesitan saber.
data class EmpleadosUiState(
    val empleados: List<Empleado> = emptyList(),
)

// Se crea UNA vez en AppNavigation y lo comparten la lista y el formulario.
// TODO (Room): por ahora no guarda nada. En clase lo conectaremos con Room.
class EmpleadosViewModel : ViewModel() {

    private val _estado = MutableStateFlow(EmpleadosUiState())
    val estado: StateFlow<EmpleadosUiState> = _estado.asStateFlow()

    // id == 0 -> crear; si no -> actualizar.
    fun guardar(empleado: Empleado, onListo: () -> Unit) {
        // TODO (Room): guardar el empleado en la base de datos
        onListo()
    }

    fun eliminar(id: Int, onListo: () -> Unit) {
        // TODO (Room): borrar el empleado de la base de datos
        onListo()
    }
}
