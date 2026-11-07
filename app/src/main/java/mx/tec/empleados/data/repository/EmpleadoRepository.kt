package mx.tec.empleados.data.repository

import mx.tec.empleados.data.remote.Empleado
import mx.tec.empleados.data.remote.EmpleadosApi
import mx.tec.empleados.data.remote.RetrofitClient
import retrofit2.HttpException

// Único lugar que sabe DE DÓNDE vienen los empleados.
// HOY: todo viene de la API (si no hay internet, la lista no carga).
// EN CLASE: agregaremos Room para que la lista se vea sin conexión.
class EmpleadoRepository(
    private val api: EmpleadosApi = RetrofitClient.api,
) {

    suspend fun obtenerTodos(): List<Empleado> = api.listar()

    suspend fun crear(empleado: Empleado): Empleado = api.crear(empleado)

    suspend fun actualizar(empleado: Empleado): Empleado = api.actualizar(empleado.id, empleado)

    suspend fun eliminar(id: Int) {
        val respuesta = api.borrar(id)
        if (!respuesta.isSuccessful) throw HttpException(respuesta)
    }
}
