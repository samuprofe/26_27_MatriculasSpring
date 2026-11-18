# Tarea: API de incidencias del centro con JWT

Desarrolla una aplicación para registrar y gestionar incidencias del centro,
como una avería en un aula o un problema con un equipo informático. La API
debe utilizar JWT para autenticar a los usuarios y controlar qué operaciones
pueden realizar.

## Funcionalidad

Cada incidencia tendrá, como mínimo, un título, una descripción, una fecha
de creación, un estado y un autor. Los estados serán `ABIERTA`, `EN_PROCESO`
y `RESUELTA`.

| Acción | USER | TECNICO | ADMIN |
|---|---:|---:|---:|
| Crear una incidencia | Sí | Sí | Sí |
| Consultar sus propias incidencias | Sí | Sí | Sí |
| Consultar cualquier incidencia | No | Sí | Sí |
| Cambiar el estado de una incidencia | No | Sí | Sí |
| Eliminar cualquier incidencia | No | No | Sí |
| Consultar usuarios y modificar sus roles | No | No | Sí |

## Requisitos de seguridad

1. Permite el registro público de usuarios con el rol inicial `ROLE_USER` y
   almacena las contraseñas cifradas con BCrypt. El registro no debe permitir
   elegir roles privilegiados.
2. Implementa un login que devuelva un JWT firmado con una clave configurada
   mediante propiedades. El token debe
   incluir el identificador del usuario, sus roles, una fecha de caducidad y
   un *claim* adicional no sensible.
3. Protege las rutas generales en `SecurityConfig` y aplica permisos por rol
   a las operaciones que lo requieran.
4. Al crear una incidencia, obtén el autor a partir de la autenticación. **No
   aceptes como autor un identificador enviado por el cliente.**
5. Permite únicamente a un administrador consultar usuarios y modificar sus
   roles. Valida en el backend que el rol solicitado sea uno de los
   admitidos. Protege estas operaciones aunque el frontend también limite su
   visibilidad.

## Reto principal: acceso a incidencias propias

Un usuario con rol `USER` solo puede consultar sus propias incidencias, tanto
en el listado como al acceder a una incidencia concreta por URL.

Por ejemplo, si Ana solicita `GET /incidencias/15`, la aplicación debe
comprobar que la incidencia pertenece a Ana. **Cambiar el número de la URL no
debe permitirle acceder a incidencias de otro usuario**, aunque conozca su
identificador.

Los usuarios `TECNICO` y `ADMIN` sí pueden consultar incidencias de cualquier
autor. Implementa la comprobación en el backend, utilizando la identidad
autenticada; ocultar enlaces en el frontend no es suficiente.

## Frontend básico con ayuda de IA

Utiliza una herramienta de inteligencia artificial para ayudarte a crear un
frontend básico con HTML y JavaScript. Revisa y adapta el código generado
para que funcione con tu API.

El frontend debe permitir:

- Registrarse, iniciar sesión y cerrar sesión.
- Guardar el JWT y enviarlo en las peticiones a rutas protegidas.
- Crear y consultar incidencias, mostrando las que correspondan al usuario.
- Consultar cualquier incidencia y cambiar su estado si el usuario tiene un
  rol autorizado.
- Eliminar incidencias y modificar los roles de otros usuarios si el usuario
  es administrador.
- Mostrar los errores de autenticación, permisos y otras respuestas de la API.

Las restricciones del frontend no sustituyen las comprobaciones del backend.

## Uso de la inteligencia artificial

Se permite utilizar herramientas de inteligencia artificial durante el
desarrollo, pero debes **entender y ser capaz de explicar todo el código
del backend generado** que incorpores al proyecto. Revisa y adapta sus propuestas:
no utilices tecnologías, bibliotecas, técnicas o construcciones que no se hayan
visto en clase y, si lo haces, explica brevemente qué hacen y cómo se utilizan.

Configura un archivo `AGENTS.md` o `CLAUDE.md` adecuado para el proyecto y la
herramienta utilizada. Incluye en él las tecnologías y convenciones del
proyecto y las restricciones sobre lo visto en clase.

Indica en el `README.md` qué herramienta de IA has utilizado y si se trata de
un chat o de una herramienta orientada al desarrollo o integrada en el IDE.
Se recomienda utilizar herramientas de desarrollo como GitHub Copilot o
Claude Code.

## Comprobaciones

Se recomiendan las siguientes comprobaciones para verificar el funcionamiento
de la aplicación.

Crea al menos dos usuarios básicos, un técnico y un administrador. Para
preparar los perfiles iniciales, asigna el primer `ROLE_ADMIN` directamente
en la base de datos de desarrollo y usa después la funcionalidad de
administración para asignar el rol de técnico. Comprueba que:

- Sin token, no se puede acceder a rutas protegidas.
- Un usuario puede crear y consultar sus incidencias.
- Ese mismo usuario no ve las incidencias ajenas en su listado ni puede
  consultarlas cambiando la URL.
- Un técnico puede consultar incidencias de ambos usuarios y cambiar su estado.
- Un usuario básico no puede cambiar el estado.
- Solo un administrador puede eliminar una incidencia.
- Solo un administrador puede consultar usuarios y cambiar sus roles, incluso
  si otro usuario llama directamente a la API.
- Un token inválido o caducado es rechazado.
- Un token válido sigue funcionando tras reiniciar la aplicación si se
  mantiene la misma clave de firma.
- El frontend permite realizar las operaciones correspondientes a cada rol y
  muestra los errores cuando una operación es rechazada.

Puedes pedir a una IA que realice las comprobaciones por consola o utilizando
un navegador, por ejemplo con Claude.

## Para pensar

Si un administrador cambia el rol de un usuario mientras este conserva un
JWT válido, ¿qué permisos tendrá ese usuario hasta que vuelva a iniciar
sesión? ¿Por qué?

Investiga y compara posibles soluciones para que el cambio de permisos se
aplique de forma inmediata **sin obligar al usuario a volver a iniciar
sesión**. Explica las ventajas y los inconvenientes de cada alternativa y
propón cuál utilizarías.

## Entrega

Entrega los enlaces a:

- Un **repositorio de GitHub** con el código, el frontend, el `README.md` y
  el archivo `AGENTS.md` o `CLAUDE.md`. Se valorará que el repositorio tenga
  varios commits que permitan seguir el progreso real de la aplicación.
- Un **vídeo de YouTube en modo oculto** en el que expliques brevemente el
  código, muestres el frontend en funcionamiento y respondas a las tres
  preguntas siguientes.

  1. Explica con un ejemplo de tu código la diferencia entre una respuesta
     `401` y una `403`. Provoca en el vídeo mediante Postman las dos
     respuestas y enseña en qué parte del código se produce la respuesta.
  2. ¿Cómo impide tu backend que un usuario consulte una incidencia ajena
     cambiando el identificador de la URL? ¿Cómo asigna el autor al crearla?
  3. ¿Qué ocurre si se cambia el rol de un usuario que ya tiene un token
     válido? Explica una solución para aplicar el cambio inmediatamente sin
     obligarle a volver a iniciar sesión y sus posibles inconvenientes.

### Contenido del README.md

Incluye en el `README.md`:

- Un resumen de la aplicación y sus funcionalidades.
- Los requisitos, la instalación, la configuración y los pasos para ejecutar
  el backend y el frontend.
- Los endpoints principales, sus métodos y los permisos necesarios.
- La herramienta de IA utilizada y su tipo, según se indica en el apartado
  sobre el uso de inteligencia artificial.
- Los principales prompts **reales** utilizados con la IA, reproducidos
  fielmente; no inventes prompts para la documentación.
- Los problemas encontrados, cómo los resolviste y las decisiones técnicas
  tomadas, incluyendo los motivos de esas decisiones.
