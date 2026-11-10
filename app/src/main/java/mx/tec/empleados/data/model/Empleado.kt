package mx.tec.empleados.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient   // OJO: el de kotlinx, no kotlin.jvm.Transient

// Una sola clase para todo:
// - @Entity: es una fila de la tabla "empleados" de Room
// - @Serializable: es el JSON que manda y recibe la API
@Serializable
@Entity(tableName = "empleados")
data class Empleado(
    @PrimaryKey
    val id: Int = 0,               // id del servidor. NEGATIVO = creado sin conexión (id temporal)
    val nombre: String,
    val correo: String,
    val puesto: String = "",
    val departamento: String = "",
    val activo: Boolean = true,
    // true = se creó sin conexión y falta enviarlo a la API.
    // @Transient (de kotlinx): Room SÍ lo guarda, pero NO viaja en el JSON.
    @Transient val pendiente: Boolean = false,
)
