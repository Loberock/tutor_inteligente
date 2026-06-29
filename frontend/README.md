# Frontend TutorInteligente

Interfaz web construida con React y Vite para consumir el backend Spring Boot del proyecto.

## Requisitos

- Node.js 20 o superior
- Backend activo en `http://localhost:8080`

## Comandos

```bash
npm install
npm run dev
npm run build
```

En desarrollo, Vite usa un proxy para enviar las llamadas `/v1/**` al backend:

```js
server: {
  proxy: {
    '/v1': 'http://localhost:8080'
  }
}
```

## Alcance del punto 5.1

- Pantalla de inicio de sesion y registro.
- Registro de alumnos por defecto y profesores seleccionando el rol.
- Dashboard docente para cursos, preguntas y reportes.
- Dashboard alumno para diagnostico y envio de evaluaciones.
- Persistencia de sesion en `localStorage`.
- Uso del JWT en el header `Authorization: Bearer`.

## Endpoints consumidos

- `POST /v1/sesiones`
- `POST /v1/usuarios`
- `GET /v1/cursos`
- `POST /v1/cursos`
- `GET /v1/preguntas`
- `POST /v1/preguntas`
- `GET /v1/evaluaciones/diagnostico`
- `POST /v1/evaluaciones`
- `GET /v1/reportes/rendimiento`
- `GET /v1/reportes/refuerzos`
