import {
  BookOpen,
  ClipboardList,
  GraduationCap,
  LogOut,
  Plus,
  Search,
  Send,
  ShieldCheck,
  UserRound,
} from 'lucide-react'
import { useCallback, useEffect, useMemo, useState } from 'react'
import './App.css'

const API_BASE = import.meta.env.VITE_API_BASE_URL || ''

const initialSession = () => {
  const raw = localStorage.getItem('tutor-session')
  return raw ? JSON.parse(raw) : null
}

async function apiRequest(path, { method = 'GET', body, token } = {}) {
  const response = await fetch(`${API_BASE}${path}`, {
    method,
    headers: {
      ...(body ? { 'Content-Type': 'application/json' } : {}),
      ...(token ? { Authorization: `Bearer ${token}` } : {}),
    },
    body: body ? JSON.stringify(body) : undefined,
  })

  const text = await response.text()
  const data = text ? JSON.parse(text) : null

  if (!response.ok) {
    throw new Error(data?.mensaje || 'No se pudo completar la operacion')
  }

  return data
}

function App() {
  const [session, setSession] = useState(initialSession)

  const saveSession = (nextSession) => {
    setSession(nextSession)
    localStorage.setItem('tutor-session', JSON.stringify(nextSession))
  }

  const logout = () => {
    setSession(null)
    localStorage.removeItem('tutor-session')
  }

  return (
    <main className="app-shell">
      {!session ? (
        <AuthScreen onLogin={saveSession} />
      ) : (
        <Workspace session={session} onLogout={logout} />
      )}
    </main>
  )
}

function AuthScreen({ onLogin }) {
  const [mode, setMode] = useState('login')
  const [role, setRole] = useState('ALUMNO')

  return (
    <section className="auth-layout">
      <aside className="brand-panel">
        <div className="brand-mark">
          <GraduationCap size={34} />
        </div>
        <div>
          <p className="eyebrow">TutorInteligente</p>
          <h1>Aprendizaje adaptativo para aulas con seguimiento real</h1>
          <p className="brand-copy">
            Una experiencia ligera para diagnosticar, practicar y visualizar el progreso por curso.
          </p>
        </div>
        <div className="brand-stats">
          <Stat label="Cursos" value="Nivelados" />
          <Stat label="Reportes" value="Docentes" />
          <Stat label="Refuerzo" value="Personal" />
        </div>
      </aside>

      <section className="auth-card">
        <div className="segmented-control">
          <button className={mode === 'login' ? 'active' : ''} onClick={() => setMode('login')}>
            Iniciar sesion
          </button>
          <button className={mode === 'register' ? 'active' : ''} onClick={() => setMode('register')}>
            Crear cuenta
          </button>
        </div>

        {mode === 'login' ? (
          <LoginForm onLogin={onLogin} />
        ) : (
          <>
            <div className="role-switch">
              <button className={role === 'ALUMNO' ? 'active' : ''} onClick={() => setRole('ALUMNO')}>
                Alumno
              </button>
              <button className={role === 'PROFESOR' ? 'active' : ''} onClick={() => setRole('PROFESOR')}>
                Profesor
              </button>
            </div>
            <RegisterForm role={role} onDone={() => setMode('login')} />
          </>
        )}
      </section>
    </section>
  )
}

function LoginForm({ onLogin }) {
  const [form, setForm] = useState({ correo: '', contrasena: '' })
  const [status, setStatus] = useState({ type: '', message: '' })

  const submit = async (event) => {
    event.preventDefault()
    setStatus({ type: 'loading', message: 'Validando credenciales' })
    try {
      const data = await apiRequest('/v1/sesiones', { method: 'POST', body: form })
      onLogin(data)
    } catch (error) {
      setStatus({ type: 'error', message: error.message })
    }
  }

  return (
    <form className="stack-form" onSubmit={submit}>
      <SectionTitle icon={<ShieldCheck size={20} />} title="Acceso" caption="Ingresa con tu correo institucional" />
      <Field label="Correo" type="email" value={form.correo} onChange={(correo) => setForm({ ...form, correo })} />
      <Field label="Contrasena" type="password" value={form.contrasena} onChange={(contrasena) => setForm({ ...form, contrasena })} />
      <SubmitButton icon={<Send size={18} />} label="Entrar" />
      <StatusMessage status={status} />
    </form>
  )
}

function RegisterForm({ role, onDone }) {
  const [form, setForm] = useState({ nombre: '', apellido: '', correo: '', contrasena: '', grado: '1' })
  const [status, setStatus] = useState({ type: '', message: '' })

  const submit = async (event) => {
    event.preventDefault()
    setStatus({ type: 'loading', message: 'Creando cuenta' })
    try {
      await apiRequest('/v1/usuarios', {
        method: 'POST',
        body: {
          ...form,
          tipo: role === 'PROFESOR' ? 'PROFESOR' : undefined,
          grado: role === 'ALUMNO' ? form.grado : undefined,
        },
      })
      setStatus({ type: 'success', message: 'Cuenta creada correctamente' })
      setTimeout(onDone, 700)
    } catch (error) {
      setStatus({ type: 'error', message: error.message })
    }
  }

  return (
    <form className="stack-form" onSubmit={submit}>
      <SectionTitle icon={<UserRound size={20} />} title={`Registro ${role.toLowerCase()}`} caption="Datos basicos de usuario" />
      <div className="form-grid two">
        <Field label="Nombre" value={form.nombre} onChange={(nombre) => setForm({ ...form, nombre })} />
        <Field label="Apellido" value={form.apellido} onChange={(apellido) => setForm({ ...form, apellido })} />
      </div>
      <Field label="Correo" type="email" value={form.correo} onChange={(correo) => setForm({ ...form, correo })} />
      <Field label="Contrasena" type="password" value={form.contrasena} onChange={(contrasena) => setForm({ ...form, contrasena })} />
      {role === 'ALUMNO' && <Field label="Grado" value={form.grado} onChange={(grado) => setForm({ ...form, grado })} />}
      <SubmitButton icon={<Plus size={18} />} label="Crear cuenta" />
      <StatusMessage status={status} />
    </form>
  )
}

function Workspace({ session, onLogout }) {
  const isTeacher = session.tipo === 'PROFESOR'

  return (
    <section className="workspace">
      <header className="topbar">
        <div className="topbar-title">
          <div className="brand-mark small">
            <GraduationCap size={22} />
          </div>
          <div>
            <span>{session.tipo}</span>
            <strong>{session.nombre}</strong>
          </div>
        </div>
        <button className="icon-button" onClick={onLogout} title="Cerrar sesion">
          <LogOut size={18} />
        </button>
      </header>
      {isTeacher ? <TeacherDashboard session={session} /> : <StudentDashboard session={session} />}
    </section>
  )
}

function TeacherDashboard({ session }) {
  const [refreshKey, setRefreshKey] = useState(0)

  return (
    <div className="dashboard-grid teacher-grid">
      <CoursesPanel token={session.token} refreshKey={refreshKey} onChanged={() => setRefreshKey((key) => key + 1)} />
      <QuestionsPanel session={session} refreshKey={refreshKey} />
      <ReportsPanel token={session.token} />
    </div>
  )
}

function StudentDashboard({ session }) {
  return (
    <div className="dashboard-grid student-grid">
      <StudentEvaluationPanel session={session} />
      <StudentSummary session={session} />
    </div>
  )
}

function CoursesPanel({ token, refreshKey, onChanged }) {
  const [courses, setCourses] = useState([])
  const [nombreCurso, setNombreCurso] = useState('')
  const [status, setStatus] = useState({ type: '', message: '' })

  useEffect(() => {
    apiRequest('/v1/cursos', { token })
      .then(setCourses)
      .catch((error) => setStatus({ type: 'error', message: error.message }))
  }, [token, refreshKey])

  const createCourse = async (event) => {
    event.preventDefault()
    try {
      await apiRequest('/v1/cursos', { method: 'POST', token, body: { nombreCurso } })
      setNombreCurso('')
      setStatus({ type: 'success', message: 'Curso creado' })
      onChanged()
    } catch (error) {
      setStatus({ type: 'error', message: error.message })
    }
  }

  return (
    <Panel icon={<BookOpen size={20} />} title="Cursos" aside={`${courses.length} activos`}>
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
  )
}

function QuestionsPanel({ session, refreshKey }) {
  const [courses, setCourses] = useState([])
  const [questions, setQuestions] = useState([])
  const [form, setForm] = useState({
    cursoId: '',
    contenidoPregunta: '',
    grado: '1',
    dificultad: 'BASICO',
    opcionA: '',
    opcionB: '',
    opcionC: '',
    opcionD: '',
    respuestaCorrecta: 'A',
    refuerzo: '',
  })
  const [status, setStatus] = useState({ type: '', message: '' })

  useEffect(() => {
    apiRequest('/v1/cursos', { token: session.token }).then(setCourses).catch(() => {})
  }, [session.token, refreshKey])

  const loadQuestions = async () => {
    try {
      const query = form.cursoId ? `?cursoId=${form.cursoId}&grado=${form.grado}&dificultad=${form.dificultad}` : ''
      setQuestions(await apiRequest(`/v1/preguntas${query}`, { token: session.token }))
    } catch (error) {
      setStatus({ type: 'error', message: error.message })
    }
  }

  const createQuestion = async (event) => {
    event.preventDefault()
    try {
      await apiRequest('/v1/preguntas', {
        method: 'POST',
        token: session.token,
        body: { ...form, profesorId: Number(session.perfilId), cursoId: Number(form.cursoId) },
      })
      setStatus({ type: 'success', message: 'Pregunta creada' })
      loadQuestions()
    } catch (error) {
      setStatus({ type: 'error', message: error.message })
    }
  }

  return (
    <Panel icon={<ClipboardList size={20} />} title="Preguntas" aside="Banco docente">
      <form className="stack-form dense" onSubmit={createQuestion}>
        <div className="form-grid three">
          <Select label="Curso" value={form.cursoId} onChange={(cursoId) => setForm({ ...form, cursoId })}>
            <option value="">Seleccionar</option>
            {courses.map((course) => (
              <option key={course.cursoId} value={course.cursoId}>{course.nombreCurso}</option>
            ))}
          </Select>
          <Select label="Dificultad" value={form.dificultad} onChange={(dificultad) => setForm({ ...form, dificultad })}>
            <option>BASICO</option>
            <option>INTERMEDIO</option>
            <option>AVANZADO</option>
          </Select>
        </div>
        <Field label="Pregunta" value={form.contenidoPregunta} onChange={(contenidoPregunta) => setForm({ ...form, contenidoPregunta })} />
        <div className="form-grid four">
          {['A', 'B', 'C', 'D'].map((letter) => (
            <Field key={letter} label={`Opcion ${letter}`} value={form[`opcion${letter}`]} onChange={(value) => setForm({ ...form, [`opcion${letter}`]: value })} />
          ))}
        </div>
        <div className="form-grid two">
          <Select label="Respuesta" value={form.respuestaCorrecta} onChange={(respuestaCorrecta) => setForm({ ...form, respuestaCorrecta })}>
            <option>A</option>
            <option>B</option>
            <option>C</option>
            <option>D</option>
          </Select>
          <Field label="Grado" value={form.grado} onChange={(grado) => setForm({ ...form, grado })} />
        </div>
        <Field label="Refuerzo" value={form.refuerzo} onChange={(refuerzo) => setForm({ ...form, refuerzo })} />
        <div className="button-row">
          <SubmitButton icon={<Plus size={18} />} label="Guardar" />
          <button type="button" className="secondary-button" onClick={loadQuestions}>
            <Search size={18} />
            Buscar
          </button>
        </div>
      </form>
      <div className="list">
        {questions.slice(0, 5).map((question) => (
          <div className="list-row question-row" key={question.preguntaId}>
            <span>{question.contenidoPregunta}</span>
            <small>{question.dificultad}</small>
          </div>
        ))}
      </div>
      <StatusMessage status={status} />
    </Panel>
  )
}

function ReportsPanel({ token }) {
  const [reports, setReports] = useState([])
  const [status, setStatus] = useState({ type: '', message: '' })

  const loadReports = useCallback(async (onlySupport = false) => {
    try {
      const path = onlySupport ? '/v1/reportes/refuerzos' : '/v1/reportes/rendimiento'
      setReports(await apiRequest(path, { token }))
      setStatus({ type: '', message: '' })
    } catch (error) {
      setStatus({ type: 'error', message: error.message })
    }
  }, [token])

  useEffect(() => {
    loadReports()
  }, [loadReports])

  return (
    <Panel icon={<GraduationCap size={20} />} title="Reportes" aside={`${reports.length} registros`}>
      <div className="button-row">
        <button className="secondary-button" onClick={() => loadReports(false)}>Rendimiento</button>
        <button className="secondary-button" onClick={() => loadReports(true)}>Refuerzos</button>
      </div>
      <div className="list">
        {reports.slice(0, 6).map((report) => (
          <div className="list-row report-row" key={`${report.alumnoId}-${report.cursoId}`}>
            <span>{report.nombreCompleto}</span>
            <small>{report.estado}</small>
          </div>
        ))}
      </div>
      <StatusMessage status={status} />
    </Panel>
  )
}

function StudentEvaluationPanel({ session }) {
  const [courses, setCourses] = useState([])
  const [config, setConfig] = useState({ cursoId: '', grado: '1', nivel: 'BASICO', cantidad: 5 })
  const [exercises, setExercises] = useState([])
  const [answers, setAnswers] = useState({})
  const [result, setResult] = useState(null)
  const [status, setStatus] = useState({ type: '', message: '' })

  useEffect(() => {
    apiRequest('/v1/cursos', { token: session.token }).then(setCourses).catch(() => {})
  }, [session.token])

  const loadDiagnostic = async () => {
    try {
      const query = `cursoId=${config.cursoId}&grado=${config.grado}&nivel=${config.nivel}&cantidad=${config.cantidad}`
      setExercises(await apiRequest(`/v1/evaluaciones/diagnostico?${query}`, { token: session.token }))
      setResult(null)
      setStatus({ type: '', message: '' })
    } catch (error) {
      setStatus({ type: 'error', message: error.message })
    }
  }

  const submitEvaluation = async () => {
    try {
      const data = await apiRequest('/v1/evaluaciones', {
        method: 'POST',
        token: session.token,
        body: {
          alumnoId: Number(session.perfilId),
          respuestas: exercises.map((exercise) => ({
            preguntaId: exercise.preguntaId,
            respuestaSeleccionada: answers[exercise.preguntaId] || '',
          })),
          cursos: [{ cursoId: Number(config.cursoId), nivel: config.nivel }],
        },
      })
      setResult(data)
    } catch (error) {
      setStatus({ type: 'error', message: error.message })
    }
  }

  return (
    <Panel icon={<ClipboardList size={20} />} title="Evaluacion" aside={config.nivel}>
      <div className="form-grid four">
        <Select label="Curso" value={config.cursoId} onChange={(cursoId) => setConfig({ ...config, cursoId })}>
          <option value="">Seleccionar</option>
          {courses.map((course) => (
            <option key={course.cursoId} value={course.cursoId}>{course.nombreCurso}</option>
          ))}
        </Select>
        <Field label="Grado" value={config.grado} onChange={(grado) => setConfig({ ...config, grado })} />
        <Select label="Nivel" value={config.nivel} onChange={(nivel) => setConfig({ ...config, nivel })}>
          <option>BASICO</option>
          <option>INTERMEDIO</option>
          <option>AVANZADO</option>
        </Select>
        <Field label="Cantidad" type="number" value={config.cantidad} onChange={(cantidad) => setConfig({ ...config, cantidad })} />
      </div>
      <div className="button-row">
        <button className="secondary-button" onClick={loadDiagnostic}>
          <Search size={18} />
          Cargar
        </button>
        <button className="primary-button" onClick={submitEvaluation} disabled={!exercises.length}>
          <Send size={18} />
          Enviar
        </button>
      </div>
      <div className="exercise-list">
        {exercises.map((exercise, index) => (
          <article className="exercise-item" key={exercise.preguntaId}>
            <strong>{index + 1}. {exercise.contenidoPregunta}</strong>
            <div className="answer-grid">
              {['A', 'B', 'C', 'D'].map((letter) => (
                <button key={letter} className={answers[exercise.preguntaId] === letter ? 'answer active' : 'answer'} onClick={() => setAnswers({ ...answers, [exercise.preguntaId]: letter })}>
                  {letter}. {exercise[`opcion${letter}`]}
                </button>
              ))}
            </div>
          </article>
        ))}
      </div>
      {result && <ResultBox result={result} />}
      <StatusMessage status={status} />
    </Panel>
  )
}

function StudentSummary({ session }) {
  const initials = useMemo(() => session.nombre?.split(' ').map((part) => part[0]).join('').slice(0, 2), [session.nombre])

  return (
    <Panel icon={<UserRound size={20} />} title="Alumno" aside="Sesion activa">
      <div className="profile-block">
        <div className="avatar">{initials}</div>
        <div>
          <h2>{session.nombre}</h2>
          <p>{session.tipo}</p>
        </div>
      </div>
      <div className="metric-grid">
        <Stat label="Token" value="Activo" />
        <Stat label="Ruta" value="Adaptativa" />
      </div>
    </Panel>
  )
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
        <strong>{result.respuestasCorrectas}/{result.totalPreguntas}</strong>
      </div>
      {result.refuerzos?.length > 0 && (
        <div className="support-list">
          {result.refuerzos.map((item) => (
            <p key={item.preguntaId}>{item.refuerzo}</p>
          ))}
        </div>
      )}
    </div>
  )
}

function Panel({ icon, title, aside, children }) {
  return (
    <section className="panel">
      <header className="panel-header">
        <div>{icon}<h2>{title}</h2></div>
        {aside && <span>{aside}</span>}
      </header>
      {children}
    </section>
  )
}

function SectionTitle({ icon, title, caption }) {
  return (
    <div className="section-title">
      {icon}
      <div>
        <h2>{title}</h2>
        <p>{caption}</p>
      </div>
    </div>
  )
}

function Field({ label, value, onChange, type = 'text' }) {
  return (
    <label className="field">
      <span>{label}</span>
      <input type={type} value={value} onChange={(event) => onChange(event.target.value)} />
    </label>
  )
}

function Select({ label, value, onChange, children }) {
  return (
    <label className="field">
      <span>{label}</span>
      <select value={value} onChange={(event) => onChange(event.target.value)}>
        {children}
      </select>
    </label>
  )
}

function SubmitButton({ icon, label }) {
  return <button className="primary-button">{icon}{label}</button>
}

function StatusMessage({ status }) {
  if (!status.message) return null
  return <p className={`status ${status.type}`}>{status.message}</p>
}

function Stat({ label, value }) {
  return (
    <div className="stat">
      <span>{label}</span>
      <strong>{value}</strong>
    </div>
  )
}

export default App
