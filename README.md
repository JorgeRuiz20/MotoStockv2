# MotoStock Taller

App Android (Kotlin, Clean Architecture) para gestión de inventario de repuestos de motos,
historial de servicio por placa, y agenda de citas. Usa **Room** (base local) + **Firebase
Firestore** (sincronización en la nube) + **Retrofit** (integración opcional a una API de
decodificación de placas).

## 1. Qué hacer antes de abrir el proyecto

1. Descomprime este `.zip` en una carpeta.
2. Reemplaza el archivo `app/google-services.json` (aquí va uno de EJEMPLO, no funcional)
   por el que descargaste de tu proyecto de Firebase.
3. Abre Android Studio → `File > Open` → selecciona la carpeta `MotoStock`.
4. Espera a que sincronice Gradle (primera vez tarda porque descarga dependencias).

## 2. Configuración de Firebase (ya tienes cuenta creada)

Como ya tienes el proyecto en Firebase:

1. Ve a la [consola de Firebase](https://console.firebase.google.com) → tu proyecto.
2. En **Compilación > Firestore Database**, crea la base de datos si aún no existe
   (modo producción o prueba, como prefieras).
3. En **Reglas de Firestore**, para desarrollo puedes usar temporalmente:
   ```
   rules_version = '2';
   service cloud.firestore {
     match /databases/{database}/documents {
       match /{document=**} {
         allow read, write: if true; // SOLO para pruebas, no producción
       }
     }
   }
   ```
4. Verifica que el `package_name` dentro de tu `google-services.json` sea
   exactamente `com.taller.motostock` (o cambia el `applicationId` en
   `app/build.gradle` para que coincida con el tuyo).
5. Coloca el archivo real en `app/google-services.json` (reemplazando el de ejemplo).

La app ya sincroniza automáticamente con 4 colecciones de Firestore:
`repuestos`, `historial`, `motos`, `citas` (ver `di/FirebaseModule.kt` y los `*RepositoryImpl.kt`).

## 3. Sobre la API de placas (Retrofit)

Ya tienes tu propia API modelo (según indicaste). El punto de conexión está en:

- `data/remote/api/PlacaApiService.kt` — interfaz Retrofit
- `data/remote/dto/VehicleApiResponse.kt` — el modelo de respuesta esperado
- `di/NetworkModule.kt` — la URL base (`BASE_URL`)
- `app/build.gradle` (última línea) — el `CARSXE_API_KEY` de ejemplo

**Para conectar tu propia API**, solo necesitas:
1. Cambiar `BASE_URL` en `NetworkModule.kt` por la tuya.
2. Ajustar los campos de `VehicleApiResponse.kt` para que coincidan con el JSON
   que devuelve tu API.
3. Reemplazar la key en `app/build.gradle`:
   ```gradle
   android.defaultConfig.buildConfigField("String", "CARSXE_API_KEY", '"TU_KEY_REAL"')
   ```

Si tu API falla o no encuentra la placa, la app no se rompe: crea la moto solo
con la placa para que el usuario complete los datos manualmente (ver
`HistorialRepositoryImpl.buscarPlacaEnApi`).

## 4. Qué se corrigió/agregó (proyecto original venía incompleto)

Estos archivos NO existían en el zip original y son obligatorios para compilar:

- `gradlew`, `gradlew.bat`, `gradle/wrapper/gradle-wrapper.jar`, `gradle-wrapper.properties`
- `settings.gradle`, `gradle.properties`
- `app/src/main/AndroidManifest.xml`
- `app/proguard-rules.pro`
- Todos los layouts de `fragment_home`, `fragment_historial`, `fragment_citas`,
  `fragment_form_repuesto`, `fragment_form_servicio`, `item_repuesto`, `item_cita`,
  `item_servicio`
- `values/colors.xml`, íconos del launcher, íconos del menú inferior, `menu/bottom_nav_menu.xml`
- `activity_main.xml` (existía el archivo pero estaba vacío)

También se subió el **Android Gradle Plugin de 8.2.2 a 8.4.2** y se fijó
**Gradle 8.6** en el wrapper — la combinación que traías (AGP 8.2.2 sin wrapper
propio, probablemente usando un Gradle instalado en tu sistema con otra versión)
es la causa más probable del error
`Unable to find method ... Configuration.fileCollection(...)`.

## 5. Estructura (Clean Architecture)

```
presentation/  → Fragments, Activities, ViewModels, Adapters (UI)
domain/        → Models, Repository interfaces, UseCases (reglas de negocio puras)
data/          → Room (local), Firestore + Retrofit (remoto), implementación de repos
di/            → Módulos de Hilt (inyección de dependencias)
```

## 7. Solucionar "API key not valid" al crear cuenta

Ese error casi siempre es uno de estos 3 casos:

1. **No reemplazaste `google-services.json`** — sigue el de ejemplo (con la key `REEMPLAZAR_CON_TU_API_KEY`). Descarga el real desde Firebase → ⚙️ Configuración del proyecto → tus apps → `google-services.json` → reemplázalo completo (no lo edites a mano).
2. **No activaste el proveedor Email/Password** en Authentication → Sign-in method.
3. **La Identity Toolkit API está deshabilitada** en Google Cloud Console para ese proyecto (raro, pero pasa si se desactivó una API "para ahorrar costos"). Ve a [Google Cloud Console](https://console.cloud.google.com) → tu proyecto → APIs & Services → busca "Identity Toolkit API" → actívala si aparece deshabilitada.

Después de cualquiera de estos cambios, haz **Clean Project + Rebuild** en Android Studio.

## 8. Configurar Google Sign-In (opcional, ya viene integrado en el código)

1. Firebase Console → Authentication → Sign-in method → activa **Google**.
2. **Registra la huella SHA-1 de tu certificado** (esto es obligatorio para Google Sign-In, sin esto verás siempre el error "código 10"):
   - En Android Studio, abre la pestaña **Gradle** (lateral derecho) → `MotoStock > app > Tasks > android > signingReport` (doble clic) — o desde terminal:
     ```
     ./gradlew signingReport
     ```
   - Copia el valor **SHA-1** que aparece bajo `Variant: debug`.
   - Ve a Firebase → ⚙️ Configuración del proyecto → tu app Android → **Añadir huella digital** → pega el SHA-1.
3. Vuelve a descargar `google-services.json` (ahora sí incluirá el `oauth_client` con tu SHA-1 vinculado) y reemplaza el archivo en `app/google-services.json`.
4. Clean + Rebuild. Listo — el botón "Continuar con Google" ya debería funcionar.

Sin estos pasos, el botón de Google mostrará el error "código 10" (SHA-1 no registrado), pero **login con correo/contraseña funciona igual sin este paso**.

⚠️ Cuando generes el APK final para producción (firmado con tu keystore de release, no el de debug), tendrás que repetir el paso 2-3 con el SHA-1 **de ese keystore de release** también.

## 9. Estructura del login

- `LoginFragment` / `fragment_login.xml` — pantalla de ingreso (correo/contraseña + Google).
- `RegisterFragment` / `fragment_register.xml` — pantalla de registro, **separada** del login.
- El botón "Cerrar sesión" está en Inicio.

## 10. Próximos pasos sugeridos (no incluidos aún, para no gastar todo de una vez)

- Autenticación de usuarios (Firebase Auth) para que cada mecánico tenga su cuenta.
- Cargar los datos actuales del repuesto al editar (`FormRepuestoFragment` ahora
  mismo no precarga los campos al presionar "editar").
- Registro fotográfico del vehículo al ingreso (Firebase Storage).
- Reportes/exportación de historial en PDF.

Si quieres que avancemos con alguno de estos, dime cuál y seguimos.
