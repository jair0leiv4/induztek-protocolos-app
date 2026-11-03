plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp) // KSP: procesador que genera el código de Room al compilar
}

android {
    namespace = "com.induztek.protocolos"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.induztek.protocolos"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
    }
}

dependencies {

    // ---------- BASE DE ANDROID ----------
    implementation(libs.androidx.core.ktx)                      // Funciones de ayuda de Kotlin para Android
    implementation(libs.androidx.activity.compose)              // Conecta Compose con la Activity (setContent, launchers de permisos y cámara)

    // ---------- MVVM: LIFECYCLE Y VIEWMODEL ----------
    implementation(libs.androidx.lifecycle.runtime.ktx)         // Ciclo de vida de la app con corrutinas
    implementation(libs.androidx.lifecycle.runtime.compose)     // collectAsStateWithLifecycle: leer el estado del ViewModel desde la pantalla
    implementation(libs.androidx.lifecycle.viewmodel.compose)   // viewModel(): crear y recibir ViewModels dentro de Compose

    // ---------- INTERFAZ: JETPACK COMPOSE + MATERIAL 3 ----------
    implementation(platform(libs.androidx.compose.bom))         // BOM: fija las versiones de todas las piezas de Compose
    implementation(libs.androidx.compose.ui)                    // Base de Compose (Modifier, layouts)
    implementation(libs.androidx.compose.ui.graphics)           // Colores, formas y dibujo
    implementation(libs.androidx.compose.ui.tooling.preview)    // Vistas previas (@Preview) en Android Studio
    implementation(libs.androidx.compose.material3)             // Material Design 3: botones, campos de texto, tarjetas, menú
    implementation(libs.androidx.compose.material3.windowsize)  // WindowSizeClass: saber si la pantalla es Compact, Medium o Expanded
    implementation(libs.androidx.compose.material.icons.extended) // Íconos de Material (cámara, ubicación, editar, eliminar, etc.)

    // ---------- 1 y 7. NAVEGACIÓN ----------
    implementation(libs.androidx.navigation.compose)            // NavHost y rutas para moverse entre pantallas

    // ---------- 3. ANIMACIONES Y DATASTORE ----------
    implementation(libs.androidx.compose.animation)             // AnimatedVisibility, transiciones y animaciones de estado
    implementation(libs.androidx.datastore.preferences)         // DataStore: guarda la sesión (correo y rol) en el celular

    // ---------- 4. RECURSOS NATIVOS ----------
    implementation(libs.androidx.camera.core)                   // CameraX: base de la cámara
    implementation(libs.androidx.camera.camera2)                // CameraX: usa el motor Camera2 del celular
    implementation(libs.androidx.camera.lifecycle)              // CameraX: enlaza la cámara con el ciclo de vida de la pantalla
    implementation(libs.androidx.camera.view)                   // CameraX: PreviewView, la vista previa en pantalla
    implementation(libs.coil.compose)                           // Coil: carga y muestra las fotos guardadas

    // ---------- 5. ROOM + SQLITE (CRUD) ----------
    implementation(libs.androidx.room.runtime)                  // Room: base de datos local sobre SQLite
    implementation(libs.androidx.room.ktx)                      // Room con corrutinas y Flow
    ksp(libs.androidx.room.compiler)                            // Genera el código de los DAO y la base de datos

    // ---------- 6. CORRUTINAS ----------
    implementation(libs.kotlinx.coroutines.android)             // Corrutinas: tareas en segundo plano sin congelar la app

    // ---------- PRUEBAS ----------
    testImplementation(libs.junit)                              // Pruebas unitarias (ValidatorsTest)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    debugImplementation(libs.androidx.compose.ui.tooling)       // Herramientas de diseño (solo en modo debug)
}