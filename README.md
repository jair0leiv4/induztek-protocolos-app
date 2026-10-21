# Induztek Protocolos

Aplicación móvil Android para **digitalizar el protocolo de pruebas eléctricas en terreno** de Induztek Ingeniería SpA.

Proyecto de la asignatura **DSY1105 Desarrollo de Aplicaciones Móviles**, Evaluación Parcial 2, Duoc UC.

## Integrantes

- Jairo Leiva
- Benjamin Cisterna

## Problema que resuelve

Hoy el técnico anota en papel las mediciones de cada prueba en la instalación del cliente, y después las pasa a Excel o Word en un computador. Esto produce doble digitación, errores de traspaso y demora en la entrega del protocolo al cliente.

La app permite **registrar equipos y pruebas directamente en terreno**, con validación de datos y almacenamiento local, sin necesidad de conexión a internet.

> Todos los datos del proyecto son ficticios. No se usan protocolos reales de clientes.

## Funcionalidades implementadas

- **Navegación** entre pantallas con eventos desde un ViewModel central.
- **Login** con validación visual por campo (correo y contraseña).
- **Registro de equipos** (código, tipo e instalación del cliente) con validación y control de códigos duplicados.
- **Lista de equipos** que se actualiza automáticamente desde la base de datos.
- **Registro de pruebas** (tensión, aislación, fecha y hora) con validación numérica y de fecha. Solo se pueden registrar pruebas de equipos existentes.
- **Persistencia local** con Room: los datos siguen guardados al cerrar la app.
- **Animaciones**: transiciones entre pantallas, ícono de error animado y botón con respuesta al toque.
- **Pruebas unitarias** de las reglas de validación (20 pruebas).

## En desarrollo

- Acceso a la cámara (foto de la placa del equipo).
- Acceso a la ubicación (instalación del cliente).
- Historial de pruebas por equipo, con comparación respecto a la medición anterior.

## Tecnologías

| Área | Herramienta |
|---|---|
| Lenguaje | Kotlin |
| Interfaz | Jetpack Compose y Material Design 3 |
| Arquitectura | MVVM |
| Navegación | Navigation Compose |
| Persistencia | Room (SQLite) con KSP |
| Pruebas | JUnit |

## Estructura del proyecto

```
app/src/main/java/com/induztek/protocolos/
├── data/         Base de datos Room, DAOs y repositorio
├── model/        Entidades (Equipo, Prueba)
├── navigation/   Rutas y eventos de navegación
├── ui/
│   ├── components/   Componentes reutilizables (CampoValidado, BotonAnimado)
│   ├── screens/      Pantallas
│   └── theme/        Colores, tipografía y tema
├── util/         Validators y Fechas
└── viewmodel/    ViewModels con el estado y la lógica de cada pantalla
```

Las validaciones viven en `Validators` y en los ViewModels. Las pantallas solo muestran el estado.

## Cómo ejecutar

1. Instalar **Android Studio** (versión reciente) con un SDK de Android.
2. Clonar el repositorio:
```
   git clone https://github.com/jair0leiv4/induztek-protocolos-app.git
```
3. Abrir la carpeta del proyecto en Android Studio y esperar a que termine el **Sync** de Gradle.
4. Crear un emulador (por ejemplo Pixel con Android 15) o conectar un celular Android con depuración USB. La app requiere Android 7.0 (API 24) o superior.
5. Presionar **Run** (▶).

### Flujo de prueba

1. En el login, ingresar cualquier correo válido y una contraseña de 6 o más caracteres. Por ahora solo se valida el formato.
2. Registrar un equipo (por ejemplo `TR-1042`).
3. Registrar una prueba para ese equipo.

### Ejecutar las pruebas unitarias

En Android Studio: clic derecho sobre `ValidatorsTest` y **Run 'ValidatorsTest'**.