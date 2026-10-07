# MatriculasSpring

Aplicación REST para gestionar alumnos, cursos y matrículas de un sistema de matrículas. La API permite consultar, crear, actualizar y eliminar registros, y persiste los datos en MySQL mediante Spring Data JPA.

## Tecnologías

- Java 21
- Spring Boot 4.1.1 y Spring MVC
- Spring Data JPA / Hibernate
- MySQL
- Maven (incluye Maven Wrapper)

## Requisitos

- JDK 21 instalado y disponible (`java` y `javac`).
- MySQL en ejecución y permisos para crear o utilizar la base de datos `matriculas`.
- Postman para enviar peticiones a la API.

## Instalación y ejecución

1. Clona el repositorio y entra en el directorio del proyecto:

   ```bash
   git clone https://github.com/samuprofe/26_27_MatriculasSpring.git
   cd 26_27_MatriculasSpring
   ```

2. Configura la conexión a MySQL en `src/main/resources/application.properties`. Ajusta la URL, el usuario y la contraseña a tu entorno. No publiques credenciales reales en el repositorio.

   La configuración actual apunta a `localhost:3306` y a la base de datos `matriculas`. La URL incluye `createDatabaseIfNotExist=true`; el usuario de MySQL debe tener permisos suficientes para crearla si todavía no existe.

3. Inicia la aplicación:

   ```bash
   ./mvnw spring-boot:run
   ```

   En Windows puedes utilizar:

   ```bat
   mvnw.cmd spring-boot:run
   ```

   Spring Boot inicia el servidor en `http://localhost:8080`. Hibernate está configurado para actualizar el esquema de la base de datos (`spring.jpa.hibernate.ddl-auto=update`).

## Uso desde Postman

1. Asegúrate de que MySQL y la aplicación estén en ejecución.
2. Crea una petición en Postman y utiliza `http://localhost:8080` como base URL.
3. Selecciona el método HTTP correspondiente y añade la ruta indicada en la tabla de endpoints.
4. Para `POST`, `PUT` y `PATCH`, selecciona **Body → raw → JSON** y establece `Content-Type: application/json`.
5. Envía la petición y consulta el código HTTP y el cuerpo de la respuesta.

### Ejemplos

Crear un alumno con `POST /alumnos`:

```json
{
  "nombre": "Ana",
  "apellidos": "García López",
  "email": "ana@example.com",
  "fechaNacimiento": "2005-04-17",
  "DNI": "12345678A",
  "telefono": "600000000",
  "importeBeca": 250.00
}
```

Crear un curso con `POST /cursos`:

```json
{
  "nombre": "Desarrollo de Aplicaciones Web",
  "abreviatura": "DAW",
  "nivel": "CFGS"
}
```

Crear una matrícula con `POST /matriculas` (el alumno y el curso deben existir):

```json
{
  "alumno": { "id": 1 },
  "curso": { "id": 2 },
  "cursoLectivo": "2026/27",
  "pagoSeguro": true
}
```

El campo `nivel` admite `ESO`, `BACHILLERATO`, `CFGS`, `CFGM` o `CFGB`. El identificador se genera en la base de datos y no hace falta incluirlo al crear.

Actualizar un alumno o curso requiere enviar el objeto completo con `PUT /alumnos/{id}` o `PUT /cursos/{id}`. El identificador de la ruta es el que se guarda. Para modificar solo el importe de la beca, usa `PATCH /alumnos/{id}/importe-beca` con un número JSON como cuerpo, por ejemplo:

```json
250.00
```

### Endpoints disponibles

| Método | Ruta | Uso | Respuestas principales |
|---|---|---|---|
| `GET` | `/alumnos` | Listar alumnos | `200 OK` |
| `GET` | `/alumnos/{id}` | Consultar un alumno | `200 OK`, `404 Not Found` |
| `POST` | `/alumnos` | Crear un alumno | `201 Created` |
| `PUT` | `/alumnos/{id}` | Actualizar un alumno existente | `200 OK`, `404 Not Found` |
| `PATCH` | `/alumnos/{id}/importe-beca` | Cambiar el importe de la beca | `200 OK`, `404 Not Found` |
| `DELETE` | `/alumnos/{id}` | Eliminar un alumno | `204 No Content` |
| `GET` | `/cursos` | Listar cursos | `200 OK` |
| `GET` | `/cursos/{id}` | Consultar un curso | `200 OK`, `404 Not Found` |
| `POST` | `/cursos` | Crear un curso | `201 Created` |
| `PUT` | `/cursos/{id}` | Actualizar un curso existente | `200 OK`, `404 Not Found` |
| `DELETE` | `/cursos/{id}` | Eliminar un curso | `204 No Content`, `404 Not Found` |
| `DELETE` | `/cursos` | Eliminar todos los cursos | `204 No Content` |
| `GET` | `/matriculas` | Listar matrículas | `200 OK` |
| `POST` | `/matriculas` | Matricular un alumno en un curso | `201 Created`, `400 Bad Request`, `404 Not Found` |
| `DELETE` | `/matriculas/{id}` | Eliminar una matrícula (quitar al alumno del curso) | `204 No Content`, `404 Not Found` |
| `GET` | `/cursos/{cursoId}/matriculas` | Matrículas de un curso | `200 OK`, `404 Not Found` |
| `GET` | `/alumnos/{alumnoId}/matriculas` | Matrículas de un alumno | `200 OK`, `404 Not Found` |

`DELETE /cursos` elimina todos los cursos; úsalo con cuidado.

## Frontend

La carpeta `frontend/` contiene una interfaz web en HTML, CSS y JavaScript puro (sin frameworks) para gestionar alumnos y cursos (CRUD), matricular y desmatricular alumnos, y consultar matrículas por curso o alumno.

1. Arranca el backend (`./mvnw spring-boot:run`).
2. Sirve la carpeta del frontend, por ejemplo:

   ```bash
   cd frontend
   python3 -m http.server 5500
   ```

3. Abre `http://localhost:5500`.

La URL del backend se configura en `frontend/js/config.js`. `CorsConfig` permite las peticiones desde cualquier origen, por lo que también puedes abrir `index.html` directamente.

## Pruebas

Con un JDK 21 completo disponible, ejecuta las pruebas con:

```bash
./mvnw test
```

## Estructura del código

- `controller/`: controladores REST de alumnos, cursos y matrículas.
- `entity/`: entidades JPA y sus campos.
- `repository/`: repositorios Spring Data JPA.
- `src/main/resources/application.properties`: configuración de la aplicación y de la conexión a MySQL.
- `frontend/`: interfaz web estática (`index.html`, `css/`, `js/`).
- `src/test/`: pruebas automatizadas.
