package mx.tec.empleados.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import mx.tec.empleados.data.model.Empleado

// Sirve para CREAR (empleado == null) y para EDITAR (empleado != null).
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmpleadoFormScreen(
    empleado: Empleado?,
    onGuardar: (Empleado) -> Unit,
    onEliminar: () -> Unit,
    onRegresar: () -> Unit,
) {
    val esNuevo = empleado == null

    // Lo que el usuario escribe es estado de la pantalla; empieza con los datos del empleado.
    var nombre by remember { mutableStateOf(empleado?.nombre ?: "") }
    var correo by remember { mutableStateOf(empleado?.correo ?: "") }
    var puesto by remember { mutableStateOf(empleado?.puesto ?: "") }
    var departamento by remember { mutableStateOf(empleado?.departamento ?: "") }
    var activo by remember { mutableStateOf(empleado?.activo ?: true) }
    var confirmarBorrado by remember { mutableStateOf(false) }

    val datosCompletos = nombre.isNotBlank() && correo.isNotBlank()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (esNuevo) "Nuevo empleado" else "Editar empleado") },
                navigationIcon = {
                    IconButton(onClick = onRegresar) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar")
                    }
                },
                actions = {
                    if (!esNuevo) {
                        IconButton(onClick = { confirmarBorrado = true }) {
                            Icon(Icons.Filled.Delete, contentDescription = "Eliminar")
                        }
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            OutlinedTextField(
                value = nombre,
                onValueChange = { nombre = it },
                label = { Text("Nombre *") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = correo,
                onValueChange = { correo = it },
                label = { Text("Correo *") },
                singleLine = true,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = puesto,
                onValueChange = { puesto = it },
                label = { Text("Puesto") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            OutlinedTextField(
                value = departamento,
                onValueChange = { departamento = it },
                label = { Text("Departamento") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth(),
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text("Activo", modifier = Modifier.weight(1f))
                Switch(checked = activo, onCheckedChange = { activo = it })
            }

            Spacer(Modifier.height(8.dp))
            Button(
                onClick = {
                    onGuardar(
                        Empleado(
                            id = empleado?.id ?: 0,
                            nombre = nombre.trim(),
                            correo = correo.trim(),
                            puesto = puesto.trim(),
                            departamento = departamento.trim(),
                            activo = activo,
                        )
                    )
                },
                enabled = datosCompletos,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (esNuevo) "Crear" else "Guardar cambios")
            }
            Text(
                "* Obligatorio",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }

    if (confirmarBorrado) {
        AlertDialog(
            onDismissRequest = { confirmarBorrado = false },
            title = { Text("¿Eliminar empleado?") },
            text = { Text("Se eliminará a ${empleado?.nombre}. No se puede deshacer.") },
            confirmButton = {
                TextButton(onClick = {
                    confirmarBorrado = false
                    onEliminar()
                }) { Text("Eliminar", color = MaterialTheme.colorScheme.error) }
            },
            dismissButton = {
                TextButton(onClick = { confirmarBorrado = false }) { Text("Cancelar") }
            },
        )
    }
}
