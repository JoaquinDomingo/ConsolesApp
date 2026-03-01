# 🎮 Proyecto Consolas: Arquitectura Profesional Android

## 📝 Descripción Técnica
Este proyecto es una aplicación nativa para Android desarrollada en **Kotlin** que implementa una solución de catálogo de hardware y mensajería instantánea. La aplicación destaca por su robustez técnica, utilizando un patrón **Offline-First** donde los datos se sincronizan entre una base de datos local (Room) y un servidor remoto (MariaDB) a través de una API REST y WebSockets.

---

## 🏗️ Estructura del Software (Clases y Responsabilidades)

El proyecto está organizado siguiendo los principios de **Clean Architecture** y **MVVM**:

### 🔹 Capa de Datos (Data Layer)
Gestión de persistencia y comunicación con el servidor.

* **`ConsoleRepositoryImpl` & `MessageRepositoryImpl`**: Actúan como Single Source of Truth. Coordinan cuándo pedir datos a la API y cuándo guardarlos en la base de datos local.
* **`MessageDao` & `ConsoleDao`**: Interfaces de Room que gestionan las consultas SQL, utilizando `OnConflictStrategy.IGNORE` para evitar duplicados.
* **`ApiService`**: Definición de endpoints de Retrofit para el CRUD de consolas y el historial de chat.
* **`ChatService`**: Gestión de la conexión **WebSocket (WSS)** para el chat en tiempo real.
* **`SessionManager`**: Encapsula el acceso a `SharedPreferences` para gestionar el email y token del usuario de forma persistente.

### 🔹 Capa de Dominio (Domain Layer)
Contiene la lógica de negocio pura, independiente de librerías de Android.

* **`Console` / `Message` / `Game`**: Modelos de datos (Data Classes) que representan las entidades del negocio.
* **`AddNewConsoleUseCase`**: Interactor que valida y procesa la creación de una nueva consola sincronizando ambos repositorios.
* **`Mappers.kt`**: Funciones de extensión para transformar objetos entre capas (Entity ↔ Domain ↔ DTO).

### 🔹 Capa de Presentación (UI Layer)
Implementación de la interfaz de usuario reactiva.

* **`ConsoleViewModel` & `MessagesViewModel`**: Gestionan el estado de la pantalla mediante `StateFlow`. Exponen la lógica de negocio a la vista y sobreviven a cambios de configuración.
* **`MessageAdapter`**: Adaptador avanzado de RecyclerView que gestiona:
    * **Alineación Dinámica**: Uso de `Gravity.START/END` para diferenciar mensajes enviados de recibidos.
    * **Filtrado de Duplicados**: Uso de `distinctBy` para garantizar una interfaz limpia.
* **`AddConsoleFragment`**: Gestión de formularios con validación, DatePickers y captura de imágenes mediante `ActivityResultLauncher`.
* **`MensajesFragment`**: Interfaz de chat con scroll automático y observación de flujos de datos en tiempo real.

---

## 🛠️ Stack Tecnológico Detallado

| Herramienta | Implementación |
| :--- | :--- |
| **Inyección de Dependencias** | **Hilt (Dagger)** para proveer instancias de DAOs, Clientes API y Repositorios. |
| **Networking** | **Retrofit 2** para REST y **OkHttp 3** con interceptores para Ngrok. |
| **Real-time** | **WebSockets** sobre protocolos seguros (`wss://`). |
| **Base de Datos** | **Room Persistence Library** con soporte para consultas asíncronas vía `Flow`. |
| **Concurrencia** | **Kotlin Coroutines** (scopes de ViewModel y Repository) para evitar bloqueos en el hilo principal. |
| **Navegación** | **Jetpack Navigation Component** con `SafeArgs` para paso de datos entre fragmentos. |

---

## 🚀 Configuración y Despliegue

### 📡 Conexión con el Servidor (Ngrok)
Para permitir que la aplicación se comunique con un servidor local desde cualquier red, se utiliza un túnel de **Ngrok**:

1.  Ejecutar el túnel: `ngrok http 8081`.
2.  Configurar el `NetworkModule`: Actualizar la constante `BASE_URL` con la URL HTTPS de Ngrok.
3.  Configurar el `ChatService`: Asegurar el uso del prefijo `wss://` para el WebSocket.
4.  **Header Especial**: Se incluye automáticamente el header `"ngrok-skip-browser-warning": "true"` en todas las peticiones para evitar bloqueos del proxy.

### 💾 Persistencia e Integridad
Para evitar duplicados en el chat y la colección, se ha implementado:
* **Índices Únicos** en Room basados en la combinación de `sender + text + timestamp`.
* **Normalización de Tiempo**: Redondeo de milisegundos en el cliente para asegurar que el ID de sincronización coincida entre el socket y la base de datos.

---

## 👤 Autor
**Joaquín Domingo Domingo**
* 📧 [dojoaquindo@gmail.com](mailto:dojoaquindo@gmail.com)
* 🐙 [GitHub: JoaquinDomingo](https://github.com/JoaquinDomingo)

---
> *Este proyecto demuestra el dominio de patrones de diseño modernos, arquitectura limpia y manejo de comunicaciones complejas en el ecosistema Android.*