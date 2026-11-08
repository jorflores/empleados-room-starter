package mx.tec.empleados.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import mx.tec.empleados.data.model.Empleado

// version 2: la tabla ganó la columna "pendiente".
@Database(entities = [Empleado::class], version = 2)
abstract class AppDatabase : RoomDatabase() {

    abstract fun empleadoDao(): EmpleadoDao

    companion object {
        @Volatile private var INSTANCE: AppDatabase? = null

        // Una sola base de datos para toda la app.
        fun getInstance(context: Context): AppDatabase =
            INSTANCE ?: synchronized(this) {
                INSTANCE ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "empleados.db"
                )
                    // Solo en desarrollo: si cambia la versión, borra la base y la crea de nuevo.
                    .fallbackToDestructiveMigration(dropAllTables = true)
                    .build()
                    .also { INSTANCE = it }
            }
    }
}
