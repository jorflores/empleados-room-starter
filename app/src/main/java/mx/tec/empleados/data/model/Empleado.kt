package mx.tec.empleados.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// Un empleado = una fila de la tabla "empleados".
@Entity(tableName = "empleados")
data class Empleado(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,               // 0 = nuevo: Room le asigna 1, 2, 3... al insertarlo
    val nombre: String,
    val correo: String,
    val puesto: String = "",
    val departamento: String = "",
    val activo: Boolean = true,
)
