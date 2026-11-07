package mx.tec.empleados.data.remote

import kotlinx.serialization.Serializable

// Un empleado: el mismo JSON que manda y recibe la API.
// Lo usan Retrofit, el ViewModel y las pantallas.
@Serializable
data class Empleado(
    val id: Int = 0,               // 0 = nuevo (el servidor le asigna el id real al crearlo)
    val nombre: String,
    val correo: String,
    val puesto: String = "",
    val departamento: String = "",
    val activo: Boolean = true,
)
