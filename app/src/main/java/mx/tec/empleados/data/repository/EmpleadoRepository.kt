package mx.tec.empleados.data.repository

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import mx.tec.empleados.data.local.EmpleadoDao
import mx.tec.empleados.data.model.Empleado
import mx.tec.empleados.data.model.Pendiente
import mx.tec.empleados.data.remote.EmpleadosApi
import mx.tec.empleados.data.remote.RetrofitClient
import retrofit2.HttpException
import java.io.IOException

// Único lugar que sabe DÓNDE están los empleados.
// Regla: la pantalla SIEMPRE lee de Room. La API solo se usa para descargar y para enviar pendientes.
class EmpleadoRepository(
    private val dao: EmpleadoDao,
    private val api: EmpleadosApi = RetrofitClient.api,
) {
    // Evita que dos sincronizaciones corran al mismo tiempo (se duplicarían los POST).
    private val mutex = Mutex()

    val empleados: Flow<List<Empleado>> = dao.obtenerTodos()

    // Trae la lista de la API y la guarda en Room. Sin conexión lanza IOException.
    suspend fun descargar() {
        val remotos = api.listar()
        // No pisamos los que tienen cambios sin enviar.
        val idsPendientes = dao.pendientes().map { it.id }.toSet()
        dao.guardarTodos(remotos.filter { it.id !in idsPendientes })
        dao.borrarLosQueYaNoExisten(remotos.map { it.id })
    }

    // GUARDAR y ELIMINAR: primero Room (funciona sin red), después se intenta enviar a la API.

    // id == 0 -> es nuevo; si no -> ya existe.
    suspend fun guardar(empleado: Empleado) {
        if (empleado.id == 0) {
            val idTemporal = minOf(dao.menorId() ?: 0, 0) - 1   // -1, -2, -3...
            dao.guardar(empleado.copy(id = idTemporal, pendiente = Pendiente.CREAR))
        } else {
            val anterior = dao.buscar(empleado.id)
            // Si todavía no existe en el servidor, sigue pendiente de CREAR (con los datos nuevos).
            val pendiente = if (anterior?.pendiente == Pendiente.CREAR) Pendiente.CREAR else Pendiente.ACTUALIZAR
            dao.guardar(empleado.copy(pendiente = pendiente))
        }
        intentarSincronizar()
    }

    suspend fun eliminar(id: Int) {
        val anterior = dao.buscar(id) ?: return
        if (anterior.pendiente == Pendiente.CREAR) {
            dao.eliminarPorId(id)                                     // nunca llegó al servidor
        } else {
            dao.guardar(anterior.copy(pendiente = Pendiente.BORRAR))  // se borra al sincronizar
        }
        intentarSincronizar()
    }

    // Envía a la API todo lo pendiente. Sin conexión lanza IOException y lo que falta se queda pendiente.
    suspend fun sincronizar() = mutex.withLock {
        for (p in dao.pendientes()) {
            try {
                when (p.pendiente) {
                    Pendiente.CREAR -> {
                        val creado = api.crear(p)
                        dao.eliminarPorId(p.id)   // quita el id temporal (negativo)...
                        dao.guardar(creado)       // ...y lo guarda con el id real del servidor
                    }
                    Pendiente.ACTUALIZAR -> {
                        dao.guardar(api.actualizar(p.id, p))
                    }
                    Pendiente.BORRAR -> {
                        val respuesta = api.borrar(p.id)
                        // 404: ya no existía en el servidor; igual lo quitamos.
                        if (!respuesta.isSuccessful && respuesta.code() != 404) throw HttpException(respuesta)
                        dao.eliminarPorId(p.id)
                    }
                }
            } catch (e: HttpException) {
                // El servidor rechazó ESTE cambio (por ejemplo, 409 correo repetido).
                // Se queda pendiente y seguimos con los demás.
            }
        }
    }

    private suspend fun intentarSincronizar() {
        try {
            sincronizar()
        } catch (e: IOException) {
            // Sin conexión: el cambio ya está en Room; se enviará cuando regrese la red.
        }
    }
}
