package mx.tec.empleados.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mx.tec.empleados.data.local.AppDatabase
import mx.tec.empleados.data.model.Empleado
import mx.tec.empleados.data.red.ConectividadObserver
import mx.tec.empleados.data.repository.EmpleadoRepository

// Todo lo que las pantallas necesitan saber.
data class EmpleadosUiState(
    val empleados: List<Empleado> = emptyList(),
    val enLinea: Boolean = true,            // ¿hay red?
)

// Se crea UNA vez en AppNavigation y lo comparten la lista y el formulario.
// AndroidViewModel: necesita el Context (app) para abrir la base de datos y escuchar la red.
class EmpleadosViewModel(app: Application) : AndroidViewModel(app) {

    // Equivale a escribirlo en tres pasos:
    // val db = AppDatabase.getInstance(app)
    // val dao = db.empleadoDao()
    // private val repo = EmpleadoRepository(dao)
    private val repo = EmpleadoRepository(AppDatabase.getInstance(app).empleadoDao())
    private val conectividad = ConectividadObserver(app)

    private val _estado = MutableStateFlow(EmpleadosUiState())
    val estado: StateFlow<EmpleadosUiState> = _estado.asStateFlow()

    init {
        // 1. Escuchamos la tabla: cada vez que cambia, la pantalla se actualiza sola.
        viewModelScope.launch {
            repo.empleados.collect { lista ->
                _estado.update { it.copy(empleados = lista) }
            }
        }
        // 2. Escuchamos la red: al abrir la app y cada vez que regresa, enviamos pendientes y descargamos.
        viewModelScope.launch {
            conectividad.hayInternet.collect { enLinea ->
                _estado.update { it.copy(enLinea = enLinea) }
                if (enLinea) cargar()
            }
        }
    }

    // Primero envía lo pendiente y después descarga lo nuevo (en ese orden).
    // Si falla (sin red o API apagada), la pantalla sigue con lo guardado en Room.
    fun cargar() {
        viewModelScope.launch {
            try {
                repo.sincronizar()
                repo.descargar()
            } catch (e: Exception) {
                // Sin conexión con la API: se reintenta con ↻ o cuando regrese la red.
            }
        }
    }

    // id == 0 -> crear; si no -> actualizar. Siempre se guarda en Room, con o sin red.
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
