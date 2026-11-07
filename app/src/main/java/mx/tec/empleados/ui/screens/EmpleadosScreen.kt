package mx.tec.empleados.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import mx.tec.empleados.ui.viewmodel.EmpleadosUiState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmpleadosScreen(
    estado: EmpleadosUiState,
    onRecargar: () -> Unit,
    onCerrarError: () -> Unit,
    onAgregar: () -> Unit,
    onEditar: (Int) -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Empleados") },
                actions = {
                    IconButton(onClick = onRecargar, enabled = !estado.cargando) {
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
        ) {
            // Barra de carga mientras se descarga la lista (cuando ya hay algo en pantalla)
            if (estado.cargando && estado.empleados.isNotEmpty()) {
                LinearProgressIndicator(Modifier.fillMaxWidth())
            }

            // Error: con la lista vacía se muestra grande con "Reintentar"; si no, como aviso arriba
            estado.error?.let { mensaje ->
                if (estado.empleados.isNotEmpty()) {
                    AvisoError(mensaje, onCerrar = onCerrarError)
                }
            }

            when {
                estado.cargando && estado.empleados.isEmpty() -> Centro {
                    CircularProgressIndicator()
                    Spacer(Modifier.height(12.dp))
                    Text("Cargando empleados…")
                }

                estado.error != null && estado.empleados.isEmpty() -> Centro {
                    Icon(
                        Icons.Filled.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(48.dp),
                    )
                    Spacer(Modifier.height(12.dp))
                    Text(estado.error ?: "", textAlign = TextAlign.Center)
                    Spacer(Modifier.height(16.dp))
                    Button(onClick = onRecargar) { Text("Reintentar") }
                }

                estado.empleados.isEmpty() -> Centro {
                    Text("No hay empleados. Agrega uno con el botón +")
                }

                else -> LazyColumn(
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    items(estado.empleados, key = { it.id }) { empleado ->
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
                if (!empleado.activo) {
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

@Composable
private fun AvisoError(mensaje: String, onCerrar: () -> Unit) {
    Surface(
        color = MaterialTheme.colorScheme.errorContainer,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        shape = MaterialTheme.shapes.medium,
    ) {
        Row(
            modifier = Modifier.padding(start = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                mensaje,
                color = MaterialTheme.colorScheme.onErrorContainer,
                modifier = Modifier.weight(1f),
            )
            Spacer(Modifier.width(8.dp))
            IconButton(onClick = onCerrar) {
                Icon(Icons.Filled.Close, contentDescription = "Cerrar")
            }
        }
    }
}

@Composable
private fun Centro(contenido: @Composable () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        contenido()
    }
}
