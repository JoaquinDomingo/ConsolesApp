#  Aplicación Consolas

##  Resumen del Proyecto

Esta aplicación es una aplicación móvil construida usando **Kotlin** para dispositivos **Android**.

En las versiones iniciales, proporcionará a los usuarios un **catálogo completo de consolas de videojuegos**, permitiéndoles navegar a través de varias consolas, ver información básica (que se ampliará con un menú desplegable en versiones posteriores) y **gestionar sus consolas favoritas**.

Además, los usuarios pueden **añadir nuevas consolas** al catálogo, asegurando que la aplicación se mantenga actualizada. En futuras versiones, los usuarios también podrán **editar y eliminar** consolas. Además, se añadirán las funciones para ver **juegos relacionados** con cada consola, ya sean juegos compatibles con la consola o juegos que se lanzaron para ella.

##  Características

* Navegar por un catálogo de consolas de videojuegos.
* Ver información detallada sobre cada consola.
* **Añadir nuevas consolas** al catálogo.
* **Gestionar una lista de consolas favoritas**.
* **Futuras actualizaciones** incluirán edición y eliminación de consolas.
* **Futuras actualizaciones** incluirán la visualización de juegos relacionados para cada consola.
* Autenticación de usuario y gestión de perfiles (planeado para futuras versiones).
* Funciones para compartir en redes sociales (planeado para futuras versiones).

##  Tecnologías Utilizadas

* **Kotlin**
* **Android SDK**
* **SQLite** para almacenamiento de datos local (aún no implementado).
* **Firebase** para autenticación de usuario (planeado para futuras versiones).
* **Arquitectura MVC** que se migrará a **MVVM** en futuras versiones.
* `Activities` para diferentes pantallas y sus respectivos *layouts*.
* `RecyclerView` para mostrar listas de consolas.
* `Intents` para la navegación entre *activities* (aún no implementado).

## ️ Arquitectura

La aplicación sigue el patrón de arquitectura **Modelo-Vista-Controlador (MVC)**, que separa la lógica de la aplicación en tres componentes interconectados:

* **Modelo (Model):**
    * Representa los datos y la lógica de negocio.
    * Incluye clases que definen la estructura de una consola de videojuegos y manejan las operaciones de datos.
    * La clase `Console` se encuentra en el paquete 'model'.

* **Vista (View):**
    * Representa la interfaz de usuario de la aplicación.
    * Es responsable de mostrar datos al usuario y capturar la entrada del usuario.
    * Consiste en archivos *layout* XML que definen los componentes de la UI para diferentes pantallas.
    * Se utiliza `RecyclerView` para mostrar listas de consolas.
    * (En futuras versiones, la Vista incluirá componentes de UI para autenticación y perfiles).

* **Controlador (Controller):**
    * Actúa como un intermediario entre el Modelo y la Vista.
    * Maneja la entrada del usuario, actualiza el Modelo y refresca la Vista.
    * Incluye `Activities` que gestionan las interacciones. La `MainActivity` es responsable de mostrar la lista de consolas.

* **Adaptadores (Adapters):**
    * La aplicación utiliza `Adapters` para vincular datos del Modelo a los componentes de la Vista, particularmente para mostrar listas en `RecyclerViews`.
    * El `ConsoleAdapter` es responsable de poblar el `RecyclerView` con datos de la consola.
    * `ViewHConsole` se utiliza para contener las vistas de cada elemento de consola en el `RecyclerView`.

##  Futuras Mejoras

* Implementar autenticación de usuario y gestión de perfiles usando **Firebase**.
* Añadir funcionalidad para **editar y eliminar** consolas del catálogo.
* Integrar un servicio *backend* para obtener datos de consolas y juegos relacionados.
* Migrar de **MVC a MVVM** para una mejor separación de preocupaciones y facilitar las pruebas.

##  Autor

* **Joaquin Domingo Domingo**
* **Email:** dojoaquindo@gmail.com
* **GitHub:** [JoaquinDomingo]

## Otra Información

> Este proyecto es un proyecto educativo y no está destinado a uso comercial. Se está desarrollando para mejorar mis habilidades en el desarrollo de Android usando Kotlin para un proyecto de clase.
