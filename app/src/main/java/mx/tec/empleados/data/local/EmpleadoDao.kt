package mx.tec.empleados.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow
import mx.tec.empleados.data.model.Empleado

@Dao
interface EmpleadoDao {

    // Leer: un Flow que emite la lista nueva cada vez que cambia la tabla.
    @Query("SELECT * FROM empleados ORDER BY nombre")
    fun obtenerTodos(): Flow<List<Empleado>>

    // Escribir: funciones suspend.
    @Insert
    suspend fun insertar(empleado: Empleado)

    @Update
    suspend fun actualizar(empleado: Empleado)

    @Query("DELETE FROM empleados WHERE id = :id")
    suspend fun eliminarPorId(id: Int)
}
