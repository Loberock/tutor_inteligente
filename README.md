# TutorInteligente

Backend REST para una plataforma de tutoria inteligente adaptativa orientada a estudiantes de secundaria. El sistema permite registrar usuarios, autenticar con JWT, gestionar cursos y preguntas, entregar evaluaciones diagnosticas por nivel, registrar respuestas, devolver refuerzos y generar reportes docentes.

## Stack

- Java 21
- Spring Boot 4.1
- Spring Web MVC
- Spring Security
- JWT
- Spring Data JPA
- SQL Server
- Maven
- Lombok
- Swagger/OpenAPI

## Requisitos

- JDK 21
- SQL Server con TCP/IP habilitado en el puerto `1433`
- Maven Wrapper incluido en el proyecto
- Base de datos `DB_TutorInteligente`

Si la base no existe, se puede crear con:

```powershell
sqlcmd -S localhost -U sa -P 12345 -Q "CREATE DATABASE DB_TutorInteligente;"
```

## Configuracion

Crear un archivo `.env` en la raiz del proyecto tomando como referencia `.env.example`:

```env
DB_URL=jdbc:sqlserver://localhost:1433;databaseName=DB_TutorInteligente;encrypt=true;trustServerCertificate=true
DB_USERNAME=sa
DB_PASSWORD=12345
JWT_SECRET=dev-only-secret-key-change-before-production-1234567890
JWT_EXPIRATION_MS=86400000
```

El archivo `.env` no se sube a Git.

## Ejecutar

```powershell
.\scripts\run-dev.ps1
```

El backend queda disponible en:

```text
http://localhost:8080
```

## Swagger

Con el backend levantado:

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON:

```text
http://localhost:8080/v3/api-docs
```

Para probar endpoints protegidos en Swagger:

1. Registrar usuario o profesor.
2. Iniciar sesion en `POST /v1/sesiones`.
3. Copiar el token.
4. Usar `Authorize` con `Bearer <token>`.

## Rutas principales

### Autenticacion

```http
POST /v1/sesiones
```

```json
{
  "correo": "profesor@test.com",
  "contrasena": "123456"
}
```

### Registro de alumno

Si no se envia `tipo`, se registra como `ALUMNO`.

```http
POST /v1/usuarios
```

```json
{
  "nombre": "Alan",
  "apellido": "Turing",
  "correo": "alumno@test.com",
  "contrasena": "123456",
  "grado": "1"
}
```

### Registro de profesor

```http
POST /v1/usuarios
```

```json
{
  "nombre": "Ada",
  "apellido": "Lovelace",
  "correo": "profesor@test.com",
  "contrasena": "123456",
  "tipo": "PROFESOR"
}
```

### Cursos

```http
GET /v1/cursos
GET /v1/cursos/{cursoId}
POST /v1/cursos
PUT /v1/cursos/{cursoId}
DELETE /v1/cursos/{cursoId}
```

Crear curso:

```json
{
  "nombreCurso": "Matematica"
}
```

### Preguntas

```http
GET /v1/preguntas
GET /v1/preguntas?cursoId=1&grado=1&dificultad=BASICO
GET /v1/preguntas/{preguntaId}
POST /v1/preguntas
POST /v1/preguntas/lote
PUT /v1/preguntas/{preguntaId}
DELETE /v1/preguntas/{preguntaId}
```

Crear pregunta:

```json
{
  "profesorId": 1,
  "cursoId": 1,
  "contenidoPregunta": "Cuanto es 2 + 2?",
  "grado": "1",
  "dificultad": "BASICO",
  "opcionA": "3",
  "opcionB": "4",
  "opcionC": "5",
  "opcionD": "6",
  "respuestaCorrecta": "B",
  "refuerzo": "Repasa la suma de numeros naturales."
}
```

### Evaluacion diagnostica

```http
GET /v1/evaluaciones/diagnostico?cursoId=1&grado=1&nivel=BASICO&cantidad=10
```

Devuelve preguntas sin exponer respuesta correcta ni refuerzo.

### Procesar evaluacion

```http
POST /v1/evaluaciones
```

```json
{
  "alumnoId": 1,
  "respuestas": [
    {
      "preguntaId": 1,
      "respuestaSeleccionada": "B"
    }
  ],
  "cursos": [
    {
      "cursoId": 1,
      "nivel": "BASICO"
    }
  ]
}
```

La respuesta incluye total, correctas, porcentaje, nivel asignado por reglas y refuerzos si el alumno fallo preguntas.

### Reportes

```http
GET /v1/reportes/rendimiento
GET /v1/reportes/rendimiento?alumnoId=1&cursoId=1&grado=1&soloRefuerzo=true
GET /v1/reportes/refuerzos
```

## Seguridad

Endpoints publicos:

- `POST /v1/usuarios`
- `POST /v1/sesiones`
- Swagger/OpenAPI

Endpoints protegidos:

- Cursos: lectura para usuarios autenticados; escritura para profesores.
- Preguntas: profesores.
- Evaluaciones: alumnos.
- Reportes: profesores.

## Flujo recomendado de prueba

1. Crear profesor.
2. Iniciar sesion como profesor.
3. Crear curso.
4. Crear preguntas.
5. Crear alumno.
6. Iniciar sesion como alumno.
7. Obtener diagnostico.
8. Enviar evaluacion.
9. Iniciar sesion como profesor y revisar reportes.

## Siguiente etapa

El siguiente punto del roadmap es crear el frontend web con React.
