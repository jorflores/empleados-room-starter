package mx.tec.empleados.data.repository

import kotlinx.coroutines.flow.Flow
import mx.tec.empleados.data.local.EmpleadoDao
import mx.tec.empleados.data.model.Empleado
import mx.tec.empleados.data.remote.EmpleadosApi
import mx.tec.empleados.data.remote.RetrofitClient
import retrofit2.HttpException
import java.io.IOException

// Único lugar que sabe DÓNDE están los empleados: en Room y en la API.
// Regla: la pantalla SIEMPRE lee de Room. La API llena Room.
class EmpleadoRepository(
    private val dao: EmpleadoDao,
    private val api: EmpleadosApi = RetrofitClient.api,
) {
    val empleados: Flow<List<Empleado>> = dao.obtenerTodos()

    // 1) Envía a la API lo que se creó sin conexión.
    // 2) Trae la lista del servidor y la guarda en Room.
    // Sin red lanza IOException: la pantalla sigue mostrando lo que hay en Room.
    suspend fun sincronizar() {
        for (p in dao.pendientes()) {
            val creado = api.crear(p)     // POST: el servidor le da su id real
            dao.eliminarPorId(p.id)       // quita la copia con id temporal (negativo)...
            dao.insertar(creado)          // ...y guarda la que regresó la API
        }
        val remotos = api.listar()
        dao.borrarSincronizados()         // deja solo los pendientes (si quedó alguno)
        dao.insertarTodos(remotos)
    }

    // id == 0 -> crear; si no -> actualizar.
    suspend fun guardar(empleado: Empleado) {
        when {
            // CREAR: se intenta en la API; si no hay red, se guarda en Room como pendiente.
            empleado.id == 0 -> try {
                dao.insertar(api.crear(empleado))
            } catch (e: IOException) {
                val idTemporal = minOf(dao.menorId() ?: 0, 0) - 1   // -1, -2, -3...
                dao.insertar(empleado.copy(id = idTemporal, pendiente = true))
            }
            // Id negativo: todavía no existe en el servidor, solo se cambia en Room.
            empleado.id < 0 -> dao.actualizar(empleado.copy(pendiente = true))
            // EDITAR: necesita red. Primero la API y, si salió bien, Room.
            else -> dao.actualizar(api.actualizar(empleado.id, empleado))
        }
    }

    suspend fun eliminar(id: Int) {
        // Id negativo: nunca llegó al servidor, solo se borra de Room.
        if (id > 0) {
            // BORRAR: necesita red. 404 = ya no existía en el servidor; igual lo quitamos.
            val respuesta = api.borrar(id)
            if (!respuesta.isSuccessful && respuesta.code() != 404) throw HttpException(respuesta)
        }
        dao.eliminarPorId(id)
    }
}
