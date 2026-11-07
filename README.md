# Empleados · Room starter · TC2007B

App Android (Kotlin + Jetpack Compose) con un **CRUD de empleados** contra la API [api-empleados-crud](https://github.com/jorflores/api-empleados-crud).

Es el **punto de partida de la clase de Room**. La app ya lista, crea, edita y borra empleados con Retrofit, pero **todo vive en la API**: sin internet la lista no carga. En clase agregaremos **Room** para que funcione sin conexión.

## Qué hace

| Pantalla | Qué tiene |
|---|---|
| **Empleados** (lista) | Tarjetas con nombre, puesto y departamento. Botón ↻ para recargar y botón **+** para agregar. Si falla la conexión: mensaje y **Reintentar**. |
| **Nuevo / Editar empleado** | Nombre y correo (obligatorios), puesto, departamento y switch **Activo**. En modo editar, el ícono 🗑 borra con confirmación. |

## Antes de correrla: prende la API

1. Clona [api-empleados-crud](https://github.com/jorflores/api-empleados-crud) y en su carpeta corre:
   ```
   docker compose up --build
   ```
2. Revisa que http://localhost:3000/empleados regrese 5 empleados en el navegador.
3. Abre este proyecto en Android Studio (**File > Open**) y córrelo en el **emulador**.

La URL ya apunta a `http://10.0.2.2:3000/`, que es tu computadora vista desde el emulador. Para cambiarla (Render, túnel HTTPS o un teléfono físico), edita `BASE_URL` en `data/remote/RetrofitClient.kt`.

> **Prueba el problema:** con la app abierta, apaga la API (`docker compose stop api`) o pon el emulador en modo avión y toca ↻. La lista no carga. Eso es lo que resolveremos con Room.

## Arquitectura

```
Pantalla  →  EmpleadosViewModel  →  EmpleadoRepository  →  EmpleadosApi (Retrofit)  →  API
```

```
mx.tec.empleados
├── MainActivity.kt
├── navigation
│   ├── Rutas.kt                    empleados  y  empleado/{id}  (id 0 = nuevo)
│   └── AppNavigation.kt            NavHost + un solo ViewModel compartido
├── data
│   ├── remote
│   │   ├── Empleado.kt             El empleado (mismo JSON que la API); lo usa toda la app
│   │   ├── EmpleadosApi.kt         GET, POST, PUT, DELETE /empleados
│   │   └── RetrofitClient.kt       URL base, JSON y Logcat
│   └── repository
│       └── EmpleadoRepository.kt   Único lugar que sabe de dónde vienen los datos
└── ui
    ├── viewmodel
    │   └── EmpleadosViewModel.kt   Estado (cargando, guardando, empleados, error) y acciones
    ├── screens
    │   ├── EmpleadosScreen.kt      Lista
    │   └── EmpleadoFormScreen.kt   Crear / editar / borrar
    └── theme                       Colores, tema y tipografía
```

## Lo que agregaremos en clase (Room)

1. Dependencias de Room + plugin KSP.
2. `data/local/EmpleadoEntity.kt`, `EmpleadoDao.kt` y `AppDatabase.kt`.
3. `EmpleadoRepository`: guardar en Room lo que llega de la API y leer siempre desde Room.
4. `EmpleadosViewModel` pasa a `AndroidViewModel` para poder abrir la base de datos.

Las pantallas no deberían cambiar: esa es la ventaja de tener el Repository.

## Versiones

Las mismas de AppBase: Kotlin 2.4.10, AGP 9.5.0, Navigation 2.9.8, Lifecycle 2.9.4, Retrofit 3.0.0 y kotlinx.serialization 1.9.0. Todas están en `gradle/libs.versions.toml`.
