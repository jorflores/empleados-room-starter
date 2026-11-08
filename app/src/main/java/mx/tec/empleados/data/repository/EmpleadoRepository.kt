package mx.tec.empleados.data.repository

import kotlinx.coroutines.flow.Flow
import mx.tec.empleados.data.local.EmpleadoDao
import mx.tec.empleados.data.model.Empleado

// Único lugar que sabe DÓNDE se guardan los empleados (hoy: Room).
class EmpleadoRepository(private val dao: EmpleadoDao) {

    val empleados: Flow<List<Empleado>> = dao.obtenerTodos()

    // id == 0 -> es nuevo; si no -> ya existe.
    suspend fun guardar(empleado: Empleado) {
        if (empleado.id == 0) dao.insertar(empleado) else dao.actualizar(empleado)
    }

    suspend fun eliminar(id: Int) {
        dao.eliminarPorId(id)
    }
}
