async function peticion(ruta, opciones = {}) {
  const respuesta = await fetch(API_URL + ruta, {
    headers: { "Content-Type": "application/json" },
    ...opciones,
  });
  if (!respuesta.ok) {
    throw new Error(`Error ${respuesta.status} en ${opciones.method || "GET"} ${ruta}`);
  }
  return respuesta.status === 204 ? null : respuesta.json();
}

const enviar = (metodo, ruta, cuerpo) =>
  peticion(ruta, { method: metodo, body: JSON.stringify(cuerpo) });

const api = {
  alumnos: {
    listar: () => peticion("/alumnos"),
    crear: (a) => enviar("POST", "/alumnos", a),
    actualizar: (id, a) => enviar("PUT", `/alumnos/${id}`, a),
    borrar: (id) => peticion(`/alumnos/${id}`, { method: "DELETE" }),
    matriculas: (id) => peticion(`/alumnos/${id}/matriculas`),
  },
  cursos: {
    listar: () => peticion("/cursos"),
    crear: (c) => enviar("POST", "/cursos", c),
    actualizar: (id, c) => enviar("PUT", `/cursos/${id}`, c),
    borrar: (id) => peticion(`/cursos/${id}`, { method: "DELETE" }),
    matriculas: (id) => peticion(`/cursos/${id}/matriculas`),
  },
  matriculas: {
    listar: () => peticion("/matriculas"),
    crear: (m) => enviar("POST", "/matriculas", m),
    borrar: (id) => peticion(`/matriculas/${id}`, { method: "DELETE" }),
  },
};
