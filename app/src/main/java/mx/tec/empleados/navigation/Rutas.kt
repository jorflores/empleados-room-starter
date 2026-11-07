package mx.tec.empleados.navigation

object Rutas {
    const val LISTA = "empleados"

    // Formulario: id = 0 para un empleado nuevo; otro número para editar.
    const val FORMULARIO = "empleado/{id}"
    fun formulario(id: Int) = "empleado/$id"
}
