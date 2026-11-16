# MatriculasSpring

Aplicación REST para gestionar alumnos, cursos y matrículas de un sistema de matrículas. La API permite consultar, crear, actualizar y eliminar registros, y persiste los datos en MySQL mediante Spring Data JPA.

## Tecnologías

- Java 21
- Spring Boot 4.1.1 y Spring MVC
- Spring Data JPA / Hibernate
- Spring Security y JWT
- MySQL
- Maven (incluye Maven Wrapper)

## Requisitos

- JDK 21 instalado y disponible (`java` y `javac`).
- MySQL en ejecución y permisos para crear o utilizar la base de datos `matriculas`.
- Un navegador para usar el frontend; Postman es opcional para probar la API.

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

## Autenticación y seguridad

Las rutas `/auth/**` son públicas. El resto de la API requiere un JWT válido en la cabecera `Authorization: Bearer <token>`. La aplicación no utiliza sesiones de servidor.

1. Registra una cuenta con `POST /auth/register` y un cuerpo JSON con `nombre`, `email` y `password`. También puedes incluir `apellidos` y `fechaNacimiento` (formato `AAAA-MM-DD`). El registro crea un usuario activo con `ROLE_USER` y guarda la contraseña cifrada con BCrypt. Si el email ya existe, responde `409 Conflict`.
2. Inicia sesión con `POST /auth/login`, enviando un JSON con `email` y `password`. Si las credenciales son correctas, la respuesta `200 OK` contiene el campo `token`; si no lo son, devuelve `401 Unauthorized`.
3. Envía el token en la cabecera `Authorization` de cada petición a una ruta protegida. No envíes un token inválido a las rutas públicas, ya que también se intentará validar.

Los JWT se firman con HS256, tienen el emisor `App-Matriculas` y caducan a las 24 horas. La clave de firma se genera al iniciar la aplicación: al reiniciarla, los tokens anteriores dejan de ser válidos y hay que iniciar sesión de nuevo. Esta configuración, así como CORS abierto a cualquier origen y el almacenamiento del token en `localStorage`, están orientados al desarrollo y deben revisarse antes de desplegar en producción.

## Uso desde Postman

1. Asegúrate de que MySQL y la aplicación estén en ejecución.
2. Crea una petición en Postman y utiliza `http://localhost:8080` como base URL.
3. Registra un usuario y solicita un token mediante las rutas de autenticación descritas arriba.
4. En las peticiones protegidas, selecciona **Authorization → Bearer Token** y pega el token recibido.
5. Selecciona el método HTTP correspondiente y añade la ruta indicada en la tabla de endpoints.
6. Para `POST`, `PUT` y `PATCH`, selecciona **Body → raw → JSON** y establece `Content-Type: application/json`.
7. Envía la petición y consulta el código HTTP y el cuerpo de la respuesta.

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
| `POST` | `/auth/register` | Registrar una cuenta | `201 Created`, `400 Bad Request`, `409 Conflict` |
| `POST` | `/auth/login` | Iniciar sesión y obtener un JWT | `200 OK`, `400 Bad Request`, `401 Unauthorized` |
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

La carpeta `frontend/` contiene una interfaz web en HTML, CSS y JavaScript puro (sin frameworks) para registrarse e iniciar sesión, gestionar alumnos y cursos (CRUD), matricular y desmatricular alumnos, y consultar matrículas por curso o alumno.

1. Arranca el backend (`./mvnw spring-boot:run`).
2. Sirve la carpeta del frontend, por ejemplo:

   ```bash
   cd frontend
   python3 -m http.server 5500
   ```

3. Abre `http://localhost:5500`.

La página inicial muestra el login y un enlace al registro. Tras iniciar sesión, el token se guarda en `localStorage` y se envía automáticamente a la API; el botón **Cerrar sesión** lo elimina. Si una petición protegida devuelve `401`, el frontend elimina el token y vuelve al login.

La URL del backend se configura en `frontend/js/config.js`. `CorsConfig` permite las peticiones desde cualquier origen, por lo que también puedes abrir `index.html` directamente.

## Pruebas

Con un JDK 21 completo disponible, ejecuta las pruebas con:

```bash
./mvnw test
```

## Estructura del código

- `controller/`: controladores REST, incluido el de autenticación.
- `service/`: servicios de negocio y autenticación, incluida la generación JWT.
- `config/`: configuración de seguridad.
- `entity/`: entidades JPA y sus campos.
- `repository/`: repositorios Spring Data JPA.
- `src/main/resources/application.properties`: configuración de la aplicación y de la conexión a MySQL.
- `frontend/`: interfaz web estática (`index.html`, `css/`, `js/`).
- `src/test/`: pruebas automatizadas.

## TODO
- Registrar qué usuario añade cada matrícula.
