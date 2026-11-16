const CLAVE_TOKEN = "matriculas_token";

const sesion = {
  token: () => localStorage.getItem(CLAVE_TOKEN),
  guardar: (token) => localStorage.setItem(CLAVE_TOKEN, token),
  cerrar: () => localStorage.removeItem(CLAVE_TOKEN),
};

async function peticion(ruta, opciones = {}, publica = false) {
  const token = publica ? null : sesion.token();
  if (!publica && !token) {
    window.dispatchEvent(new Event("sesion-caducada"));
    throw new Error("Inicia sesión para continuar");
  }
  const headers = new Headers(opciones.headers);
  if (opciones.body) headers.set("Content-Type", "application/json");
  if (token) headers.set("Authorization", `Bearer ${token}`);
  const respuesta = await fetch(API_URL + ruta, { ...opciones, headers });
  if (!respuesta.ok) {
    if (!publica && respuesta.status === 401 && sesion.token() === token) {
      sesion.cerrar();
      window.dispatchEvent(new Event("sesion-caducada"));
      throw new Error("La sesión ha caducado. Inicia sesión de nuevo");
    }
    let detalle;
    if (respuesta.headers.get("content-type")?.includes("application/json")) {
      detalle = await respuesta.json();
    }
    const mensaje = detalle && typeof detalle === "object"
      ? Object.values(detalle).filter((valor) => typeof valor === "string").join(". ")
      : "";
    throw new Error(mensaje || `Error ${respuesta.status} en ${opciones.method || "GET"} ${ruta}`);
  }
  return respuesta.status === 204 ? null : respuesta.json();
}

const enviar = (metodo, ruta, cuerpo, publica = false) =>
  peticion(ruta, { method: metodo, body: JSON.stringify(cuerpo) }, publica);

const api = {
  auth: {
    login: (datos) => enviar("POST", "/auth/login", datos, true),
    registro: (datos) => enviar("POST", "/auth/register", datos, true),
  },
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
