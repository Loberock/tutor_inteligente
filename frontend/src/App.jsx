import {
  BarChart3,
  BookOpen,
  CheckCircle2,
  ClipboardList,
  KeyRound,
  LogOut,
  Monitor,
  Plus,
  School,
  Search,
  Send,
  Sparkles,
  UserRound,
  Wifi,
} from "lucide-react";
import { useCallback, useEffect, useMemo, useState } from "react";
import "./App.css";

const API_BASE = import.meta.env.VITE_API_BASE_URL || "";
const SESSION_KEY = "tutor-session";

const roleLabels = {
  ALUMNO: "Alumno",
  PROFESOR: "Docente",
};

const difficultyLabels = {
  BASICO: "Basico",
  INTERMEDIO: "Intermedio",
  AVANZADO: "Avanzado",
};

const initialSession = () => {
  const raw = localStorage.getItem(SESSION_KEY);
  return raw ? JSON.parse(raw) : null;
};

async function apiRequest(path, { method = "GET", body, token } = {}) {
  const response = await fetch(`${API_BASE}${path}`, {
    method,
    headers: {
      ...(body ? { "Content-Type": "application/json" } : {}),
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
    body: body ? JSON.stringify(body) : undefined,
  });

  const text = await response.text();
  const data = text ? JSON.parse(text) : null;

  if (!response.ok) {
    const details = Array.isArray(data?.detalles) ? data.detalles.join(". ") : "";
    throw new Error(details || data?.mensaje || "No se pudo completar la operacion");
  }

  return data;
}

function App() {
  const [session, setSession] = useState(initialSession);

  const saveSession = (nextSession) => {
    setSession(nextSession);
    localStorage.setItem(SESSION_KEY, JSON.stringify(nextSession));
  };

  const logout = () => {
    setSession(null);
    localStorage.removeItem(SESSION_KEY);
  };

  return (
    <main className="app-shell">
      {!session ? (
        <AuthScreen onLogin={saveSession} />
      ) : (
        <Workspace session={session} onLogout={logout} />
      )}
    </main>
  );
}

function AppHeader({ session, onLogout }) {
  return (
    <header className="app-header">
      <div className="product-title">
        <div className="robot-mark">
          <Monitor size={24} />
        </div>
        <div>
          <span>Matematica adaptativa</span>
          <strong>Tutor inteligente</strong>
        </div>
      </div>
      <div className="header-actions">
        {session && <Pill>{roleLabels[session.tipo] || session.tipo}</Pill>}
        <span className="online-badge">
          <Wifi size={16} />
          En linea
        </span>
        {session && (
          <button className="icon-button" onClick={onLogout} title="Cerrar sesion">
            <LogOut size={17} />
            Cerrar sesion
          </button>
        )}
      </div>
    </header>
  );
}

function AuthScreen({ onLogin }) {
  const [mode, setMode] = useState("login");
  const [role, setRole] = useState("ALUMNO");

  return (
    <>
      <AppHeader />
      <section className="auth-layout">
        <aside className="auth-preview" aria-hidden="true">
          <MathIllustration />
          <div className="preview-note">
            <strong>Acceso seguro por rol</strong>
            <span>Alumno y docente entran a paneles distintos</span>
          </div>
        </aside>

        <section className="auth-card">
          <p className="eyebrow">Inicio de sesion</p>
          <h1>Tutor Inteligente</h1>
          <p className="auth-copy">
            Ingresa como alumno o docente para continuar con la practica y los reportes.
          </p>

          <SegmentedControl
            value={mode}
            options={[
              { value: "login", label: "Ingresar" },
              { value: "register", label: "Registrarse" },
            ]}
            onChange={setMode}
          />

          <div className="role-switch">
            <button className={role === "ALUMNO" ? "active" : ""} onClick={() => setRole("ALUMNO")}>
              <School size={18} />
              Alumno
            </button>
            <button className={role === "PROFESOR" ? "active" : ""} onClick={() => setRole("PROFESOR")}>
              <Monitor size={18} />
              Docente
            </button>
          </div>

          {mode === "login" ? (
            <LoginForm onLogin={onLogin} />
          ) : (
            <RegisterForm role={role} onDone={() => setMode("login")} />
          )}
        </section>
      </section>
    </>
  );
}

function MathIllustration() {
  return (
    <div className="math-illustration">
      <span className="math-symbol plus">+</span>
      <span className="math-symbol question">?</span>
      <span className="math-symbol minus">-</span>
      <span className="math-symbol equals">=</span>
      <span className="math-symbol add">+</span>
      <div className="screen-card">
        <div className="chart-line" />
        <div className="screen-lines">
          <span />
          <span />
          <span />
        </div>
        <div className="screen-tile yellow">+</div>
        <div className="screen-tile blue">+</div>
      </div>
    </div>
  );
}

function LoginForm({ onLogin }) {
  const [form, setForm] = useState({ correo: "", contrasena: "" });
  const [status, setStatus] = useState({ type: "", message: "" });

  const submit = async (event) => {
    event.preventDefault();
    setStatus({ type: "loading", message: "Validando credenciales" });
    try {
      const data = await apiRequest("/v1/sesiones", {
        method: "POST",
        body: form,
      });
      onLogin(data);
    } catch (error) {
      setStatus({ type: "error", message: error.message });
    }
  };

  return (
    <form className="stack-form" onSubmit={submit}>
      <Field
        label="Correo"
        type="email"
        value={form.correo}
        placeholder="alumno@demo.pe"
        onChange={(correo) => setForm({ ...form, correo })}
      />
      <Field
        label="Contrasena"
        type="password"
        value={form.contrasena}
        placeholder="Minimo 6 caracteres"
        onChange={(contrasena) => setForm({ ...form, contrasena })}
      />
      <SubmitButton icon={<KeyRound size={18} />} label="Ingresar" />
      <StatusMessage status={status} />
    </form>
  );
}

function RegisterForm({ role, onDone }) {
  const [form, setForm] = useState({
    nombre: "",
    apellido: "",
    correo: "",
    contrasena: "",
    grado: "1",
  });
  const [status, setStatus] = useState({ type: "", message: "" });

  const submit = async (event) => {
    event.preventDefault();
    setStatus({ type: "loading", message: "Creando cuenta" });
    try {
      await apiRequest("/v1/usuarios", {
        method: "POST",
        body: {
          ...form,
          tipo: role === "PROFESOR" ? "PROFESOR" : undefined,
          grado: role === "ALUMNO" ? form.grado : undefined,
        },
      });
      setStatus({ type: "success", message: "Cuenta creada. Ya puedes iniciar sesion" });
      setTimeout(onDone, 800);
    } catch (error) {
      setStatus({ type: "error", message: error.message });
    }
  };

  return (
    <form className="stack-form" onSubmit={submit}>
      <div className="form-grid two">
        <Field label="Nombre" value={form.nombre} placeholder="Ana" onChange={(nombre) => setForm({ ...form, nombre })} />
        <Field label="Apellido" value={form.apellido} placeholder="Torres" onChange={(apellido) => setForm({ ...form, apellido })} />
      </div>
      <Field
        label="Correo"
        type="email"
        value={form.correo}
        placeholder="correo@institucion.pe"
        onChange={(correo) => setForm({ ...form, correo })}
      />
      <Field
        label="Contrasena"
        type="password"
        value={form.contrasena}
        placeholder="Minimo 6 caracteres"
        onChange={(contrasena) => setForm({ ...form, contrasena })}
      />
      {role === "ALUMNO" && (
        <Select label="Grado" value={form.grado} onChange={(grado) => setForm({ ...form, grado })}>
          <option value="1">1. secundaria</option>
          <option value="2">2. secundaria</option>
          <option value="3">3. secundaria</option>
          <option value="4">4. secundaria</option>
          <option value="5">5. secundaria</option>
        </Select>
      )}
      <SubmitButton icon={<Plus size={18} />} label="Registrarse" />
      <StatusMessage status={status} />
    </form>
  );
}

function Workspace({ session, onLogout }) {
  const isTeacher = session.tipo === "PROFESOR";

  return (
    <>
      <AppHeader session={session} onLogout={onLogout} />
      <section className="workspace">
        <div className="workspace-heading">
          <div>
            <p className="eyebrow">{isTeacher ? "Modo docente" : "Modo estudiante"}</p>
            <h1>{isTeacher ? "Banco de preguntas y reportes" : "Practica adaptativa"}</h1>
          </div>
          <div className="heading-pills">
            <Pill>{session.nombre}</Pill>
            {!isTeacher && <Pill>{difficultyLabels.BASICO}</Pill>}
          </div>
        </div>
        {isTeacher ? <TeacherDashboard session={session} /> : <StudentDashboard session={session} />}
      </section>
    </>
  );
}

function TeacherDashboard({ session }) {
  const [refreshKey, setRefreshKey] = useState(0);

  return (
    <div className="teacher-layout">
      <div className="teacher-sidebar">
        <QuestionsPanel session={session} refreshKey={refreshKey} />
        <CoursesPanel token={session.token} refreshKey={refreshKey} onChanged={() => setRefreshKey((key) => key + 1)} />
      </div>
      <ReportsPanel token={session.token} />
    </div>
  );
}

function StudentDashboard({ session }) {
  const [summary, setSummary] = useState({
    answered: 0,
    total: 0,
    progress: 0,
    result: null,
    currentLevel: "BASICO",
  });

  return (
    <div className="student-layout">
      <StudentProfilePanel session={session} />
      <StudentEvaluationPanel session={session} onSummaryChange={setSummary} />
      <StudentSummary session={session} summary={summary} />
    </div>
  );
}

function CoursesPanel({ token, refreshKey, onChanged }) {
  const [courses, setCourses] = useState([]);
  const [nombreCurso, setNombreCurso] = useState("");
  const [status, setStatus] = useState({ type: "", message: "" });

  useEffect(() => {
    apiRequest("/v1/cursos", { token })
      .then(setCourses)
      .catch((error) => setStatus({ type: "error", message: error.message }));
  }, [token, refreshKey]);

  const createCourse = async (event) => {
    event.preventDefault();
    try {
      await apiRequest("/v1/cursos", {
        method: "POST",
        token,
        body: { nombreCurso },
      });
      setNombreCurso("");
      setStatus({ type: "success", message: "Curso creado" });
      onChanged();
    } catch (error) {
      setStatus({ type: "error", message: error.message });
    }
  };

  return (
    <Panel icon={<BookOpen size={19} />} title="Cursos" aside={`${courses.length} activos`}>
      <form className="inline-form" onSubmit={createCourse}>
        <input value={nombreCurso} onChange={(event) => setNombreCurso(event.target.value)} placeholder="Nuevo curso" />
        <button className="icon-button filled" title="Crear curso">
          <Plus size={18} />
        </button>
      </form>
      <div className="list compact">
        {courses.map((course) => (
          <div className="list-row" key={course.cursoId}>
            <span>{course.nombreCurso}</span>
            <small>#{course.cursoId}</small>
          </div>
        ))}
      </div>
      <StatusMessage status={status} />
    </Panel>
  );
}

function QuestionsPanel({ session, refreshKey }) {
  const [courses, setCourses] = useState([]);
  const [questions, setQuestions] = useState([]);
  const [form, setForm] = useState({
    cursoId: "",
    contenidoPregunta: "",
    grado: "1",
    dificultad: "BASICO",
    opcionA: "",
    opcionB: "",
    opcionC: "",
    opcionD: "",
    respuestaCorrecta: "A",
    refuerzo: "",
  });
  const [status, setStatus] = useState({ type: "", message: "" });

  useEffect(() => {
    apiRequest("/v1/cursos", { token: session.token })
      .then(setCourses)
      .catch(() => {});
  }, [session.token, refreshKey]);

  const loadQuestions = async () => {
    try {
      const query = form.cursoId ? `?cursoId=${form.cursoId}&grado=${form.grado}&dificultad=${form.dificultad}` : "";
      setQuestions(await apiRequest(`/v1/preguntas${query}`, { token: session.token }));
      setStatus({ type: "", message: "" });
    } catch (error) {
      setStatus({ type: "error", message: error.message });
    }
  };

  const createQuestion = async (event) => {
    event.preventDefault();
    try {
      await apiRequest("/v1/preguntas", {
        method: "POST",
        token: session.token,
        body: {
          ...form,
          profesorId: Number(session.perfilId),
          cursoId: Number(form.cursoId),
        },
      });
      setForm({
        ...form,
        contenidoPregunta: "",
        opcionA: "",
        opcionB: "",
        opcionC: "",
        opcionD: "",
        refuerzo: "",
      });
      setStatus({ type: "success", message: "Pregunta creada" });
      loadQuestions();
    } catch (error) {
      setStatus({ type: "error", message: error.message });
    }
  };

  return (
    <Panel icon={<ClipboardList size={19} />} title="Registrar pregunta" aside="Banco docente">
      <form className="stack-form dense" onSubmit={createQuestion}>
        <Field
          label="Pregunta"
          value={form.contenidoPregunta}
          placeholder="Ejemplo: Si 6x + 2 = 32, cuanto es x?"
          onChange={(contenidoPregunta) => setForm({ ...form, contenidoPregunta })}
        />
        <div className="form-grid three">
          <Select label="Curso" value={form.cursoId} onChange={(cursoId) => setForm({ ...form, cursoId })}>
            <option value="">Seleccionar</option>
            {courses.map((course) => (
              <option key={course.cursoId} value={course.cursoId}>
                {course.nombreCurso}
              </option>
            ))}
          </Select>
          <Select label="Grado" value={form.grado} onChange={(grado) => setForm({ ...form, grado })}>
            <option value="1">1. secundaria</option>
            <option value="2">2. secundaria</option>
            <option value="3">3. secundaria</option>
            <option value="4">4. secundaria</option>
            <option value="5">5. secundaria</option>
          </Select>
          <Select label="Dificultad" value={form.dificultad} onChange={(dificultad) => setForm({ ...form, dificultad })}>
            <option value="BASICO">Basico</option>
            <option value="INTERMEDIO">Intermedio</option>
            <option value="AVANZADO">Avanzado</option>
          </Select>
        </div>
        <div className="form-grid two">
          {["A", "B", "C", "D"].map((letter) => (
            <Field
              key={letter}
              label={`Opcion ${letter}`}
              value={form[`opcion${letter}`]}
              onChange={(value) => setForm({ ...form, [`opcion${letter}`]: value })}
            />
          ))}
        </div>
        <div className="form-grid two">
          <Select
            label="Respuesta correcta"
            value={form.respuestaCorrecta}
            onChange={(respuestaCorrecta) => setForm({ ...form, respuestaCorrecta })}
          >
            <option>A</option>
            <option>B</option>
            <option>C</option>
            <option>D</option>
          </Select>
          <Field label="Refuerzo" value={form.refuerzo} placeholder="Pista breve para el estudiante" onChange={(refuerzo) => setForm({ ...form, refuerzo })} />
        </div>
        <div className="button-row">
          <SubmitButton icon={<Plus size={18} />} label="Guardar pregunta" />
          <button type="button" className="secondary-button" onClick={loadQuestions}>
            <Search size={18} />
            Ver banco
          </button>
        </div>
      </form>
      <div className="list">
        {questions.length === 0 ? (
          <EmptyState title="Sin preguntas cargadas" text="Usa los filtros y guarda nuevas preguntas para este curso." />
        ) : (
          questions.slice(0, 5).map((question) => (
            <div className="list-row question-row" key={question.preguntaId}>
              <span>{question.contenidoPregunta}</span>
              <small>{question.dificultad}</small>
            </div>
          ))
        )}
      </div>
      <StatusMessage status={status} />
    </Panel>
  );
}

function ReportsPanel({ token }) {
  const [reports, setReports] = useState([]);
  const [status, setStatus] = useState({ type: "", message: "" });

  const loadReports = useCallback(
    async (onlySupport = false) => {
      try {
        const path = onlySupport ? "/v1/reportes/refuerzos" : "/v1/reportes/rendimiento";
        setReports(await apiRequest(path, { token }));
        setStatus({ type: "", message: "" });
      } catch (error) {
        setStatus({ type: "error", message: error.message });
      }
    },
    [token],
  );

  useEffect(() => {
    loadReports();
  }, [loadReports]);

  const reportRows = reports.slice(0, 6);

  return (
    <Panel icon={<BarChart3 size={19} />} title="Reporte automatico" aside={`${reports.length} registros`}>
      <div className="button-row">
        <button className="secondary-button" onClick={() => loadReports(false)}>
          Rendimiento
        </button>
        <button className="secondary-button" onClick={() => loadReports(true)}>
          Refuerzos
        </button>
      </div>
      <div className="report-bars">
        {reportRows.slice(0, 4).map((report, index) => (
          <div className="report-bar" key={`${report.alumnoId}-${report.cursoId}-${index}`}>
            <span>{report.curso || report.temaCritico || "Curso"}</span>
            <div>
              <i style={{ width: `${Math.max(Number(report.porcentajePromedio || report.rendimiento || 0), 8)}%` }} />
            </div>
            <strong>{Number(report.porcentajePromedio || report.rendimiento || 0).toFixed(0)}%</strong>
          </div>
        ))}
      </div>
      <div className="table-card">
        <div className="table-row table-head">
          <span>Alumno</span>
          <span>Rendimiento</span>
          <span>Estado</span>
        </div>
        {reportRows.length === 0 ? (
          <EmptyState title="Sin reportes todavia" text="Los reportes apareceran cuando existan evaluaciones." />
        ) : (
          reportRows.map((report) => (
            <div className="table-row" key={`${report.alumnoId}-${report.cursoId}`}>
              <span>{report.nombreCompleto}</span>
              <span>{Number(report.porcentajePromedio || report.rendimiento || 0).toFixed(0)}%</span>
              <span>{report.estado}</span>
            </div>
          ))
        )}
      </div>
      <StatusMessage status={status} />
    </Panel>
  );
}

function StudentProfilePanel({ session }) {
  return (
    <Panel icon={<UserRound size={19} />} title="Estudiante" aside="Sesion">
      <div className="field-readonly">{session.nombre}</div>
      <div className="profile-mini-grid">
        <Stat label="Rol" value={roleLabels[session.tipo] || session.tipo} />
        <Stat label="Token" value="Activo" />
      </div>
    </Panel>
  );
}

function StudentEvaluationPanel({ session, onSummaryChange }) {
  const [courses, setCourses] = useState([]);
  const [config, setConfig] = useState({
    cursoId: "",
    grado: "1",
    nivel: "BASICO",
    cantidad: 5,
  });
  const [exercises, setExercises] = useState([]);
  const [answers, setAnswers] = useState({});
  const [result, setResult] = useState(null);
  const [status, setStatus] = useState({ type: "", message: "" });
  const answeredCount = Object.values(answers).filter(Boolean).length;
  const progress = result
    ? Math.round(result.porcentaje)
    : exercises.length
      ? Math.round((answeredCount / exercises.length) * 100)
      : 0;
  const canSubmit = exercises.length > 0 && answeredCount === exercises.length && !result;

  useEffect(() => {
    apiRequest("/v1/cursos", { token: session.token })
      .then(setCourses)
      .catch(() => {});
  }, [session.token]);

  useEffect(() => {
    onSummaryChange({
      answered: answeredCount,
      total: exercises.length,
      progress,
      result,
      currentLevel: result?.nivelAsignado || config.nivel,
    });
  }, [answeredCount, config.nivel, exercises.length, onSummaryChange, progress, result]);

  const loadDiagnostic = async () => {
    try {
      if (!config.cursoId) {
        setStatus({ type: "error", message: "Selecciona un curso para cargar ejercicios" });
        return;
      }
      const query = `cursoId=${config.cursoId}&grado=${config.grado}&nivel=${config.nivel}&cantidad=${config.cantidad}`;
      const data = await apiRequest(`/v1/evaluaciones/diagnostico?${query}`, { token: session.token });
      setExercises(data);
      setAnswers({});
      setResult(null);
      setStatus(
        data.length
          ? { type: "success", message: `${data.length} ejercicios cargados. Responde todas las preguntas antes de enviar.` }
          : { type: "error", message: "No hay preguntas para ese curso, grado y nivel. Prueba otro filtro o pide al docente que registre preguntas." },
      );
    } catch (error) {
      setStatus({ type: "error", message: error.message });
    }
  };

  const submitEvaluation = async () => {
    try {
      if (!canSubmit) {
        setStatus({ type: "error", message: "Completa todas las respuestas antes de enviar la evaluacion" });
        return;
      }

      const data = await apiRequest("/v1/evaluaciones", {
        method: "POST",
        token: session.token,
        body: {
          alumnoId: Number(session.perfilId),
          respuestas: exercises.map((exercise) => ({
            preguntaId: exercise.preguntaId,
            respuestaSeleccionada: answers[exercise.preguntaId] || "",
          })),
          cursos: [{ cursoId: Number(config.cursoId), nivel: config.nivel }],
        },
      });
      setResult(data);
      setConfig({ ...config, nivel: data.nivelAsignado || config.nivel });
      setStatus({ type: "success", message: "Evaluacion enviada. Revisa tu resultado y refuerzos." });
    } catch (error) {
      setStatus({ type: "error", message: error.message });
    }
  };

  const updateAnswer = (preguntaId, respuestaSeleccionada) => {
    if (result) return;
    setAnswers({ ...answers, [preguntaId]: respuestaSeleccionada });
  };

  return (
    <Panel icon={<ClipboardList size={19} />} title="Evaluacion diagnostica" aside={`${answeredCount}/${exercises.length || 0} respondidas`}>
      <div className="form-grid four">
        <Select label="Tema" value={config.cursoId} onChange={(cursoId) => setConfig({ ...config, cursoId })}>
          <option value="">Seleccionar</option>
          {courses.map((course) => (
            <option key={course.cursoId} value={course.cursoId}>
              {course.nombreCurso}
            </option>
          ))}
        </Select>
        <Select label="Grado" value={config.grado} onChange={(grado) => setConfig({ ...config, grado })}>
          <option value="1">1. secundaria</option>
          <option value="2">2. secundaria</option>
          <option value="3">3. secundaria</option>
          <option value="4">4. secundaria</option>
          <option value="5">5. secundaria</option>
        </Select>
        <Select label="Nivel" value={config.nivel} onChange={(nivel) => setConfig({ ...config, nivel })}>
          <option value="BASICO">Basico</option>
          <option value="INTERMEDIO">Intermedio</option>
          <option value="AVANZADO">Avanzado</option>
        </Select>
        <Field label="Cantidad" type="number" value={config.cantidad} onChange={(cantidad) => setConfig({ ...config, cantidad })} />
      </div>
      <div className="button-row">
        <button className="secondary-button" onClick={loadDiagnostic}>
          <Search size={18} />
          Cargar
        </button>
        <button className="primary-button" onClick={submitEvaluation} disabled={!canSubmit}>
          <Send size={18} />
          Enviar
        </button>
      </div>

      {exercises.length > 0 ? (
        <section className="evaluation-flow">
          <div className="exercise-topline">
            <span>{answeredCount} de {exercises.length} ejercicios respondidos</span>
            <Pill>{progress}% avance</Pill>
          </div>
          <div className="progress-track" aria-label="Progreso de evaluacion">
            <i style={{ width: `${progress}%` }} />
          </div>
          <div className="exercise-list">
            {exercises.map((exercise, index) => (
              <article className="exercise-card" key={exercise.preguntaId}>
                <div className="exercise-topline">
                  <span>Pregunta {index + 1}</span>
                  <Pill>{answers[exercise.preguntaId] ? "Respondida" : "Pendiente"}</Pill>
                </div>
                <h2>{exercise.contenidoPregunta}</h2>
                <div className="answer-grid">
                  {["A", "B", "C", "D"].map((letter) => (
                    <button
                      key={letter}
                      className={answers[exercise.preguntaId] === letter ? "answer active" : "answer"}
                      onClick={() => updateAnswer(exercise.preguntaId, letter)}
                      disabled={Boolean(result)}
                    >
                      <span>{letter}</span>
                      {exercise[`opcion${letter}`]}
                    </button>
                  ))}
                </div>
              </article>
            ))}
          </div>
        </section>
      ) : (
        <EmptyState title="Carga un diagnostico" text="Selecciona un curso y nivel para comenzar la practica." />
      )}

      {result && <ResultBox result={result} />}
      <StatusMessage status={status} />
    </Panel>
  );
}

function StudentSummary({ session, summary }) {
  const initials = useMemo(
    () =>
      session.nombre
        ?.split(" ")
        .map((part) => part[0])
        .join("")
        .slice(0, 2),
    [session.nombre],
  );

  return (
    <Panel icon={<Sparkles size={19} />} title="Progreso" aside="Resumen">
      <div className="progress-block">
        <div className="progress-ring">
          <span>{summary.progress}%</span>
        </div>
        <div className="mini-illustration">
          <Monitor size={30} />
        </div>
      </div>
      <div className="profile-block">
        <div className="avatar">{initials}</div>
        <div>
          <h2>{session.nombre}</h2>
          <p>{roleLabels[session.tipo] || session.tipo}</p>
        </div>
      </div>
      <div className="metric-grid">
        <Stat label="Respondidas" value={`${summary.answered}/${summary.total}`} />
        <Stat label="Nivel" value={difficultyLabels[summary.currentLevel] || "Basico"} />
      </div>
      {summary.result && (
        <div className="summary-note">
          <strong>{summary.result.respuestasCorrectas} correctas</strong>
          <span>Nuevo nivel sugerido: {difficultyLabels[summary.result.nivelAsignado] || summary.result.nivelAsignado}</span>
        </div>
      )}
    </Panel>
  );
}

function ResultBox({ result }) {
  return (
    <div className="result-box">
      <div>
        <span>Resultado</span>
        <strong>{result.porcentaje.toFixed(1)}%</strong>
      </div>
      <div>
        <span>Correctas</span>
        <strong>
          {result.respuestasCorrectas}/{result.totalPreguntas}
        </strong>
      </div>
      <div>
        <span>Nivel asignado</span>
        <strong>{difficultyLabels[result.nivelAsignado] || result.nivelAsignado}</strong>
      </div>
      {result.refuerzos?.length > 0 && (
        <div className="support-list">
          {result.refuerzos.map((item) => (
            <article key={item.preguntaId}>
              <strong>{item.contenidoPregunta}</strong>
              <p>Marcaste {item.respuestaSeleccionada}. Correcta: {item.respuestaCorrecta}.</p>
              <p>{item.refuerzo || "Repasa el tema y vuelve a intentarlo."}</p>
            </article>
          ))}
        </div>
      )}
      {result.refuerzos?.length === 0 && (
        <div className="support-list">
          <article>
            <strong>Buen trabajo</strong>
            <p>No tienes refuerzos pendientes en esta evaluacion.</p>
          </article>
        </div>
      )}
    </div>
  );
}

function Panel({ icon, title, aside, children }) {
  return (
    <section className="panel">
      <header className="panel-header">
        <div>
          {icon}
          <h2>{title}</h2>
        </div>
        {aside && <span>{aside}</span>}
      </header>
      {children}
    </section>
  );
}

function Field({ label, value, onChange, type = "text", placeholder = "" }) {
  return (
    <label className="field">
      <span>{label}</span>
      <input type={type} value={value} placeholder={placeholder} onChange={(event) => onChange(event.target.value)} />
    </label>
  );
}

function Select({ label, value, onChange, children }) {
  return (
    <label className="field">
      <span>{label}</span>
      <select value={value} onChange={(event) => onChange(event.target.value)}>
        {children}
      </select>
    </label>
  );
}

function SegmentedControl({ value, options, onChange }) {
  return (
    <div className="segmented-control">
      {options.map((option) => (
        <button key={option.value} className={value === option.value ? "active" : ""} onClick={() => onChange(option.value)}>
          {option.label}
        </button>
      ))}
    </div>
  );
}

function SubmitButton({ icon, label }) {
  return (
    <button className="primary-button">
      {icon}
      {label}
    </button>
  );
}

function StatusMessage({ status }) {
  if (!status.message) return null;
  return <p className={`status ${status.type}`}>{status.message}</p>;
}

function Stat({ label, value }) {
  return (
    <div className="stat">
      <span>{label}</span>
      <strong>{value}</strong>
    </div>
  );
}

function Pill({ children }) {
  return <span className="pill">{children}</span>;
}

function EmptyState({ title, text }) {
  return (
    <div className="empty-state">
      <CheckCircle2 size={18} />
      <strong>{title}</strong>
      <span>{text}</span>
    </div>
  );
}

export default App;
