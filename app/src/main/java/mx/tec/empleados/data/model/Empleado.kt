package mx.tec.empleados.data.model

// Un empleado tal como lo usa la app (pantallas, ViewModel y Repository).
// No depende de Retrofit ni de Room: cada uno tiene su propia clase y se convierte a esta.
data class Empleado(
    val id: Int = 0,               // 0 = todavía no existe en el servidor (nuevo)
    val nombre: String,
    val correo: String,
    val puesto: String = "",
    val departamento: String = "",
    val activo: Boolean = true,
)
