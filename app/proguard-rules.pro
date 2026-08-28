# Reglas de ProGuard para MotoStock
# minifyEnabled está en false, así que estas reglas solo aplican si lo activas
# más adelante para el build de release.

# Room
-keep class com.taller.motostock.core.database.entity.** { *; }

# Gson (usado por Room TypeConverters y por Retrofit)
-keepattributes Signature
-keepattributes *Annotation*
-keep class com.taller.motostock.core.network.dto.** { *; }
-keep class com.taller.motostock.core.domain.model.** { *; }

# Firebase Firestore serializa los data class por reflexión
-keepclassmembers class com.taller.motostock.core.domain.model.** {
    <init>(...);
}
