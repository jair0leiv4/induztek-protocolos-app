# Induztek Protocolos

Aplicación móvil Android para **digitalizar el protocolo de pruebas eléctricas en terreno** de Induztek Ingeniería SpA.

Proyecto de la asignatura **DSY1105 Desarrollo de Aplicaciones Móviles** (Duoc UC), Evaluación Parcial 2.

## Integrantes

- Jairo Leiva 
- Benjamin Cisternas

## Problema que resuelve

Hoy el técnico anota en papel las mediciones de cada prueba en la instalación del cliente y recién puede armar el protocolo en Excel o Word cuando vuelve a un computador. Esto produce doble digitación, errores de traspaso, demora en la entrega del informe y poca facilidad para comparar con pruebas anteriores.

La app permite **registrar equipos y pruebas directamente en terreno**, con validación de datos, foto de la placa del equipo, ubicación y almacenamiento local, sin necesidad de conexión a internet. Además muestra un **historial por equipo** que compara cada medición con la anterior.

> Todos los datos del proyecto son ficticios. No se usan protocolos reales de clientes.

## Cómo funciona (flujo principal)

1. **Ingresa** con correo y contraseña. Según el usuario entra como **Administrador** o como **Técnico**.
2. **Registra el equipo** (una sola vez): código (por ejemplo `TR-1042`), tipo e instalación del cliente.
3. **Hace una prueba**: elige el equipo, anota tensión y aislación, saca una foto de la placa y captura la ubicación.
4. **Se guarda en el celular** con Room. Cada prueba queda asociada a su equipo.
5. **Consulta el historial**: cada medición se compara con la anterior del mismo equipo (variación porcentual de la aislación).

Un equipo tiene muchas pruebas. No se puede registrar una prueba de un equipo que no existe: el formulario solo permite elegir equipos ya registrados.

## Roles

| Capacidad | Administrador | Técnico |
|---|:---:|:---:|
| Ver equipos e historial de pruebas | Sí | Sí |
| Registrar equipos y pruebas (con foto y ubicación) | Sí | Sí |
| Editar y eliminar equipos y pruebas | Sí | No |
| Gestionar técnicos y usuarios | Sí | No |
| Gestionar tipos de equipo | Sí | No |

Las reglas se aplican en el ViewModel y la interfaz solo oculta los botones.

### Cuentas de demostración

| Rol | Correo | Contraseña |
|---|---|---|
| Administrador | `admin@induztek.cl` | `Admin123` |
| Técnico | `tecnico@induztek.cl` | `Tecnico123` |

Se crean automáticamente la primera vez que se abre la app.

## Funcionalidades implementadas

- **Navegación** con Navigation Compose, rutas con argumentos opcionales (el mismo formulario crea y edita) y menú lateral que cambia según el rol.
- **Login** contra Room con dos roles. Las contraseñas se guardan como huella SHA-256 y la **sesión se recuerda con DataStore**.
- **Formularios validados** con ícono, borde y mensaje por campo, para login, equipos, pruebas, usuarios y tipos de equipo.
- **CRUD completo con Room** para equipos, pruebas, usuarios y tipos de equipo (crear, leer, actualizar y eliminar con confirmación).
- **Cámara con CameraX**: vista previa, cambio de lente, permiso en tiempo de uso y foto guardada en almacenamiento privado de la app.
- **Ubicación (GPS)** con permiso en tiempo de uso. Se guarda latitud y longitud en cada prueba.
- **Historial con comparación**: variación porcentual de la aislación respecto a la medición anterior del mismo equipo.
- **Animaciones**: transiciones entre pantallas, ícono de error, botón que responde al toque y tarjetas animadas en las listas.
- **Diseño para pantalla Compact** (celular) calculado con `WindowSizeClass`; en pantallas más grandes el contenido se centra.
- **Pruebas unitarias** de las reglas de validación y de la huella de contraseñas (28 pruebas).

## Requisitos de la pauta y dónde están

| Requisito | Dónde está |
|---|---|
| Interfaz organizada con navegación clara | `ui/screens/`, `ui/components/Pantallas.kt` (menú lateral), `MainActivity.kt` (NavHost) |
| Formularios validados con íconos y mensajes | `ui/components/CampoValidado.kt` y los formularios en `ui/screens/` |
| Validaciones manejadas desde la lógica | `util/Validators.kt` y los ViewModels en `viewmodel/` |
| Animaciones funcionales | `ui/utils/Animations.kt`, `ui/components/BotonAnimado.kt` |
| Estructura modular y persistencia local | MVVM por capas y Room en `model/`, `data/` |
| Repositorio en GitHub y planificación | Este repositorio y su tablero de Projects |
| Al menos dos recursos nativos | Cámara: `ui/components/CameraCapture.kt`. Ubicación: `util/Ubicacion.kt` y `ui/components/SelectorUbicacion.kt` |

## Arquitectura

Se usa **MVVM**. Se explica en este orden: model, viewmodel y view.

| Capa | Carpetas | Responsabilidad |
|---|---|---|
| **Model** | `model/`, `data/`, `util/` | Entidades, DAO, base de datos Room, repositorio, DataStore y reglas de validación |
| **ViewModel** | `viewmodel/` | Estado de cada pantalla, validación, decisiones y llamadas al repositorio |
| **View** | `ui/`, `MainActivity.kt` | Pantallas y componentes de Jetpack Compose. Solo muestran el estado y avisan los toques |

```
app/src/main/java/com/induztek/protocolos/
├── model/        Entidades Room (Usuario, TipoEquipo, Equipo, Prueba) y roles
├── data/         DAO, AppDatabase, ProtocolosRepository y SesionManager (DataStore)
├── util/         Validators, Seguridad, Fechas, Formato y Ubicacion
├── navigation/   Rutas (Screen) y eventos de navegación
├── viewmodel/    MainViewModel, SesionViewModel, LoginViewModel y los ViewModels de cada CRUD
└── ui/
    ├── components/   CampoValidado, BotonAnimado, SelectorDropdown, CameraCapture, SelectorUbicacion, Pantallas
    ├── screens/      Login, Equipos, Pruebas, Usuarios, Tipos y sus formularios
    ├── theme/        Colores, tipografía y tema
    └── utils/        Animations.kt
```

## Tecnologías

| Área | Herramienta |
|---|---|
| Lenguaje | Kotlin |
| Interfaz | Jetpack Compose y Material Design 3 |
| Arquitectura | MVVM |
| Navegación | Navigation Compose |
| Base de datos local | Room (SQLite) con KSP |
| Preferencias y sesión | DataStore |
| Cámara | CameraX y Coil (para mostrar las fotos) |
| Tareas asíncronas | Corrutinas y Flow |
| Tamaño de pantalla | `material3-window-size-class` |
| Pruebas | JUnit |

Cada dependencia está comentada en `app/build.gradle.kts` con su función.

## Cómo ejecutar

1. Instalar **Android Studio** (versión reciente) con un SDK de Android.
2. Clonar el repositorio:
   ```
   git clone https://github.com/jair0leiv4/induztek-protocolos-app.git
   ```
3. Abrir la carpeta en Android Studio y esperar a que termine el **Sync** de Gradle.
4. Crear un emulador (por ejemplo, un Pixel con Android 15 / API 35) o conectar un celular Android con depuración USB. La app requiere Android 7.0 (API 24) o superior.
5. Presionar **Run** (▶).

### Flujo de prueba sugerido

1. Ingresar como **técnico** con `tecnico@induztek.cl`.
2. Registrar un equipo (por ejemplo, `TR-1042`).
3. Ir a **Historial de pruebas** y crear una **Nueva prueba** para ese equipo. Tomar la foto y capturar la ubicación.
4. Registrar una segunda prueba del mismo equipo con otra aislación y revisar la variación en el historial.
5. Cerrar sesión e ingresar como **administrador** para editar, eliminar y gestionar técnicos y tipos de equipo.
6. Cerrar y volver a abrir la app: la sesión y los datos se mantienen.

En el emulador, la ubicación se simula desde los tres puntos (**Extended controls > Location**) y la cámara muestra una escena virtual.

### Ejecutar las pruebas unitarias

En Android Studio: clic derecho sobre `ValidatorsTest` y **Run 'ValidatorsTest'**.

## Alcance y próximos pasos

Este entregable es un MVP con persistencia local. Queda para la evaluación final:

- Backend con microservicios Spring Boot e integración con Retrofit.
- Sincronización de las pruebas cuando vuelva la conexión.
- Generación del protocolo en PDF para compartirlo con el cliente.
- Un esquema de contraseñas más robusto para producción (hoy SHA-256 sin sal).
