package mx.tec.empleados.data.model

// Un empleado. En clase lo convertiremos en una tabla de Room.
data class Empleado(
    val id: Int = 0,               // 0 = nuevo (todavía no se ha guardado)
    val nombre: String,
    val correo: String,
    val puesto: String = "",
    val departamento: String = "",
    val activo: Boolean = true,
)
