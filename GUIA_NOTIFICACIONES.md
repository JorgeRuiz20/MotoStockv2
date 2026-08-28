# Guía de Notificaciones Push — MotoStock

## ¿Cómo funciona el sistema?

El sistema de notificaciones tiene **dos capas**:

| Capa | ¿Cuándo funciona? | Descripción |
|------|-------------------|-------------|
| **Local** (in-app) | App en primer plano | `NotificationHelper` muestra la notificación directamente en el dispositivo usando `NotificationManagerCompat` |
| **FCM remoto** (background) | App en segundo plano o cerrada | La app escribe en Firestore `/fcm_queue`, una Cloud Function la procesa y llama a la FCM API |

---

## Configuración en Firebase Console (obligatoria para background)

### 1. Habilitar Firebase Cloud Messaging

En [Firebase Console](https://console.firebase.google.com) → tu proyecto → **Cloud Messaging** → ya viene habilitado con el BOM 32.7.0.

### 2. Desplegar la Cloud Function

Crea el archivo `functions/index.js` con el siguiente contenido:

```javascript
const { onDocumentCreated } = require("firebase-functions/v2/firestore");
const { initializeApp }     = require("firebase-admin/app");
const { getMessaging }      = require("firebase-admin/messaging");

initializeApp();

/**
 * Se dispara cada vez que se crea un documento en /fcm_queue.
 * Envía el push al token (usuario específico) o al topic (broadcast).
 */
exports.procesarFcmQueue = onDocumentCreated("fcm_queue/{docId}", async (event) => {
    const data = event.data?.data();
    if (!data || data.processed) return;

    const messaging = getMessaging();

    try {
        if (data.token) {
            // Notificación a un usuario específico
            await messaging.send({
                token: data.token,
                notification: { title: data.title, body: data.body },
                android: {
                    notification: { channelId: data.channel ?? "channel_citas" }
                },
                data: { channel: data.channel ?? "channel_citas" }
            });
        } else if (data.topic) {
            // Notificación broadcast a un topic (ej: "trabajadores")
            await messaging.sendToTopic(data.topic, {
                notification: { title: data.title, body: data.body },
                data: { channel: data.channel ?? "channel_citas" }
            });
        }

        // Marcar como procesado para no re-enviar
        await event.data.ref.update({ processed: true });
    } catch (err) {
        console.error("Error enviando FCM:", err);
        await event.data.ref.update({ processed: true, error: err.message });
    }
});
```

**Para desplegar:**
```bash
npm install -g firebase-tools
firebase login
firebase init functions   # selecciona JavaScript
# copia el index.js de arriba
firebase deploy --only functions
```

---

## Reglas de Firestore para /fcm_queue y /usuarios

Agrega estas reglas en **Firestore → Reglas**:

```javascript
rules_version = '2';
service cloud.firestore {
  match /databases/{database}/documents {

    // Cualquier usuario autenticado puede leer/escribir sus propios datos
    match /usuarios/{uid} {
      allow read, write: if request.auth != null && request.auth.uid == uid;
    }

    // La cola FCM solo la escribe la app (usuario autenticado)
    // Solo las Cloud Functions (admin SDK) la procesan
    match /fcm_queue/{docId} {
      allow create: if request.auth != null;
      allow read, update: if false; // solo el admin SDK
    }
  }
}
```

---

## Flujo completo de notificaciones

```
CLIENTE agenda cita
    └─► ClienteViewModel.agendarCita()
            └─► NotificationHelper.notificarNuevaCitaATrabajadores()
                    ├─► Notificación LOCAL (trabajador con app abierta)
                    └─► fcm_queue/{id} → Cloud Function → FCM → trabajadores en background

TRABAJADOR acepta cita
    └─► CitasViewModel.aceptarCita()
            └─► NotificationHelper.notificarCitaAceptada()
                    ├─► Notificación LOCAL (si cliente tiene app abierta)
                    └─► fcm_queue/{id} → Cloud Function → FCM → cliente en background
                    Mensaje: "✅ Tu cita ha sido aceptada para las HH:mm. ¡Por favor no llegues tarde!"

TRABAJADOR rechaza cita
    └─► CitasViewModel.rechazarCita()
            └─► NotificationHelper.notificarCitaRechazada()
                    Mensaje: "❌ Lo sentimos, hemos rechazado tu cita. 
                              Si quieres saber el motivo, entra a tu app y vuelve a solicitarla. ¡Te esperamos!"

TRABAJADOR finaliza servicio (descuenta stock)
    └─► CitasViewModel.finalizar()
            └─► Para cada repuesto descontado:
                    if (repuesto.stockBajo)
                        NotificationHelper.notificarStockBajo()
                            └─► Notificación LOCAL al trabajador/admin
                            Mensaje: "⚠️ Stock bajo: [nombre]. Quedan X unidades (mínimo: Y)."
```

---

## Suscripción de trabajadores al topic "trabajadores"

Para que los trabajadores reciban notificaciones broadcast (nueva cita), 
suscríbelos al topic al hacer login. Agrega esto en `LoginFragment` 
dentro del bloque `is LoginUiState.Success`:

```kotlin
// Suscribir trabajadores/admin al topic para recibir notificaciones de nuevas citas
if (state.role == UserRole.TRABAJADOR || state.role == UserRole.ADMINISTRADOR) {
    FirebaseMessaging.getInstance().subscribeToTopic("trabajadores")
}
```

---

## Tokens FCM — dónde se guardan

Cada vez que un usuario hace login, su token FCM se guarda en:
```
/usuarios/{uid}/fcmToken   (campo String)
```

Esto lo hace automáticamente `MotoStockFirebaseMessagingService.guardarTokenSiHayUsuario()`,
que se llama desde:
- `MotoStockApp.onCreate()` (al iniciar la app)
- `LoginFragment` al completar el login exitosamente
- `onNewToken()` del servicio FCM cuando Firebase rota el token
