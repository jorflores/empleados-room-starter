package mx.tec.empleados.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import mx.tec.empleados.data.model.Empleado

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmpleadosScreen(
    empleados: List<Empleado>,
    enLinea: Boolean,
    onRecargar: () -> Unit,
    onAgregar: () -> Unit,
    onEditar: (Int) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Empleados") },
                actions = {
                    // Reintenta enviar pendientes y descargar (por si la API estaba apagada)
                    IconButton(onClick = onRecargar) {
                        Icon(Icons.Filled.Refresh, contentDescription = "Recargar")
                    }
                },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onAgregar) {
                Icon(Icons.Filled.Add, contentDescription = "Agregar empleado")
            }
        },
    ) { innerPadding ->
        Column(Modifier.padding(innerPadding)) {
            // Sin red: avisamos que los cambios se guardan en el teléfono
            if (!enLinea) {
                AvisoSinConexion()
            }
            if (empleados.isEmpty()) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text(
                        "No hay empleados. Agrega uno con el botón +",
                        textAlign = TextAlign.Center,
                    )
                }
            } else {
                LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(empleados, key = { it.id }) { empleado ->
                        TarjetaEmpleado(empleado, onClick = { onEditar(empleado.id) })
                    }
                }
            }
        }
    }
}

@Composable
private fun TarjetaEmpleado(empleado: Empleado, onClick: () -> Unit) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
    ) {
        ListItem(
            leadingContent = { Inicial(empleado.nombre) },
            headlineContent = { Text(empleado.nombre) },
            supportingContent = {
                val detalle = listOf(empleado.puesto, empleado.departamento)
                    .filter { it.isNotBlank() }
                    .joinToString(" · ")
                Text(detalle.ifBlank { empleado.correo })
            },
            trailingContent = {
                if (empleado.pendiente != null) {
                    // Guardado en el teléfono; falta enviarlo a la API
                    Text(
                        "Pendiente",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.tertiary,
                    )
                } else if (!empleado.activo) {
                    Text(
                        "Inactivo",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.error,
                    )
                }
            },
        )
    }
}

@Composable
private fun AvisoSinConexion() {
    Surface(
        color = MaterialTheme.colorScheme.tertiaryContainer,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = MaterialTheme.shapes.medium,
    ) {
        Text(
            "Sin conexión. Los cambios se guardan en el teléfono y se enviarán al regresar la red.",
            color = MaterialTheme.colorScheme.onTertiaryContainer,
            modifier = Modifier.padding(16.dp),
        )
    }
}

// Círculo con la primera letra del nombre
@Composable
private fun Inicial(nombre: String) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            nombre.firstOrNull()?.uppercase() ?: "?",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer,
        )
    }
}
