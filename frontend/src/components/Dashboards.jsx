import {
  BarChart3,
  BookOpen,
  ClipboardList,
  Edit3,
  Plus,
  Search,
  Send,
  Sparkles,
  Trash2,
  X,
} from "lucide-react";
import { useCallback, useEffect, useMemo, useState } from "react";
import { apiRequest, difficultyLabels, roleLabels } from "../services/api";
import { EmptyState, Field, Panel, Pill, Select, Stat, StatusMessage, SubmitButton } from "./ui";
import { AppHeader } from "./AppHeader";

export function Workspace({ session, onLogout }) {
  const isTeacher = session.tipo === "PROFESOR";

  return (
    <>
      <AppHeader session={session} onLogout={onLogout} />
      <section className="workspace">
        <div className="workspace-heading">
          <p className="eyebrow">{isTeacher ? "Modo docente" : "Modo estudiante"}</p>
          <h1>{isTeacher ? "Banco de preguntas y reportes" : "Practica adaptativa"}</h1>
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
      <QuestionsPanel session={session} refreshKey={refreshKey} />
      <div className="teacher-side-stack">
        <ReportsPanel token={session.token} />
        <CoursesPanel token={session.token} refreshKey={refreshKey} onChanged={() => setRefreshKey((key) => key + 1)} />
      </div>
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
      <StudentEvaluationPanel session={session} onSummaryChange={setSummary} />
      <StudentSummary session={session} summary={summary} />
    </div>
  );
}

function CoursesPanel({ token, refreshKey, onChanged }) {
  const [courses, setCourses] = useState([]);
  const [nombreCurso, setNombreCurso] = useState("");
  const [editingCourse, setEditingCourse] = useState(null);
  const [status, setStatus] = useState({ type: "", message: "" });

  useEffect(() => {
    apiRequest("/v1/cursos", { token })
      .then(setCourses)
      .catch((error) => setStatus({ type: "error", message: error.message }));
  }, [token, refreshKey]);

  const saveCourse = async (event) => {
    event.preventDefault();
    try {
      const path = editingCourse ? `/v1/cursos/${editingCourse.cursoId}` : "/v1/cursos";
      await apiRequest(path, {
        method: editingCourse ? "PUT" : "POST",
        token,
        body: { nombreCurso },
      });
      setNombreCurso("");
      setEditingCourse(null);
      setStatus({ type: "success", message: editingCourse ? "Curso actualizado" : "Curso creado" });
      onChanged();
    } catch (error) {
      setStatus({ type: "error", message: error.message });
    }
  };

  const startCourseEdit = (course) => {
    setEditingCourse(course);
    setNombreCurso(course.nombreCurso);
    setStatus({ type: "", message: "" });
  };

  const cancelCourseEdit = () => {
    setEditingCourse(null);
    setNombreCurso("");
  };

  const deleteCourse = async (course) => {
    if (!window.confirm(`Eliminar el curso "${course.nombreCurso}"?`)) return;
    try {
      await apiRequest(`/v1/cursos/${course.cursoId}`, { method: "DELETE", token });
      setStatus({ type: "success", message: "Curso eliminado" });
      onChanged();
    } catch (error) {
      setStatus({ type: "error", message: error.message });
    }
  };

  return (
    <Panel icon={<BookOpen size={19} />} title="Cursos" aside={`${courses.length} activos`} className="courses-panel">
      <form className="inline-form course-form" onSubmit={saveCourse}>
        <input value={nombreCurso} onChange={(event) => setNombreCurso(event.target.value)} placeholder={editingCourse ? "Editar curso" : "Nuevo curso"} />
        <button className="icon-button filled" title="Crear curso">
          {editingCourse ? <Edit3 size={18} /> : <Plus size={18} />}
        </button>
        {editingCourse && (
          <button type="button" className="icon-button" title="Cancelar edicion" onClick={cancelCourseEdit}>
            <X size={18} />
          </button>
        )}
      </form>
      <div className="list compact">
        {courses.map((course) => (
          <div className="list-row" key={course.cursoId}>
            <span>{course.nombreCurso}</span>
            <div className="row-actions">
              <small>#{course.cursoId}</small>
              <button className="ghost-button" title="Editar curso" onClick={() => startCourseEdit(course)}>
                <Edit3 size={16} />
              </button>
              <button className="ghost-button danger" title="Eliminar curso" onClick={() => deleteCourse(course)}>
                <Trash2 size={16} />
              </button>
            </div>
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
  const [editingQuestionId, setEditingQuestionId] = useState(null);
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

  const loadQuestions = useCallback(async () => {
    try {
      const query = form.cursoId ? `?cursoId=${form.cursoId}&grado=${form.grado}&dificultad=${form.dificultad}` : "";
      setQuestions(await apiRequest(`/v1/preguntas${query}`, { token: session.token }));
      setStatus({ type: "", message: "" });
    } catch (error) {
      setStatus({ type: "error", message: error.message });
    }
  }, [form.cursoId, form.dificultad, form.grado, session.token]);

  useEffect(() => {
    loadQuestions();
  }, [loadQuestions, refreshKey]);

  const clearQuestionForm = () => {
    setEditingQuestionId(null);
    setForm({
      ...form,
      contenidoPregunta: "",
      opcionA: "",
      opcionB: "",
      opcionC: "",
      opcionD: "",
      respuestaCorrecta: "A",
      refuerzo: "",
    });
  };

  const createQuestion = async (event) => {
    event.preventDefault();
    try {
      const path = editingQuestionId ? `/v1/preguntas/${editingQuestionId}` : "/v1/preguntas";
      await apiRequest(path, {
        method: editingQuestionId ? "PUT" : "POST",
        token: session.token,
        body: {
          ...form,
          profesorId: Number(session.perfilId),
          cursoId: Number(form.cursoId),
        },
      });
      clearQuestionForm();
      setStatus({ type: "success", message: editingQuestionId ? "Pregunta actualizada" : "Pregunta creada" });
      loadQuestions();
    } catch (error) {
      setStatus({ type: "error", message: error.message });
    }
  };

  const startQuestionEdit = (question) => {
    setEditingQuestionId(question.preguntaId);
    setForm({
      cursoId: String(question.cursoId),
      contenidoPregunta: question.contenidoPregunta,
      grado: question.grado,
      dificultad: question.dificultad,
      opcionA: question.opcionA,
      opcionB: question.opcionB,
      opcionC: question.opcionC,
      opcionD: question.opcionD,
      respuestaCorrecta: question.respuestaCorrecta,
      refuerzo: question.refuerzo || "",
    });
    setStatus({ type: "", message: "" });
  };

  const deleteQuestion = async (question) => {
    if (!window.confirm("Eliminar esta pregunta del banco?")) return;
    try {
      await apiRequest(`/v1/preguntas/${question.preguntaId}`, { method: "DELETE", token: session.token });
      if (editingQuestionId === question.preguntaId) {
        clearQuestionForm();
      }
      setStatus({ type: "success", message: "Pregunta eliminada" });
      loadQuestions();
    } catch (error) {
      setStatus({ type: "error", message: error.message });
    }
  };

  return (
    <Panel icon={<ClipboardList size={19} />} title={editingQuestionId ? "Editar pregunta" : "Registrar pregunta"} aside={`${questions.length} en banco`} className="questions-panel">
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
          <SubmitButton icon={editingQuestionId ? <Edit3 size={18} /> : <Plus size={18} />} label={editingQuestionId ? "Actualizar" : "Guardar pregunta"} />
          <button type="button" className="secondary-button" onClick={loadQuestions}>
            <Search size={18} />
            Ver banco
          </button>
          {editingQuestionId && (
            <button type="button" className="secondary-button" onClick={clearQuestionForm}>
              <X size={18} />
              Cancelar
            </button>
          )}
        </div>
      </form>
      <div className="question-bank">
        {questions.length === 0 ? (
          <EmptyState title="Sin preguntas cargadas" text="Usa los filtros y guarda nuevas preguntas para este curso." />
        ) : (
          questions.map((question) => (
            <article className={editingQuestionId === question.preguntaId ? "question-card active" : "question-card"} key={question.preguntaId}>
              <div>
                <strong>{question.contenidoPregunta}</strong>
                <span>{question.cursoNombre} · {question.grado}. secundaria · {difficultyLabels[question.dificultad] || question.dificultad}</span>
              </div>
              <div className="row-actions">
                <button className="ghost-button" title="Editar pregunta" onClick={() => startQuestionEdit(question)}>
                  <Edit3 size={16} />
                </button>
                <button className="ghost-button danger" title="Eliminar pregunta" onClick={() => deleteQuestion(question)}>
                  <Trash2 size={16} />
                </button>
              </div>
            </article>
          ))
        )}
      </div>
      <StatusMessage status={status} />
    </Panel>
  );
}

function ReportsPanel({ token }) {
  const [reports, setReports] = useState([]);
  const [courses, setCourses] = useState([]);
  const [filters, setFilters] = useState({
    alumnoId: "",
    cursoId: "",
    grado: "",
    soloRefuerzo: false,
  });
  const [status, setStatus] = useState({ type: "", message: "" });

  useEffect(() => {
    apiRequest("/v1/cursos", { token })
      .then(setCourses)
      .catch(() => {});
  }, [token]);

  const loadReports = useCallback(
    async (nextFilters = filters) => {
      try {
        const params = new URLSearchParams();
        if (nextFilters.alumnoId) params.set("alumnoId", nextFilters.alumnoId);
        if (nextFilters.cursoId) params.set("cursoId", nextFilters.cursoId);
        if (nextFilters.grado) params.set("grado", nextFilters.grado);
        if (nextFilters.soloRefuerzo) params.set("soloRefuerzo", "true");
        const query = params.toString();
        setReports(await apiRequest(`/v1/reportes/rendimiento${query ? `?${query}` : ""}`, { token }));
        setStatus({ type: "", message: "" });
      } catch (error) {
        setStatus({ type: "error", message: error.message });
      }
    },
    [filters, token],
  );

  useEffect(() => {
    loadReports();
  }, [loadReports]);

  const summary = useMemo(() => {
    const total = reports.length;
    const refuerzo = reports.filter((report) => report.estado === "NECESITA REFUERZO").length;
    const promedio = total
      ? reports.reduce((sum, report) => sum + Number(report.porcentaje || 0), 0) / total
      : 0;
    const respuestas = reports.reduce((sum, report) => sum + Number(report.totalRespuestas || 0), 0);

    return { total, refuerzo, promedio, respuestas };
  }, [reports]);

  const updateFilters = (nextFilters) => {
    setFilters(nextFilters);
    loadReports(nextFilters);
  };

  const clearFilters = () => {
    updateFilters({ alumnoId: "", cursoId: "", grado: "", soloRefuerzo: false });
  };

  return (
    <Panel icon={<BarChart3 size={19} />} title="Reportes docentes" aside={`${reports.length} registros`} className="reports-panel">
      <div className="report-summary">
        <Stat label="Promedio" value={`${summary.promedio.toFixed(0)}%`} />
        <Stat label="En refuerzo" value={summary.refuerzo} />
        <Stat label="Respuestas" value={summary.respuestas} />
      </div>

      <div className="report-filters">
        <Field label="Alumno ID" value={filters.alumnoId} placeholder="Opcional" onChange={(alumnoId) => updateFilters({ ...filters, alumnoId })} />
        <Select label="Curso" value={filters.cursoId} onChange={(cursoId) => updateFilters({ ...filters, cursoId })}>
          <option value="">Todos</option>
          {courses.map((course) => (
            <option key={course.cursoId} value={course.cursoId}>
              {course.nombreCurso}
            </option>
          ))}
        </Select>
        <Select label="Grado" value={filters.grado} onChange={(grado) => updateFilters({ ...filters, grado })}>
          <option value="">Todos</option>
          <option value="1">1. secundaria</option>
          <option value="2">2. secundaria</option>
          <option value="3">3. secundaria</option>
          <option value="4">4. secundaria</option>
          <option value="5">5. secundaria</option>
        </Select>
        <label className="toggle-field">
          <span>Solo refuerzo</span>
          <input
            type="checkbox"
            checked={filters.soloRefuerzo}
            onChange={(event) => updateFilters({ ...filters, soloRefuerzo: event.target.checked })}
          />
        </label>
      </div>

      <div className="button-row">
        <button className="secondary-button" onClick={() => loadReports()}>
          <Search size={18} />
          Actualizar
        </button>
        <button className="secondary-button" onClick={clearFilters}>
          <X size={18} />
          Limpiar
        </button>
      </div>

      <div className="report-bars">
        {reports.slice(0, 4).map((report, index) => (
          <div className="report-bar" key={`${report.alumnoId}-${report.cursoId}-${index}`}>
            <span>{report.cursoNombre || report.temaCritico || "Curso"}</span>
            <div>
              <i style={{ width: `${Math.max(Number(report.porcentaje || report.porcentajePromedio || report.rendimiento || 0), 8)}%` }} />
            </div>
            <strong>{Number(report.porcentaje || report.porcentajePromedio || report.rendimiento || 0).toFixed(0)}%</strong>
          </div>
        ))}
      </div>

      <div className="table-card">
        <div className="table-row table-head">
          <span>Alumno</span>
          <span>Curso</span>
          <span>Grado</span>
          <span>Rendimiento</span>
          <span>Nivel</span>
          <span>Estado</span>
        </div>
        {reports.length === 0 ? (
          <EmptyState title="Sin reportes todavia" text="Los reportes apareceran cuando existan evaluaciones." />
        ) : (
          reports.map((report) => (
            <div className="table-row" key={`${report.alumnoId}-${report.cursoId}`}>
              <span>{report.nombreCompleto}</span>
              <span>{report.cursoNombre}</span>
              <span>{report.grado}</span>
              <span>{Number(report.porcentaje || report.porcentajePromedio || report.rendimiento || 0).toFixed(0)}%</span>
              <span>{difficultyLabels[report.nivel] || report.nivel}</span>
              <span className={report.estado === "NECESITA REFUERZO" ? "state-badge warning" : "state-badge"}>{report.estado}</span>
            </div>
          ))
        )}
      </div>
      <StatusMessage status={status} />
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
    <Panel icon={<ClipboardList size={19} />} title="Evaluacion diagnostica" aside={`${answeredCount}/${exercises.length || 0} respondidas`} className="evaluation-panel">
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
    <Panel icon={<Sparkles size={19} />} title="Progreso" aside="Resumen" className="progress-panel">
      <div className="progress-block">
        <div className="progress-ring">
          <span>{summary.progress}%</span>
        </div>
        <div className="profile-block">
          <div className="avatar">{initials}</div>
          <div>
            <h2>{session.nombre}</h2>
            <p>{roleLabels[session.tipo] || session.tipo}</p>
          </div>
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
