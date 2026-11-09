# AGENTS.md

## Proyecto

- Aplicación Spring Boot para gestionar matrículas, alumnos y cursos.
- Java 21, Maven, Spring MVC, Spring Data JPA y MySQL.
- Paquete base: `es.iesjuanbosco.matriculasspring`.

## Estructura

- `controller/`: endpoints REST (`AlumnoController`, `CursoController`, `MatriculaController`, `UsuarioController`).
- `service/`: servicios con la lógica de negocio y el acceso a repositorios para alumnos, cursos, materias, matrículas, usuarios y detalle de usuarios; `AuthService` gestiona el registro de usuarios.
- `exception/`: manejadores globales de excepciones REST.
- `entity/`: entidades JPA (`Alumno`, `Curso`, `Matricula`, `Usuario`, `DetalleUsuario`).
- `repository/`: interfaces Spring Data basadas en `JpaRepository`.
- `frontend/`: cliente web con HTML, CSS y JavaScript puro (sin frameworks ni librerías CSS); la URL de la API está en `frontend/js/config.js`. `CorsConfig` habilita CORS para cualquier origen (solo desarrollo).
- Endpoints de matrículas (`MatriculaController`): `GET /matriculas`, `POST /matriculas` (cuerpo con la entidad `Matricula`: `alumno.id`, `curso.id`, `cursoLectivo`, `pagoSeguro`), `DELETE /matriculas/{id}`, `GET /cursos/{cursoId}/matriculas` y `GET /alumnos/{alumnoId}/matriculas`.
- Endpoints de detalle de usuario (`UsuarioController`): `POST /usuarios/{usuarioId}/detalle-usuario` para asociar un detalle a un usuario y `GET /usuarios/{usuarioId}/detalle-usuario` para consultarlo y `DELETE /usuarios/{usuarioId}/detalle-usuario` para borrarlo.
- Las listas `matriculas` de `Alumno` y `Curso` llevan `@JsonIgnore` para evitar recursión al serializar.
- `src/main/resources/application.properties`: configuración de la aplicación y conexión a la base de datos.
- `src/test/`: pruebas automatizadas.

## Convenciones de implementación

- Conserva los nombres, rutas y estilo de las clases existentes, salvo que la tarea requiera cambiarlos.
- Para nuevos endpoints REST, usa las anotaciones de Spring MVC y rutas con recursos en plural, como `/alumnos` y `/cursos`.
- Devuelve códigos HTTP explícitos y coherentes. Usa `ResponseEntity` cuando la respuesta dependa del resultado: `200 OK` para consultas o actualizaciones correctas, `201 Created` al crear, `204 No Content` al borrar y `404 Not Found` cuando no exista el recurso solicitado.
- El manejador global devuelve errores de validación como un mapa de campo a mensaje y responde con `400 Bad Request`.
- `POST /register` devuelve `409 Conflict` con `{"error":"El email ya está registrado"}` cuando el email está repetido; `GlobalExceptionHandler` gestiona `EmailRepetidoExpepcion`.
- Los controladores no acceden directamente a repositorios: delegan en servicios, y toda lógica de negocio se implementa siempre en servicios. Hay servicios para alumnos, cursos, materias, matrículas, usuarios y detalle de usuarios.
- No uses `Optional.get()` sin comprobar antes si el valor está presente; maneja el caso no encontrado explícitamente.
- Mantén los controladores centrados en HTTP. Usa `JpaRepository` para las operaciones de persistencia existentes desde los servicios.
- Utiliza siempre inyección de dependencias por constructor, no por campos ni setters. Si la clase tiene un único constructor, no añadas `@Autowired`; úsalo solo cuando sea necesario para seleccionar el constructor.
- Se recomienda usar DTOs para las entradas y salidas de la API: los DTOs de entrada terminan en `Request` y los de salida en `Response`.
- Utiliza el patrón Builder en las Entidades.
- Cuando crees nuevos archivos ejecuta un git add <nombre_del_archivo>

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