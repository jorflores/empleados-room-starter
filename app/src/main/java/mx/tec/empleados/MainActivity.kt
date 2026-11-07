package mx.tec.empleados

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import mx.tec.empleados.navigation.AppNavigation
import mx.tec.empleados.ui.theme.EmpleadosTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            EmpleadosTheme {
                AppNavigation()
            }
        }
    }
}
