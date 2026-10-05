# AGENTS.md

## Proyecto

- Aplicación Spring Boot para gestionar matrículas, alumnos y cursos.
- Java 21, Maven, Spring MVC, Spring Data JPA y MySQL.
- Paquete base: `es.iesjuanbosco.matriculasspring`.

## Estructura

- `controller/`: endpoints REST (`AlumnoController`, `CursoController`).
- `entity/`: entidades JPA (`Alumno`, `Curso`).
- `repository/`: interfaces Spring Data basadas en `JpaRepository`.
- `src/main/resources/application.properties`: configuración de la aplicación y conexión a la base de datos.
- `src/test/`: pruebas automatizadas.

## Convenciones de implementación

- Conserva los nombres, rutas y estilo de las clases existentes, salvo que la tarea requiera cambiarlos.
- Para nuevos endpoints REST, usa las anotaciones de Spring MVC y rutas con recursos en plural, como `/alumnos` y `/cursos`.
- Devuelve códigos HTTP explícitos y coherentes. Usa `ResponseEntity` cuando la respuesta dependa del resultado: `200 OK` para consultas o actualizaciones correctas, `201 Created` al crear, `204 No Content` al borrar y `404 Not Found` cuando no exista el recurso solicitado.
- No uses `Optional.get()` sin comprobar antes si el valor está presente; maneja el caso no encontrado explícitamente.
- Mantén controladores centrados en HTTP y acceso a repositorios. Usa `JpaRepository` para las operaciones de persistencia existentes.
- Para código nuevo, prefiere inyección por constructor. Evita combinar inyección por campo y por constructor en la misma clase.
- Mantén las entidades y sus mapeos JPA compatibles con el esquema y el comportamiento ya existentes. No cambies contratos o rutas sin que la tarea lo pida.
- No añadas dependencias, capas o abstracciones nuevas si la funcionalidad puede implementarse con las tecnologías y patrones actuales.
- Utiliza el patrón Builder en las Entidades.

## Configuración y secretos

- No incluyas contraseñas, tokens ni otros secretos en el código, documentación, registros o commits.
- Trata los valores de conexión y credenciales de `application.properties` como información sensible. No los muestres ni los copies a otros archivos.
- Si una tarea requiere cambiar la configuración de base de datos, conserva las propiedades no relacionadas y usa variables de entorno o un mecanismo local no versionado para los secretos.

## Validación

- El proyecto requiere un JDK completo compatible con Java 21, incluido `javac`.
- Ejecuta las pruebas con `./mvnw test`; para compilar sin ejecutar pruebas, usa `./mvnw -DskipTests package`.
- Si la validación falla por falta de JDK, base de datos u otro requisito del entorno, informa del bloqueo en vez de afirmar que las pruebas pasaron.
- Añade o actualiza pruebas cuando cambie el comportamiento observable de endpoints o persistencia, siguiendo la configuración de pruebas ya presente.

## Actualización AGENTS.md
- Mantén este archivo actualizado con la información relevante del proyecto, estructura, convenciones y validación.