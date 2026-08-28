# 🔧 Guía de Integración - Sistema de Roles

## Paso 1: Estructura de Carpetas

```
app/src/main/java/com/taller/motostock/
├── presentation/
│   ├── ui/
│   │   └── cliente/  ← NUEVA CARPETA
│   │       ├── MisServiciosFragment.kt
│   │       └── AgendarCitaFragment.kt
│   ├── adapter/
│   │   └── MisServiciosAdapter.kt  ← NUEVO
│   └── viewmodel/
│       └── ClienteViewModel.kt  ← NUEVO
└── domain/
    └── model/
        └── UserRole.kt  ← NUEVO
```

## Paso 2: Agregar Dependencias (si no están)

En `app/build.gradle`:

```gradle
dependencies {
    // Ya debería estar:
    implementation 'androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.1'
    implementation 'com.google.dagger:hilt-android:2.46'
    
    // Confirmar que tiene View Binding habilitado
}

android {
    // ... otras configuraciones
    buildFeatures {
        viewBinding true  // IMPORTANTE
    }
}
```

## Paso 3: Crear Drawables Necesarios

Crear `res/drawable/card_background.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android">
    <solid android:color="@android:color/white" />
    <corners android:radius="8dp" />
    <stroke 
        android:width="1dp" 
        android:color="#E0E0E0" />
</shape>
```

Crear `res/drawable/et_border.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<shape xmlns:android="http://schemas.android.com/apk/res/android">
    <solid android:color="@android:color/white" />
    <corners android:radius="4dp" />
    <stroke 
        android:width="1dp" 
        android:color="#CCCCCC" />
    <padding 
        android:left="8dp" 
        android:right="8dp" 
        android:top="8dp" 
        android:bottom="8dp" />
</shape>
```

## Paso 4: Actualizar Navigation

En `res/navigation/nav_graph.xml`, agregar (dentro del navigation):

```xml
<!-- Destinos para Cliente -->
<fragment
    android:id="@+id/misServiciosFragment"
    android:name="com.taller.motostock.presentation.ui.cliente.MisServiciosFragment"
    android:label="Mis Servicios"
    tools:layout="@layout/fragment_mis_servicios" />

<fragment
    android:id="@+id/agendarCitaFragment"
    android:name="com.taller.motostock.presentation.ui.cliente.AgendarCitaFragment"
    android:label="Agendar Cita"
    tools:layout="@layout/fragment_agendar_cita" />
```

## Paso 5: Crear UserRepository

Crear `data/repository/UserRepository.kt`:

```kotlin
package com.taller.motostock.data.repository

import com.taller.motostock.domain.model.Usuario
import com.taller.motostock.domain.model.UserRole

interface UserRepository {
    suspend fun guardarUsuario(usuario: Usuario)
    suspend fun obtenerUsuario(uid: String): Usuario?
    suspend fun obtenerRol(uid: String): UserRole?
}
```

Crear `data/repository/UserRepositoryImpl.kt`:

```kotlin
package com.taller.motostock.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.taller.motostock.domain.model.Usuario
import com.taller.motostock.domain.model.UserRole
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : UserRepository {

    override suspend fun guardarUsuario(usuario: Usuario) {
        firestore.collection("usuarios")
            .document(usuario.uid)
            .set(usuario)
            .await()
    }

    override suspend fun obtenerUsuario(uid: String): Usuario? {
        return try {
            firestore.collection("usuarios")
                .document(uid)
                .get()
                .await()
                .toObject(Usuario::class.java)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun obtenerRol(uid: String): UserRole? {
        return obtenerUsuario(uid)?.role
    }
}
```

## Paso 6: Agregar a RepositoryModule

En `di/RepositoryModule.kt`, agregar:

```kotlin
@Provides
@Singleton
fun provideUserRepository(firestore: FirebaseFirestore): UserRepository {
    return UserRepositoryImpl(firestore)
}
```

## Paso 7: Actualizar LoginViewModel

En `presentation/viewmodel/LoginViewModel.kt`, en la función `registrar()`:

```kotlin
suspend fun registrar(email: String, password: String, nombre: String, telefono: String) {
    // ... código existente ...
    
    // Después de crear usuario en Auth, guardar en Firestore:
    val usuario = Usuario(
        uid = authResult.user?.uid ?: "",
        email = email,
        nombre = nombre,
        telefono = telefono,
        role = UserRole.CLIENTE,  // Por defecto cliente
        creadoEn = System.currentTimeMillis()
    )
    userRepository.guardarUsuario(usuario)
}
```

## Paso 8: Actualizar MainActivity

En `presentation/ui/MainActivity.kt`, detectar rol y mostrar menú:

```kotlin
private fun setupNavegacionPorRol() {
    // Obtener rol del usuario actual
    viewLifecycleOwner.lifecycleScope.launch {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@launch
        val rol = userRepository.obtenerRol(uid)
        
        when (rol) {
            UserRole.TRABAJADOR -> mostrarMenuTrabajador()
            UserRole.CLIENTE -> mostrarMenuCliente()
            else -> {}
        }
    }
}

private fun mostrarMenuCliente() {
    // Mostrar solo fragments para cliente
    // Ocultar InventarioFragment, EnTallerFragment, etc.
}

private fun mostrarMenuTrabajador() {
    // Mostrar todos los fragments
}
```

## Paso 9: Configurar Firestore Security Rules

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {
    
    // Colección de usuarios
    match /usuarios/{uid} {
      allow read: if request.auth.uid == uid;
      allow write: if request.auth.uid == uid;
    }
    
    // Colección de citas
    match /citas/{document=**} {
      // Trabajadores pueden leer todas
      allow read: if get(/databases/$(database)/documents/usuarios/$(request.auth.uid)).data.role == 'trabajador';
      allow read: if get(/databases/$(database)/documents/usuarios/$(request.auth.uid)).data.role == 'cliente' 
                  && resource.data.placa == request.query.placa;
      
      // Solo trabajadores pueden escribir
      allow write: if get(/databases/$(database)/documents/usuarios/$(request.auth.uid)).data.role == 'trabajador';
    }
  }
}
```

## Paso 10: Testing

### Flujo Cliente
```
1. Registrarse con email/password
2. Sistema asigna role = CLIENTE
3. Pantalla muestra:
   - Agendar Cita
   - Mis Servicios
4. Al agendar, guardar en Firestore
5. Al buscar, mostrar solo sus citas
```

### Flujo Trabajador
```
1. Ser registrado como trabajador (en Firestore)
2. Pantalla muestra:
   - Inventario
   - En Taller
   - Historial
   - Registro Vehicular
3. Puede ver todas las citas
4. Puede marcar como finalizado
```

## Checklist de Integración

- [ ] Crear carpeta `presentation/ui/cliente/`
- [ ] Copiar archivos .kt nuevos
- [ ] Copiar archivos .xml nuevos
- [ ] Crear drawables (card_background, et_border)
- [ ] Crear UserRepository (interfaz + impl)
- [ ] Agregar a RepositoryModule
- [ ] Actualizar LoginViewModel
- [ ] Actualizar MainActivity para mostrar UI según rol
- [ ] Actualizar nav_graph.xml
- [ ] Configurar Firestore Security Rules
- [ ] Probar flujo de cliente
- [ ] Probar flujo de trabajador
- [ ] Validar que cliente NO vea datos de otros

---

**Tiempo estimado de integración**: 2-3 horas  
**Dificultad**: Media  
**Requiere**: Firebase Firestore + Android conocimientos intermedios
