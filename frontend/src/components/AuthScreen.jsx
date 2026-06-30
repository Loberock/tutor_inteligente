import { KeyRound, Monitor, Plus, School } from "lucide-react";
import { useState } from "react";
import { CLASSROOM_IMAGE_SRC } from "../config/assets";
import { apiRequest } from "../services/api";
import { Field, SegmentedControl, Select, StatusMessage, SubmitButton } from "./ui";
import { AppHeader } from "./AppHeader";

export function AuthScreen({ onLogin }) {
  const [mode, setMode] = useState("login");
  const [role, setRole] = useState("ALUMNO");

  return (
    <>
      <AppHeader />
      <section className="auth-layout">
        <aside className="auth-preview" aria-label="Resumen del sistema">
          <figure className="auth-visual">
            <img src={CLASSROOM_IMAGE_SRC} alt="Estudiante resolviendo ejercicios en Tutor Inteligente" />
          </figure>
          <div className="preview-note">
            <strong>Practica matematica con una ruta clara</strong>
            <span>El sistema adapta ejercicios, nivel y refuerzos segun el avance de cada estudiante.</span>
          </div>
        </aside>

        <section className="auth-card">
          <p className="eyebrow">Acceso al sistema</p>
          <h1>Bienvenido</h1>
          <p className="auth-copy">
            Ingresa para practicar como alumno o administrar preguntas y reportes como docente.
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
      <Field label="Correo" type="email" value={form.correo} placeholder="alumno@demo.pe" onChange={(correo) => setForm({ ...form, correo })} />
      <Field label="Contrasena" type="password" value={form.contrasena} placeholder="Minimo 6 caracteres" onChange={(contrasena) => setForm({ ...form, contrasena })} />
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
      <Field label="Correo" type="email" value={form.correo} placeholder="correo@institucion.pe" onChange={(correo) => setForm({ ...form, correo })} />
      <Field label="Contrasena" type="password" value={form.contrasena} placeholder="Minimo 6 caracteres" onChange={(contrasena) => setForm({ ...form, contrasena })} />
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
