package mx.tec.empleados.data.remote

import kotlinx.serialization.Serializable
import mx.tec.empleados.data.model.Empleado

// Lo que RESPONDE la API (GET, POST y PUT).
@Serializable
data class EmpleadoDto(
    val id: Int,
    val nombre: String,
    val correo: String,
    val puesto: String,
    val departamento: String,
    val activo: Boolean,
)

// Lo que se MANDA a la API al crear o actualizar (sin id: el id va en la URL o lo pone el servidor).
@Serializable
data class EmpleadoRequest(
    val nombre: String,
    val correo: String,
    val puesto: String,
    val departamento: String,
    val activo: Boolean,
)

// Conversiones entre "lenguaje del servidor" (DTO) y "lenguaje de la app" (Empleado).
fun EmpleadoDto.toModel() = Empleado(id, nombre, correo, puesto, departamento, activo)

fun Empleado.toRequest() = EmpleadoRequest(nombre, correo, puesto, departamento, activo)
