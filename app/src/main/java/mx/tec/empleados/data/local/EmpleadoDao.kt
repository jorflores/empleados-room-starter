package mx.tec.empleados.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow
import mx.tec.empleados.data.model.Empleado

@Dao
interface EmpleadoDao {

    // Lo que se ve en pantalla: todo, menos lo que está marcado para borrar.
    @Query("SELECT * FROM empleados WHERE pendiente IS NULL OR pendiente != 'borrar' ORDER BY nombre")
    fun obtenerTodos(): Flow<List<Empleado>>

    @Query("SELECT * FROM empleados WHERE id = :id")
    suspend fun buscar(id: Int): Empleado?

    // Los cambios que faltan enviar a la API.
    @Query("SELECT * FROM empleados WHERE pendiente IS NOT NULL")
    suspend fun pendientes(): List<Empleado>

    // Para generar ids temporales negativos: -1, -2, -3...
    @Query("SELECT MIN(id) FROM empleados")
    suspend fun menorId(): Int?

    // REPLACE: si ya existe un empleado con ese id, lo reemplaza (sirve para insertar y actualizar).
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardar(empleado: Empleado)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun guardarTodos(empleados: List<Empleado>)

    @Query("DELETE FROM empleados WHERE id = :id")
    suspend fun eliminarPorId(id: Int)

    // Quita los empleados que ya se borraron en el servidor (solo los que no tienen cambios pendientes).
    @Query("DELETE FROM empleados WHERE pendiente IS NULL AND id NOT IN (:idsServidor)")
    suspend fun borrarLosQueYaNoExisten(idsServidor: List<Int>)
}
