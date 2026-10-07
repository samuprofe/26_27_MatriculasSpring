const NIVELES = ["ESO", "BACHILLERATO", "CFGS", "CFGM", "CFGB"];

const CAMPOS_ALUMNO = [
  { name: "nombre", label: "Nombre", required: true },
  { name: "apellidos", label: "Apellidos", required: true },
  { name: "email", label: "Email", type: "email" },
  { name: "fechaNacimiento", label: "Fecha de nacimiento", type: "date" },
  { name: "dni", label: "DNI", maxlength: 10 },
  { name: "telefono", label: "Teléfono", maxlength: 15 },
  { name: "importeBeca", label: "Importe de la beca (€)", type: "number", step: "0.01", min: "0" },
];

const CAMPOS_CURSO = [
  { name: "nombre", label: "Nombre", required: true },
  { name: "abreviatura", label: "Abreviatura", required: true },
  { name: "nivel", label: "Nivel", options: NIVELES, required: true },
];

let alumnos = [];
let cursos = [];

const $ = (id) => document.getElementById(id);

function el(etiqueta, props = {}, ...hijos) {
  const e = Object.assign(document.createElement(etiqueta), props);
  e.append(...hijos);
  return e;
}

function mostrarMensaje(texto, error = false) {
  const m = $("mensaje");
  m.textContent = texto;
  m.className = "mensaje" + (error ? " error" : "");
  clearTimeout(mostrarMensaje.t);
  mostrarMensaje.t = setTimeout(() => m.classList.add("oculto"), 4000);
}

async function ejecutar(accion, textoOk) {
  try {
    await accion();
    if (textoOk) mostrarMensaje(textoOk);
  } catch (e) {
    mostrarMensaje(e.message, true);
  }
}

// El backend serializa el campo DNI como "dni"; se acepta también "DNI" por si cambia el mapeo.
const dniDe = (a) => a.dni ?? a.DNI ?? "";

/* ---------- Diálogo genérico de formulario ---------- */
function abrirFormulario(titulo, campos, valores, alGuardar) {
  $("dialogo-titulo").textContent = titulo;
  const cont = $("dialogo-campos");
  cont.replaceChildren();
  for (const c of campos) {
    const { label, options, ...atributos } = c;
    let control;
    if (options) {
      control = el("select", { name: c.name, required: !!c.required },
        el("option", { value: "", textContent: "-- Selecciona --" }),
        ...options.map((o) => el("option", { value: o, textContent: o })));
    } else {
      control = el("input", atributos);
    }
    control.value = valores[c.name] ?? "";
    cont.append(el("label", {}, label, control));
  }
  $("form-dialogo").onsubmit = (ev) => {
    ev.preventDefault();
    const datos = Object.fromEntries(new FormData(ev.target));
    $("dialogo").close();
    alGuardar(datos);
  };
  $("dialogo").showModal();
}

$("dialogo-cancelar").onclick = () => $("dialogo").close();

/* ---------- Pestañas ---------- */
document.querySelectorAll(".tab").forEach((boton) => {
  boton.onclick = () => {
    document.querySelectorAll(".tab").forEach((b) => b.classList.toggle("active", b === boton));
    document.querySelectorAll(".panel").forEach((p) =>
      p.classList.toggle("oculto", p.id !== boton.dataset.tab));
    if (boton.dataset.tab === "matriculas") cargarMatriculas();
  };
});

/* ---------- Alumnos ---------- */
async function cargarAlumnos() {
  alumnos = await api.alumnos.listar();
  const tbody = $("tabla-alumnos");
  tbody.replaceChildren();
  if (!alumnos.length) tbody.append(filaVacia(8, "No hay alumnos"));
  for (const a of alumnos) {
    tbody.append(el("tr", {},
      el("td", { textContent: a.nombre }),
      el("td", { textContent: a.apellidos }),
      el("td", { textContent: a.email ?? "" }),
      el("td", { textContent: a.fechaNacimiento ?? "" }),
      el("td", { textContent: dniDe(a) }),
      el("td", { textContent: a.telefono ?? "" }),
      el("td", { textContent: a.importeBeca ?? "" }),
      el("td", { className: "acciones" },
        el("button", { textContent: "Editar", onclick: () => editarAlumno(a) }),
        el("button", { textContent: "Borrar", className: "peligro", onclick: () => borrarAlumno(a) }))));
  }
}

function datosAlumno(d) {
  return {
    nombre: d.nombre,
    apellidos: d.apellidos,
    email: d.email || null,
    fechaNacimiento: d.fechaNacimiento || null,
    dni: d.dni || null,
    DNI: d.dni || null,
    telefono: d.telefono || null,
    importeBeca: d.importeBeca === "" ? null : Number(d.importeBeca),
  };
}

function editarAlumno(a) {
  abrirFormulario("Editar alumno", CAMPOS_ALUMNO, { ...a, dni: dniDe(a) }, (d) =>
    ejecutar(async () => {
      await api.alumnos.actualizar(a.id, datosAlumno(d));
      await cargarAlumnos();
    }, "Alumno actualizado"));
}

$("nuevo-alumno").onclick = () =>
  abrirFormulario("Nuevo alumno", CAMPOS_ALUMNO, {}, (d) =>
    ejecutar(async () => {
      await api.alumnos.crear(datosAlumno(d));
      await cargarAlumnos();
    }, "Alumno creado"));

function borrarAlumno(a) {
  if (!confirm(`¿Borrar a ${a.nombre} ${a.apellidos}? También se borrarán sus matrículas.`)) return;
  ejecutar(async () => {
    await api.alumnos.borrar(a.id);
    await cargarAlumnos();
  }, "Alumno borrado");
}

/* ---------- Cursos ---------- */
async function cargarCursos() {
  cursos = await api.cursos.listar();
  const tbody = $("tabla-cursos");
  tbody.replaceChildren();
  if (!cursos.length) tbody.append(filaVacia(4, "No hay cursos"));
  for (const c of cursos) {
    tbody.append(el("tr", {},
      el("td", { textContent: c.nombre }),
      el("td", { textContent: c.abreviatura ?? "" }),
      el("td", { textContent: c.nivel ?? "" }),
      el("td", { className: "acciones" },
        el("button", { textContent: "Editar", onclick: () => editarCurso(c) }),
        el("button", { textContent: "Borrar", className: "peligro", onclick: () => borrarCurso(c) }))));
  }
}

const datosCurso = (d) => ({ nombre: d.nombre, abreviatura: d.abreviatura, nivel: d.nivel });

function editarCurso(c) {
  abrirFormulario("Editar curso", CAMPOS_CURSO, c, (d) =>
    ejecutar(async () => {
      await api.cursos.actualizar(c.id, datosCurso(d));
      await cargarCursos();
    }, "Curso actualizado"));
}

$("nuevo-curso").onclick = () =>
  abrirFormulario("Nuevo curso", CAMPOS_CURSO, {}, (d) =>
    ejecutar(async () => {
      await api.cursos.crear(datosCurso(d));
      await cargarCursos();
    }, "Curso creado"));

function borrarCurso(c) {
  if (!confirm(`¿Borrar el curso ${c.nombre}? También se borrarán sus matrículas.`)) return;
  ejecutar(async () => {
    await api.cursos.borrar(c.id);
    await cargarCursos();
  }, "Curso borrado");
}

/* ---------- Matrículas ---------- */
const nombreAlumno = (a) => `${a.nombre} ${a.apellidos}`;
const nombreCurso = (c) => c.abreviatura ? `${c.abreviatura} - ${c.nombre}` : c.nombre;

function rellenarSelect(select, items, texto, valorInicial = "") {
  select.replaceChildren(
    el("option", { value: "", textContent: "-- Selecciona --" }),
    ...items.map((i) => el("option", { value: i.id, textContent: texto(i) })));
  select.value = valorInicial;
}

function actualizarFiltroValor() {
  const tipo = $("filtro-tipo").value;
  const select = $("filtro-valor");
  select.classList.toggle("oculto", tipo === "todas");
  if (tipo === "curso") rellenarSelect(select, cursos, nombreCurso);
  if (tipo === "alumno") rellenarSelect(select, alumnos, nombreAlumno);
}

async function cargarMatriculas() {
  await ejecutar(async () => {
    await Promise.all([cargarAlumnos(), cargarCursos()]);
    const form = $("form-matricula");
    rellenarSelect(form.alumno, alumnos, nombreAlumno, form.alumno.value);
    rellenarSelect(form.curso, cursos, nombreCurso, form.curso.value);

    const tipo = $("filtro-tipo").value;
    const valor = $("filtro-valor").value;
    let lista = [];
    if (tipo === "todas") lista = await api.matriculas.listar();
    else if (valor) lista = await (tipo === "curso" ? api.cursos : api.alumnos).matriculas(valor);
    pintarMatriculas(lista);
  });
}

function pintarMatriculas(lista) {
  const tbody = $("tabla-matriculas");
  tbody.replaceChildren();
  if (!lista.length) tbody.append(filaVacia(5, "No hay matrículas"));
  for (const m of lista) {
    tbody.append(el("tr", {},
      el("td", { textContent: m.alumno ? nombreAlumno(m.alumno) : "" }),
      el("td", { textContent: m.curso ? nombreCurso(m.curso) : "" }),
      el("td", { textContent: m.cursoLectivo ?? "" }),
      el("td", { textContent: m.pagoSeguro ? "Sí" : "No" }),
      el("td", { className: "acciones" },
        el("button", { textContent: "Desmatricular", className: "peligro", onclick: () => desmatricular(m) }))));
  }
}

function desmatricular(m) {
  if (!confirm(`¿Quitar a ${nombreAlumno(m.alumno)} del curso ${nombreCurso(m.curso)}?`)) return;
  ejecutar(async () => {
    await api.matriculas.borrar(m.id);
    await cargarMatriculas();
  }, "Alumno desmatriculado");
}

$("form-matricula").onsubmit = (ev) => {
  ev.preventDefault();
  const f = ev.target;
  ejecutar(async () => {
    await api.matriculas.crear({
      alumno: { id: Number(f.alumno.value) },
      curso: { id: Number(f.curso.value) },
      cursoLectivo: f.cursoLectivo.value,
      pagoSeguro: f.pagoSeguro.checked,
    });
    f.cursoLectivo.value = "";
    f.pagoSeguro.checked = false;
    await cargarMatriculas();
  }, "Alumno matriculado");
};

$("filtro-tipo").onchange = () => {
  actualizarFiltroValor();
  cargarMatriculas();
};
$("filtro-valor").onchange = cargarMatriculas;

function filaVacia(columnas, texto) {
  return el("tr", {}, el("td", { colSpan: columnas, className: "vacio", textContent: texto }));
}

/* ---------- Inicio ---------- */
ejecutar(async () => {
  await Promise.all([cargarAlumnos(), cargarCursos()]);
});
