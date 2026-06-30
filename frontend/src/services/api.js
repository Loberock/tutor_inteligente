export const SESSION_KEY = "tutor-session";

const API_BASE = import.meta.env.VITE_API_BASE_URL || "";

export const roleLabels = {
  ALUMNO: "Alumno",
  PROFESOR: "Docente",
};

export const difficultyLabels = {
  BASICO: "Basico",
  INTERMEDIO: "Intermedio",
  AVANZADO: "Avanzado",
};

export function readSession() {
  const raw = localStorage.getItem(SESSION_KEY);
  return raw ? JSON.parse(raw) : null;
}

export async function apiRequest(path, { method = "GET", body, token } = {}) {
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
