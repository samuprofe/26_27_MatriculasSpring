# Ejercicio: JWT y permisos por roles

## Parte 1. Investiga y amplía el JWT

1. Obtén un token y úsalo para consultar una ruta protegida. Reinicia la
   aplicación y comprueba si el mismo token sigue funcionando. Explica
   el resultado.

2. Modifica la configuración para que la clave de firma se configure
   mediante `application.properties` y no cambie al reiniciar. 
   Comprueba que un token válido siga funcionando tras reiniciar, siempre
   que se conserve la misma clave.

3. Añade al JWT un *claim* con información adicional no sensible, por
   ejemplo el nombre del usuario. Inspecciona el token localmente y
   comprueba que contiene el nuevo dato. No pegues tokens en servicios
   externos.

## Parte 2. Protege las matrículas por roles

Aplica esta política en `MatriculaController` mediante `@PreAuthorize`:

| Operación | USER | SECRETARIA | ADMIN |
|---|---:|---:|---:|
| Consultar matrículas, resumen y consultas por alumno o curso | Sí | Sí | Sí |
| Crear una matrícula | No | Sí | Sí |
| Eliminar una matrícula | No | No | Sí |


Crea usuarios con esos Roles y comprueba que las operaciones se comportan según la tabla.


### Permisos generales en `SecurityConfig`

Además de proteger `MatriculaController`, configura estas reglas mediante
`requestMatchers`:

- `/auth/**`: acceso público.
- `GET /alumnos/**` y `GET /cursos/**`: cualquier usuario autenticado.
- `DELETE /cursos/**`: solo administradores.
- El resto de peticiones: requieren autenticación.

Ten en cuenta el orden de las reglas y comprueba qué sucede si una regla
general se coloca antes que otra más específica. Prueba con distintos usuarios
que funcionen los permisos indicados


### Pregunta para pensar

Después de obtener un token de `USER`, cambia ese usuario a `ROLE_ADMIN`
y repite una operación reservada al administrador **sin volver a iniciar
sesión**. ¿Cambia el resultado? ¿Por qué? ¿Qué ocurre si inicia sesión de
nuevo?