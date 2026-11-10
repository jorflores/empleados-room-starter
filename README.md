# Empleados · Room starter · TC2007B

> **Estás en `room-solucion`:** la app terminada con Room. El punto de partida está en `main`.

App Android (Kotlin + Jetpack Compose) para practicar **Room**: un CRUD de empleados guardado en el teléfono.

## Ramas

| Rama | Qué tiene |
|---|---|
| **`main`** | Punto de partida de la clase: **solo la UI y la navegación**. Los botones no guardan nada (`TODO (Room)`). |
| **`room-solucion`** | La app terminada con **Room**: crear, editar y borrar empleados que sobreviven al cerrar la app. |
| `room-api-solucion` | Room + [api-empleados-crud](https://github.com/jorflores/api-empleados-crud), versión sencilla: sin conexión se pueden crear empleados y se envían al sincronizar. Parte de `room-solucion`. |
| `room-api-completa` | Versión avanzada: crear, editar y borrar sin conexión, con detector de red automático. |

## Qué hace (rama `main`)

| Pantalla | Qué tiene |
|---|---|
| **Empleados** (lista) | Tarjetas con nombre, puesto y departamento. Botón **+** para agregar; tocar una tarjeta abre la edición. |
| **Nuevo / Editar empleado** | Nombre y correo (obligatorios), puesto, departamento y switch **Activo**. En modo editar, el ícono 🗑 borra con confirmación. |

> **Prueba el problema:** agrega un empleado y toca **Crear**. Regresas a la lista… y sigue vacía. Las pantallas y la navegación funcionan, pero `EmpleadosViewModel` no guarda nada: eso es lo que haremos con Room.

## Estructura

```
mx.tec.empleados
├── MainActivity.kt
├── navigation
│   ├── Rutas.kt                    empleados  y  empleado/{id}  (id 0 = nuevo)
│   └── AppNavigation.kt            NavHost + un solo ViewModel compartido
├── data
│   └── model
│       └── Empleado.kt             El empleado (en clase se vuelve una tabla de Room)
└── ui
    ├── viewmodel
    │   └── EmpleadosViewModel.kt   Estado de la lista + guardar() y eliminar() con TODO
    ├── screens
    │   ├── EmpleadosScreen.kt      Lista
    │   └── EmpleadoFormScreen.kt   Crear / editar / borrar
    └── theme                       Colores, tema y tipografía
```

## Lo que agregaremos en clase

1. Dependencias de Room y el plugin KSP.
2. `Empleado` se vuelve una tabla con `@Entity`.
3. `EmpleadoDao`: las consultas.
4. `AppDatabase`: la base de datos.
5. `EmpleadoRepository`: el que habla con el DAO.
6. `EmpleadosViewModel`: lee la lista de Room y guarda o borra con el Repository.

Las pantallas y la navegación no cambian.

## Versiones

Kotlin 2.4.10, AGP 9.5.0, Navigation 2.9.8 y Lifecycle 2.9.4. Todas están en `gradle/libs.versions.toml`.
