# Roadmap de pendientes - TutorInteligente

Fecha: 28/06/2026

## Cambio realizado

Se actualizo el proyecto para usar **Java 21**.

Archivo modificado:

- `pom.xml`

Configuracion actual:

```xml
<java.version>21</java.version>
```

Verificacion realizada:

```powershell
.\mvnw.cmd -v
.\mvnw.cmd -DskipTests compile
```

Resultado:

- Maven detecta Java `21.0.6`.
- El proyecto compila correctamente con `BUILD SUCCESS`.

## Viabilidad del sistema

Si se puede implementar el sistema con la base actual. El proyecto ya tiene una estructura util para continuar:

- Entidades principales: usuario, alumno, profesor, curso, pregunta, evaluacion/respuestas y alumno-curso.
- Repositorios JPA.
- Servicios de registro, preguntas, evaluacion, niveles y reportes.
- Endpoints REST iniciales.
- Conexion preparada para SQL Server.

La parte de tutor inteligente tambien es viable, pero conviene implementarla primero con reglas claras y trazables, no con IA compleja. Para el curso, la parte de IA puede explicarse como evolucion tecnica: que datos se usarian, que modelos tendrian sentido y donde encajarian. Por ahora se ignora la implementacion de IA, como se indico.

## Prioridad 1: Seguridad, login y JWT

Esta debe ser la primera etapa.

Tareas:

1. Agregar dependencias de seguridad:
   - Spring Security.
   - Libreria JWT.

2. Mejorar la entidad `Usuario`:
   - Agregar campo `rol` o `tipoUsuario`.
   - Mantener roles como `ALUMNO`, `PROFESOR` y opcionalmente `ADMIN`.
   - Cambiar `contraseña` por un nombre sin problemas de codificacion, por ejemplo `contrasena`.

3. Hashear contrasenas:
   - Usar `BCryptPasswordEncoder`.
   - Modificar el registro para guardar hash, no texto plano.

4. Crear endpoint de login:

```http
POST /auth/login
```

Entrada esperada:

```json
{
  "correo": "usuario@correo.com",
  "contrasena": "123456"
}
```

Salida esperada:

```json
{
  "token": "jwt...",
  "tipo": "ALUMNO",
  "usuarioId": 1,
  "nombre": "Nombre del usuario"
}
```

5. Crear endpoint opcional de perfil:

```http
GET /auth/me
```

Debe devolver datos basicos del usuario autenticado.

6. Configurar filtro JWT:
   - Leer token desde `Authorization: Bearer <token>`.
   - Validar firma y expiracion.
   - Cargar usuario autenticado en el contexto de Spring Security.

7. Proteger endpoints:
   - Publicos:
     - `POST /registro`
     - `POST /auth/login`
   - Protegidos para profesor:
     - `POST /pregunta/batch`
     - `GET /reporte/rendimiento`
   - Protegidos para alumno:
     - `POST /evaluacion`
     - `PUT /alumno-curso/niveles`

8. Mover secretos a configuracion segura:
   - `jwt.secret`
   - credenciales de base de datos
   - tiempo de expiracion del token

9. Agregar pruebas basicas:
   - login correcto.
   - login con credenciales invalidas.
   - endpoint protegido sin token.
   - endpoint protegido con token valido.

## Prioridad 2: Correcciones tecnicas base

Antes de crecer funcionalidades, conviene ordenar estos puntos.

Tareas:

1. Corregir problemas de codificacion:
   - `contraseÃ±a`
   - `relaciÃ³n`
   - `SEGÃšN`

2. Renombrar campos problematicos:
   - Usar `contrasena` en Java y JSON.
   - Evitar caracteres especiales en nombres internos del codigo.

3. Agregar validaciones a DTOs:
   - `@NotBlank`
   - `@NotNull`
   - `@Email`
   - `@Size`
   - `@Valid`

4. Crear manejo global de errores:
   - `@RestControllerAdvice`
   - respuestas consistentes para errores.

5. Separar perfiles:
   - `application-dev.properties`
   - `application-test.properties`
   - `application-prod.properties`

6. Crear entorno de pruebas sin SQL Server obligatorio:
   - H2 para tests simples, o
   - Testcontainers con SQL Server si se quiere mayor fidelidad.

## Prioridad 3: Funcionalidades basicas del tutor inteligente

Esta es la parte central del producto, sin implementar IA real por ahora.

Tareas:

1. Gestion de cursos:
   - Crear curso.
   - Listar cursos.
   - Editar curso.
   - Eliminar o desactivar curso.

2. Gestion de preguntas:
   - Listar preguntas.
   - Buscar por curso.
   - Buscar por grado.
   - Buscar por dificultad.
   - Editar preguntas.
   - Eliminar o desactivar preguntas.

3. Evaluacion diagnostica:
   - Endpoint para generar una evaluacion inicial por grado/curso.
   - Seleccionar preguntas segun nivel inicial.
   - Registrar intento del alumno.

4. Adaptacion por reglas:
   - Si el alumno obtiene bajo porcentaje, asignar `BASICO`.
   - Si obtiene porcentaje medio, asignar `INTERMEDIO`.
   - Si obtiene porcentaje alto, asignar `AVANZADO`.

5. Seleccion adaptativa de ejercicios:
   - Si el nivel es `BASICO`, entregar preguntas faciles.
   - Si el nivel es `INTERMEDIO`, entregar preguntas medias.
   - Si el nivel es `AVANZADO`, entregar preguntas dificiles.

6. Refuerzo personalizado:
   - Usar el campo `refuerzo` de cada pregunta.
   - Mostrar refuerzo cuando el alumno responde mal.
   - Agrupar recomendaciones por curso o tema.

7. Historial de progreso:
   - Guardar fecha, curso, nivel, porcentaje y cantidad de respuestas.
   - Consultar progreso por alumno.

8. Reportes docentes:
   - Reporte por alumno.
   - Reporte por grado.
   - Reporte por curso.
   - Alumnos que necesitan refuerzo.
   - Evolucion del rendimiento en el tiempo.

## Prioridad 4: API y documentacion

Tareas:

1. Agregar Swagger/OpenAPI.
2. Documentar requests y responses.
3. Crear ejemplos para Postman o Bruno.
4. Documentar flujo principal:
   - registro
   - login
   - carga de cursos
   - carga de preguntas
   - evaluacion
   - reporte

## Prioridad 5: Frontend web

El requerimiento habla de plataforma web, pero el repositorio actual solo tiene backend.

Pantallas necesarias:

1. Login.
2. Registro.
3. Panel de alumno.
4. Evaluacion diagnostica.
5. Resolucion de ejercicios.
6. Resultado y refuerzo.
7. Panel de profesor.
8. Gestion de preguntas.
9. Reportes de rendimiento.

Tecnologias posibles:

- React.
- Angular.
- Vue.
- Thymeleaf si se quiere algo mas simple dentro de Spring Boot.

## Prioridad 6: Justificacion tecnica de IA, sin implementarla

Para cumplir el enfoque del APF3 sin implementar IA, se puede explicar lo siguiente:

### Donde encajaria la IA

La IA encajaria en la adaptacion del nivel del estudiante y en la recomendacion de ejercicios/refuerzos. El sistema podria analizar respuestas historicas, tiempo de resolucion, dificultad de preguntas y progreso por curso para sugerir el siguiente contenido mas adecuado.

### Datos necesarios

- Alumno.
- Grado.
- Curso.
- Preguntas respondidas.
- Respuesta seleccionada.
- Respuesta correcta.
- Dificultad.
- Fecha.
- Nivel asignado.
- Porcentaje de acierto.
- Historial de intentos.

### Modelos posibles

- **Arbol de decision**: recomendado para explicar reglas de nivelacion porque es interpretable.
- **KNN**: podria recomendar ejercicios comparando estudiantes con patrones similares.
- **Clasificacion**: podria predecir si el estudiante necesita refuerzo.
- **Regresion**: podria estimar un puntaje esperado o progreso futuro.
- **SVM**: posible para clasificacion, aunque menos interpretable para este caso.

### Modelo mas adecuado para el proyecto

Para este proyecto, el modelo mas conveniente como propuesta seria un **arbol de decision**, porque:

- Es facil de explicar en una presentacion.
- Se relaciona bien con reglas educativas.
- Permite justificar por que un alumno queda en `BASICO`, `INTERMEDIO` o `AVANZADO`.
- Encaja con el requerimiento de adaptar el nivel segun desempeno.

### Estado recomendado

La IA debe quedar como evolucion planificada. En la implementacion actual se usarian reglas deterministicas, por ejemplo:

```text
0% - 50%    -> BASICO
51% - 75%   -> INTERMEDIO
76% - 100%  -> AVANZADO
```

Esto permite demostrar criterio tecnico sin complicar el desarrollo principal.

## Orden recomendado de trabajo

1. Seguridad JWT y login.
2. Correccion de codificacion y contrasenas.
3. Validaciones y manejo global de errores.
4. Endpoints faltantes de cursos y preguntas.
5. Evaluacion diagnostica adaptativa por reglas.
6. Reportes docentes mejorados.
7. Documentacion Swagger/Postman.
8. Frontend web.
9. Justificacion IA en documento/presentacion.

## Conclusion

El sistema es implementable y la base actual sirve para avanzar. El siguiente paso correcto es construir seguridad con JWT, porque ordena usuarios, roles y acceso antes de exponer mas funcionalidades. Despues conviene completar el nucleo del tutor inteligente con reglas adaptativas simples, reportes y flujo de evaluacion. La IA puede quedar documentada como evolucion futura, usando arboles de decision como alternativa principal por su facilidad de explicacion.
