package mx.tec.empleados.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import mx.tec.empleados.data.local.AppDatabase
import mx.tec.empleados.data.model.Empleado
import mx.tec.empleados.data.repository.EmpleadoRepository

// Todo lo que las pantallas necesitan saber.
data class EmpleadosUiState(
    val empleados: List<Empleado> = emptyList(),
)

// Se crea UNA vez en AppNavigation y lo comparten la lista y el formulario.
// AndroidViewModel: necesita el Context (app) para abrir la base de datos.
class EmpleadosViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = EmpleadoRepository(AppDatabase.getInstance(app).empleadoDao())

    private val _estado = MutableStateFlow(EmpleadosUiState())
    val estado: StateFlow<EmpleadosUiState> = _estado.asStateFlow()

    init {
        // Escuchamos la tabla: cada vez que cambia, la pantalla se actualiza sola.
        viewModelScope.launch {
            repo.empleados.collect { lista ->
                _estado.value = EmpleadosUiState(empleados = lista)
            }
        }
    }

    // id == 0 -> crear; si no -> actualizar.
    fun guardar(empleado: Empleado, onListo: () -> Unit) {
        viewModelScope.launch {
            repo.guardar(empleado)
            onListo()
        }
    }

    fun eliminar(id: Int, onListo: () -> Unit) {
        viewModelScope.launch {
            repo.eliminar(id)
            onListo()
        }
    }
}
