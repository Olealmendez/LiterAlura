# 📚 LiterAlura - Catálogo de Libros

Bienvenido a **LiterAlura**, una aplicación de consola desarrollada en Java utilizando el framework Spring Boot. Este proyecto funciona como un catálogo interactivo que permite a los usuarios buscar libros a través de la API pública de Gutendex, guardar la información en una base de datos local y realizar diversas consultas personalizadas.

## 🚀 Funcionalidades

El sistema cuenta con un menú interactivo que ofrece las siguientes opciones:

**Funcionalidades principales:**
1. **Buscar libro por título:** Consulta la API de Gutendex y guarda el libro y su autor en la base de datos PostgreSQL.
2. **Listar libros registrados:** Muestra todos los libros que han sido guardados en la base de datos local.
3. **Listar autores registrados:** Muestra una lista de todos los autores almacenados.
4. **Listar autores vivos en un determinado año:** Filtra y muestra los autores que estaban vivos en el año ingresado por el usuario.
5. **Listar libros por idioma:** Permite buscar en la base de datos local los libros según su idioma (ej. `es`, `en`, `fr`).

**Funcionalidades Extra (Opcionales implementadas):**

6. **Top 10 libros más descargados:** Muestra un ranking de los libros más populares almacenados en la base de datos.
7. **Buscar autor por nombre:** Permite localizar a un autor específico registrado en la base de datos.
8. **Estadísticas de descargas:** Genera estadísticas generales (promedio, máximo y mínimo) de descargas de los libros registrados.

## 🛠️ Tecnologías Utilizadas

* **Java 21**
* **Spring Boot 4**
* **Spring Data JPA** (Persistencia de datos y Derived Queries)
* **PostgreSQL** (Base de datos relacional)
* **Jackson** (Mapeo y deserialización de JSON)
* **Maven** (Gestor de dependencias)

## ⚙️ Configuración y Ejecución

Para ejecutar este proyecto localmente, sigue estos pasos:

1. Clona este repositorio:
   ```bash
   git clone https://github.com/Olealmendez/LiterAlura-.git
2. Crea una base de datos en PostgreSQL llamada literalura.

3. Configura las variables de entorno en tu IDE para proteger tus credenciales de base de datos:

* **DB_USER = tu_usuario_de_postgres**

* **DB_PASSWORD = tu_contraseña**

4. Ejecuta la clase principal LiterAluraApplication.

5. ¡Disfruta explorando libros desde la consola!

Desarrollado como parte del Challenge de Java de Alura Latam.
