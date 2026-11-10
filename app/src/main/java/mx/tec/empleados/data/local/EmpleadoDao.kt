package mx.tec.empleados.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
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
    // REPLACE: si ya existe un empleado con ese id, lo reemplaza.
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertar(empleado: Empleado)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertarTodos(empleados: List<Empleado>)

    @Update
    suspend fun actualizar(empleado: Empleado)

    @Query("DELETE FROM empleados WHERE id = :id")
    suspend fun eliminarPorId(id: Int)

    // NUEVO para trabajar con la API:

    // Los creados sin conexión (falta enviarlos).
    @Query("SELECT * FROM empleados WHERE pendiente = 1")
    suspend fun pendientes(): List<Empleado>

    // Para el id temporal: -1, -2, -3...
    @Query("SELECT MIN(id) FROM empleados")
    suspend fun menorId(): Int?

    // Borra la copia de lo que ya está en el servidor (no toca los pendientes).
    @Query("DELETE FROM empleados WHERE pendiente = 0")
    suspend fun borrarSincronizados()
}
