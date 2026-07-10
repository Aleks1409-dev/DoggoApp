# DoggoApp — Frontend (Android)

App nativa Android para DoggoApp: permite a clientes buscar cuidadores de mascotas, ver servicios, agendar citas y enviar mensajes.

## Stack

- Kotlin 2.2.10 + Jetpack Compose
- Retrofit + Gson (networking)
- Navigation Compose
- AGP 9.2.1, JDK 21, `minSdk` 24, `targetSdk` 37

## Requisitos

- Android Studio (recomendado), o JDK 21 + Gradle si se prefiere línea de comandos.

## Compilar y ejecutar

Todos los comandos se ejecutan desde `frontend/`. `gradlew` no tiene permisos de ejecución en este checkout — se invoca como `bash gradlew ...` o se le da permiso con `chmod +x gradlew` primero.

```bash
# Compilar el APK debug
bash gradlew assembleDebug

# Tests unitarios
bash gradlew test

# Tests instrumentados (requiere emulador o dispositivo conectado)
bash gradlew connectedAndroidTest

# Lint
bash gradlew lint
```

## Arquitectura

- **Sin framework de inyección de dependencias** (no Hilt/Dagger/Koin): `di/AppContainer.kt` construye a mano la instancia de Retrofit y los repositorios. `MainActivity` crea un único `AppContainer` y lo pasa hacia abajo a través de `AppNavigation` a cada pantalla.
- **Networking**: Retrofit + Gson, interfaz `ApiService` en `data/remote/`, URL base hardcodeada en `AppContainer.kt`.
- **Pantallas**: `presentation/screens/<nombre>/`. Solo `inicio` tiene el set completo `Screen` + `ViewModel` + `UiState` + `ViewModelFactory`; `agenda`, `mensajes`, `perfil` y `bienvenida` son composables estáticos todavía sin lógica.
- **Navegación**: `presentation/navigation/AppNavigation.kt` (`NavHost` de Navigation-Compose) y `NavRutas.kt` para las rutas y títulos.
- **Eventos de UI transversales** (snackbars, etc.): `presentation/events/EventBus.kt`, un `MutableSharedFlow<UiEvent>` singleton.

## Estado conocido

- `ApiService.getCuidadores()` apunta a un endpoint `CuidadoresApi` que el backend actual no implementa — las rutas reales del backend son las documentadas en [`../backend/README.md`](../backend/README.md). `CuidadorRepository.obtenerCuidadores()` es por ahora un stub que registra la respuesta cruda y devuelve una lista vacía.
- Falta apuntar la app al API Gateway desplegado (ver `backend/docs/aws-apigateway-cli.md`) una vez esté publicado, y adaptar `ApiService` a las rutas reales (`/login`, `/register`, `/sitters`, `/services`, `/messages`, `/schedule/{sitterId}`).
