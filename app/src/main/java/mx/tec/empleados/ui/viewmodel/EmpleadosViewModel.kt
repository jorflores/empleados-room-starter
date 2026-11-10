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
import mx.tec.empleados.data.repository.EmpleadoRepository
import retrofit2.HttpException
import java.io.IOException

// Todo lo que las pantallas necesitan saber.
data class EmpleadosUiState(
    val empleados: List<Empleado> = emptyList(),
    val sincronizando: Boolean = false,
    val error: String? = null,
)

// Se crea UNA vez en AppNavigation y lo comparten la lista y el formulario.
// AndroidViewModel: necesita el Context (app) para abrir la base de datos.
class EmpleadosViewModel(app: Application) : AndroidViewModel(app) {

    // Equivale a escribirlo en tres pasos:
    // val db = AppDatabase.getInstance(app)
    // val dao = db.empleadoDao()
    // private val repo = EmpleadoRepository(dao)
    private val repo = EmpleadoRepository(AppDatabase.getInstance(app).empleadoDao())

    private val _estado = MutableStateFlow(EmpleadosUiState())
    val estado: StateFlow<EmpleadosUiState> = _estado.asStateFlow()

    init {
        // Escuchamos la tabla: cada vez que cambia, la pantalla se actualiza sola.
        viewModelScope.launch {
            repo.empleados.collect { lista ->
                _estado.update { it.copy(empleados = lista) }
            }
        }
        // Al abrir la app: enviar pendientes y traer lo nuevo.
        sincronizar()
    }

    // Se llama al abrir la app y con el botón ↻.
    fun sincronizar() {
        viewModelScope.launch {
            _estado.update { it.copy(sincronizando = true, error = null) }
            try {
                repo.sincronizar()
            } catch (e: Exception) {
                _estado.update { it.copy(error = mensajeDeError(e)) }
            }
            _estado.update { it.copy(sincronizando = false) }
        }
    }

    // id == 0 -> crear; si no -> actualizar. onListo se llama solo si salió bien.
    fun guardar(empleado: Empleado, onListo: () -> Unit) {
        viewModelScope.launch {
            try {
                repo.guardar(empleado)
                onListo()
            } catch (e: Exception) {
                _estado.update { it.copy(error = mensajeDeError(e)) }
            }
        }
    }

    fun eliminar(id: Int, onListo: () -> Unit) {
        viewModelScope.launch {
            try {
                repo.eliminar(id)
                onListo()
            } catch (e: Exception) {
                _estado.update { it.copy(error = mensajeDeError(e)) }
            }
        }
    }

    fun limpiarError() {
        _estado.update { it.copy(error = null) }
    }

    private fun mensajeDeError(e: Exception): String = when (e) {
        is IOException -> "Sin conexión con el servidor. Para editar o borrar necesitas internet."
        is HttpException -> when (e.code()) {
            409 -> "Ese correo ya está registrado"
            else -> "Error del servidor (${e.code()})"
        }
        else -> "Error inesperado: ${e.message}"
    }
}
