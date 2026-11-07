package mx.tec.empleados.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import mx.tec.empleados.data.model.Empleado
import mx.tec.empleados.data.repository.EmpleadoRepository
import retrofit2.HttpException
import java.io.IOException

// Todo lo que las pantallas necesitan saber.
data class EmpleadosUiState(
    val cargando: Boolean = false,          // descargando la lista
    val guardando: Boolean = false,         // creando, actualizando o borrando
    val empleados: List<Empleado> = emptyList(),
    val error: String? = null,
)

// Se crea UNA vez en AppNavigation y lo comparten la lista y el formulario.
class EmpleadosViewModel(
    private val repo: EmpleadoRepository = EmpleadoRepository(),
) : ViewModel() {

    private val _estado = MutableStateFlow(EmpleadosUiState())
    val estado: StateFlow<EmpleadosUiState> = _estado.asStateFlow()

    init {
        cargar()
    }

    fun cargar() {
        viewModelScope.launch {
            _estado.update { it.copy(cargando = true, error = null) }
            try {
                val lista = repo.obtenerTodos()
                _estado.update { it.copy(cargando = false, empleados = lista) }
            } catch (e: Exception) {
                _estado.update { it.copy(cargando = false, error = mensajeDeError(e)) }
            }
        }
    }

    // id == 0 -> crear; si no -> actualizar. onListo se llama solo si salió bien.
    fun guardar(empleado: Empleado, onListo: () -> Unit) {
        viewModelScope.launch {
            _estado.update { it.copy(guardando = true, error = null) }
            try {
                if (empleado.id == 0) {
                    val nuevo = repo.crear(empleado)
                    _estado.update { it.copy(guardando = false, empleados = it.empleados + nuevo) }
                } else {
                    val actualizado = repo.actualizar(empleado)
                    _estado.update { s ->
                        s.copy(
                            guardando = false,
                            empleados = s.empleados.map { if (it.id == actualizado.id) actualizado else it },
                        )
                    }
                }
                onListo()
            } catch (e: Exception) {
                _estado.update { it.copy(guardando = false, error = mensajeDeError(e)) }
            }
        }
    }

    fun eliminar(id: Int, onListo: () -> Unit) {
        viewModelScope.launch {
            _estado.update { it.copy(guardando = true, error = null) }
            try {
                repo.eliminar(id)
                _estado.update { s -> s.copy(guardando = false, empleados = s.empleados.filter { it.id != id }) }
                onListo()
            } catch (e: Exception) {
                _estado.update { it.copy(guardando = false, error = mensajeDeError(e)) }
            }
        }
    }

    fun limpiarError() {
        _estado.update { it.copy(error = null) }
    }

    private fun mensajeDeError(e: Exception): String = when (e) {
        is IOException -> "Sin conexión con el servidor"
        is HttpException -> when (e.code()) {
            400 -> "Revisa los datos: nombre y correo son obligatorios"
            404 -> "Ese empleado ya no existe"
            409 -> "Ese correo ya está registrado"
            else -> "Error del servidor (${e.code()})"
        }
        else -> "Error inesperado: ${e.message}"
    }
}
