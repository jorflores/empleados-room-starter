package mx.tec.empleados.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import mx.tec.empleados.data.model.Empleado

@Database(entities = [Empleado::class], version = 1)
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
                ).build().also { INSTANCE = it }
            }
    }
}
