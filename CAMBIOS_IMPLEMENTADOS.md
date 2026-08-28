# MotoStock - Actualización con Soporte de Roles (Trabajador y Cliente)

## 📋 Resumen de Cambios

Se ha implementado un sistema de roles de usuario que permite que la aplicación funcione tanto para **TRABAJADORES** del taller como para **CLIENTES**. Cada rol tiene su propia interfaz y funcionalidades específicas.

---

## 🔑 Nuevos Componentes Creados

### 1. **Modelo de Roles** (`UserRole.kt`)
```
- Enum: UserRole (TRABAJADOR, CLIENTE)
- Data Class: Usuario (uid, email, nombre, telefono, role, creadoEn)
```

### 2. **ViewModel para Clientes** (`ClienteViewModel.kt`)
- Gestiona búsqueda de citas por placa
- Permite agendar nuevas citas
- Estados UI: Idle, Loading, Success, Error
- Funcionalidades:
  - Buscar historial de servicios por placa
  - Agendar nuevas citas
  - Validar datos ingresados

### 3. **Fragments para Cliente**

#### a) `MisServiciosFragment.kt`
- Interfaz para que cliente vea su historial de servicios
- Búsqueda por número de placa
- Muestra estado actual de cada servicio (En proceso / Finalizado)
- Eventos:
  - `btnBuscar`: Busca citas asociadas a la placa
  - `btnLimpiar`: Limpia los campos

#### b) `AgendarCitaFragment.kt`
- Formulario para agendar nuevas citas
- Campos:
  - Placa (obligatorio)
  - Nombre del propietario (obligatorio)
  - Teléfono (obligatorio)
  - Modelo de la moto (obligatorio)
  - Tipo de servicio (spinner con opciones)
  - Descripción del problema
- Validación de datos antes de guardar
- Confirmación de éxito

### 4. **Adapter** (`MisServiciosAdapter.kt`)
- Muestra lista de servicios del cliente
- Usa DiffUtil para actualizaciones eficientes
- Formatea fechas a idioma español
- Código de colores para estados

### 5. **Layouts XML**

#### `fragment_mis_servicios.xml`
- SearchBar con placa
- RecyclerView para mostrar servicios
- Estados: Loading, Success, Error
- Altura mínima: Match parent

#### `fragment_agendar_cita.xml`
- ScrollView con formulario completo
- EditText para datos básicos
- Spinner para tipo de servicio
- TextArea para descripción
- Botones: Agendar y Limpiar

#### `item_mis_servicios.xml`
- Card con información del servicio
- Muestra placa, modelo, fecha, tipo, descripción
- Color visual según estado
- Trunca descripción con ellipsize

---

## 🔄 Flujo de Uso para Clientes

### Nuevo Cliente
1. Registrarse en Login
2. El sistema lo asigna como rol CLIENTE
3. Accede a pantalla de cliente

### Cliente Buscando Servicios
1. Ingresa placa en "Mis Servicios"
2. Sistema busca todas sus citas asociadas
3. Ve historial completo con estados

### Cliente Agendando Cita
1. Ir a "Agendar Cita"
2. Llenar formulario
3. Seleccionar tipo de servicio
4. Descripción del problema
5. Sistema guarda y muestra confirmación

---

## 📊 Estructura de Datos

### Nueva Colección en Firestore (usuarios)
```json
{
  "uid": "string",
  "email": "string",
  "nombre": "string",
  "telefono": "string",
  "role": "trabajador|cliente",
  "creadoEn": "timestamp"
}
```

### Cambios en Cita
```json
{
  "id": "string",
  "placa": "string",
  "propietario": "string",
  "telefono": "string",
  "modelo": "string",
  "fechaIngreso": "timestamp",
  "horaIngreso": "string",
  "tipoServicio": "string",
  "descripcion": "string",
  "estado": "EN_TALLER|FINALIZADO",
  "fechaSalida": "timestamp|null",
  "horaSalida": "string"
}
```

---

## 🔐 Diferencias de Roles

### TRABAJADOR
- ✓ Acceso a Inventario
- ✓ Acceso a Historial (todas las motos)
- ✓ Registro de servicios
- ✓ Marcar tareas como completadas
- ✓ Ver en taller (lista de motos en proceso)

### CLIENTE
- ✓ Ver mis servicios (solo sus citas)
- ✓ Agendar nueva cita
- ✓ Ver estado de sus motos
- ✓ Ver historial de sus servicios
- ✗ NO puede ver datos de otros clientes
- ✗ NO puede acceder a inventario

---

## 🛠️ Próximas Mejoras Recomendadas

1. **Autenticación por Rol**
   - Modificar LoginViewModel para asignar rol desde Firestore
   - Mostrar UI diferente según rol

2. **Notificaciones**
   - Notificar al cliente cuando su moto está lista
   - Notificar cambios de estado

3. **Presupuestos**
   - Cliente puede ver presupuesto estimado
   - Confirmar antes de iniciar servicio

4. **Calificaciones**
   - Cliente califica el servicio
   - Comentarios sobre el trabajo realizado

5. **Pagos en Línea**
   - Integrar pasarela de pagos
   - Cliente paga desde app

6. **Chat en Vivo**
   - Comunicación entre cliente y taller
   - Consultas sobre el trabajo

7. **Historial Detallado**
   - Mostrar repuestos usados
   - Costo desglosado
   - Técnico que realizó el trabajo

---

## 📱 Pasos para Integración

1. **Actualizar LoginViewModel** para guardar rol
2. **Modificar MainActivity** para mostrar navegación según rol
3. **Crear UserRepository** para gestionar usuarios
4. **Actualizar authRepositoryImpl** para guardar rol en Firestore
5. **Conectar ClienteViewModel** a CitaRepository
6. **Implementar permisos** Firestore por rol
7. **Testing** en ambos flujos

---

## 🔗 Archivos Modificados

- Nueva carpeta: `presentation/ui/cliente/`
- Nuevo ViewModel: `presentation/viewmodel/ClienteViewModel.kt`
- Nuevo Adapter: `presentation/adapter/MisServiciosAdapter.kt`
- Nuevo modelo: `domain/model/UserRole.kt`
- 3 nuevos Layouts
- Documentación actualizada

---

## 📝 Notas Importantes

- El binding debe estar habilitado en build.gradle
- Data binding se utiliza en los layouts
- Corrutinas con Hilt DI
- Flow para estado reactivo
- Room + Firestore sincronizados

---

**Versión**: 2.0  
**Fecha**: Agosto 2026  
**Estado**: Listo para Integración
