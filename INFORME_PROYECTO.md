# Informe del proyecto TutorInteligente

Fecha de revision: 27/06/2026

## Resumen general

El proyecto **TutorInteligente** es una aplicacion backend orientada a gestionar un sistema educativo basico para alumnos y profesores. Su proposito principal es registrar usuarios, administrar preguntas por curso, procesar evaluaciones de alumnos, actualizar niveles de aprendizaje por curso y generar reportes de rendimiento.

Aunque el nombre sugiere un "tutor inteligente", en el codigo actual no se observa integracion con inteligencia artificial, modelos de recomendacion, chatbots, generacion automatica de preguntas ni algoritmos adaptativos avanzados. La inteligencia del sistema, por ahora, esta representada por reglas simples: comparar respuestas, calcular porcentajes y clasificar el rendimiento del alumno.

## Lenguaje y tecnologias

El proyecto esta construido principalmente en **Java**.

Stack identificado:

- **Java 17** configurado en `pom.xml`.
- **Spring Boot 4.1.0** como framework principal.
- **Spring Web MVC** para exponer endpoints REST.
- **Spring Data JPA** para persistencia.
- **Hibernate** como implementacion JPA.
- **Microsoft SQL Server** como base de datos esperada.
- **Maven** como gestor de dependencias y build.
- **JUnit / Spring Boot Test** para pruebas, aunque solo existe una prueba de carga de contexto.

Archivos clave:

- `pom.xml`: define Spring Boot, Java 17, JPA, Web MVC y SQL Server JDBC.
- `src/main/resources/application.properties`: configura conexion a SQL Server.
- `src/main/java/com/example/TutorInteligente/TutorInteligenteApplication.java`: punto de entrada de Spring Boot.

## Estructura del proyecto

La estructura fuente esta organizada por capas:

```text
src/main/java/com/example/TutorInteligente
|-- ClasesDTO
|-- Controladores
|-- Entidades
|-- Repositorios
|-- Servicios
`-- TutorInteligenteApplication.java
```

Cantidad aproximada de archivos Java principales:

- `Controladores`: 5
- `Servicios`: 5
- `Entidades`: 7
- `Repositorios`: 7
- `ClasesDTO`: 9
- Aplicacion principal: 1

Tambien existe carpeta `target`, lo que indica que el proyecto ya fue compilado antes y tiene artefactos generados, incluyendo un `.jar`.

## Arquitectura

El proyecto sigue una arquitectura clasica de backend Spring:

- **Controladores**: reciben solicitudes HTTP y devuelven respuestas REST.
- **Servicios**: contienen la logica de negocio.
- **Repositorios**: acceden a la base de datos mediante Spring Data JPA.
- **Entidades**: representan las tablas principales.
- **DTOs**: transportan datos de entrada y salida.

No se observa frontend dentro del repositorio. Tampoco hay archivos de React, Angular, Vue, HTML, CSS ni plantillas web. Por tanto, este proyecto actualmente es solo backend/API.

## Funcionalidades implementadas

### 1. Registro de usuarios

Endpoint:

```http
POST /registro
```

Permite registrar usuarios de tipo:

- `ALUMNO`
- `PROFESOR`

Para alumnos, ademas:

- Crea el registro del alumno.
- Obtiene todos los cursos existentes.
- Crea relaciones `alumno_curso` para cada curso.
- Inicializa el nivel del alumno en cada curso como `BASICO`.

Para profesores:

- Crea el registro del profesor asociado a un usuario.

Limitaciones observadas:

- No hay login.
- No hay autenticacion JWT o sesiones.
- Las contrasenas se guardan como texto plano.
- No hay validaciones con anotaciones como `@NotBlank`, `@Email` o `@Valid`.
- Si el tipo no es `ALUMNO`, el sistema asume que es profesor.

### 2. Registro masivo de preguntas

Endpoint:

```http
POST /pregunta/batch
```

Permite registrar una lista de preguntas. Cada pregunta incluye:

- Profesor
- Curso
- Contenido de la pregunta
- Grado
- Dificultad
- Opciones A, B, C y D
- Respuesta correcta
- Refuerzo

La operacion esta marcada como transaccional, por lo que si una pregunta falla deberia revertirse todo el lote.

Limitaciones observadas:

- No hay endpoint para listar preguntas.
- No hay endpoint para buscar preguntas por curso, grado o dificultad.
- No hay validacion de que la respuesta correcta pertenezca a una opcion valida.
- No hay control de duplicados.

### 3. Procesamiento de evaluaciones

Endpoint:

```http
POST /evaluacion
```

Procesa una evaluacion de un alumno:

- Busca al alumno.
- Recorre las respuestas enviadas.
- Busca cada pregunta.
- Compara la respuesta seleccionada con la respuesta correcta.
- Guarda cada respuesta en `preguntas_resueltas`.
- Actualiza los niveles del alumno por curso.

Limitaciones observadas:

- El calculo de nivel no lo hace el backend; recibe los niveles ya calculados desde el request.
- No hay puntaje total retornado al cliente.
- No hay detalle de preguntas correctas/incorrectas en la respuesta.
- No hay control para evitar registrar varias veces la misma evaluacion.

### 4. Actualizacion de niveles alumno-curso

Endpoint:

```http
PUT /alumno-curso/niveles
```

Actualiza el nivel de un alumno en varios cursos. El alumno se busca mediante `usuarioId`.

Niveles usados en la logica:

- `BASICO`
- `INTERMEDIO`
- `AVANZADO`

Limitaciones observadas:

- Los niveles son texto libre; no hay enum ni tabla de niveles.
- No hay validacion para impedir valores invalidos.

### 5. Reporte de rendimiento

Endpoint:

```http
GET /reporte/rendimiento
```

Genera un reporte por alumno:

- Obtiene los cursos del alumno.
- Selecciona el curso con menor nivel.
- Busca respuestas resueltas de ese alumno en ese curso.
- Calcula porcentaje de respuestas correctas.
- Clasifica el estado:
  - `NECESITA REFUERZO` si el porcentaje es menor o igual a 50.
  - `EN PROCESO` si el porcentaje es menor o igual a 75.
  - `AVANZA BIEN` si el porcentaje es mayor a 75.

Limitaciones observadas:

- Si el alumno no tiene cursos, se omite del reporte.
- Si no tiene respuestas en su peor curso, tambien se omite.
- Hay `System.out.println` de depuracion dentro del servicio.
- El reporte es general; no recibe filtros por curso, grado, alumno, fechas o profesor.

## Modelo de datos identificado

Entidades principales:

- `Usuario`
  - `usuarioId`
  - `correo`
  - `contraseña`
  - `estado`

- `Alumno`
  - `alumnoId`
  - `grado`
  - `nombre`
  - `apellido`
  - relacion uno a uno con `Usuario`

- `Profesor`
  - `profesorId`
  - `nombre`
  - `apellido`
  - relacion uno a uno con `Usuario`

- `Curso`
  - `cursoId`
  - `nombreCurso`

- `AlumnoCurso`
  - relacion entre alumno y curso
  - campo `nivel`

- `Pregunta`
  - contenido
  - grado
  - dificultad
  - opciones A-D
  - respuesta correcta
  - refuerzo
  - relacion con curso
  - relacion con profesor

- `PreguntasResueltas`
  - alumno
  - pregunta
  - respuesta seleccionada
  - indicador de correcta/incorrecta
  - fecha

## Base de datos

El proyecto espera conectarse a una base de datos SQL Server local:

```text
jdbc:sqlserver://localhost:1433;databaseName=DB_TutorInteligente
```

La estrategia de Hibernate esta configurada como:

```properties
spring.jpa.hibernate.ddl-auto=update
```

Esto permite que Hibernate intente actualizar el esquema automaticamente.

Riesgos:

- Las credenciales de base de datos estan escritas directamente en `application.properties`.
- No hay perfiles separados para desarrollo, pruebas y produccion.
- No hay migraciones con Flyway o Liquibase.
- La prueba automatica depende de tener SQL Server corriendo localmente.

## Estado de avance

El proyecto esta en un estado de **prototipo backend funcional parcial**.

Estimacion cualitativa de avance:

```text
Backend base / CRUD parcial:        60% - 70%
Logica educativa basica:            45% - 55%
Persistencia y modelo de datos:     60% - 70%
API REST documentada/probada:       20% - 30%
Seguridad/autenticacion:             0% - 10%
Frontend:                            0%
Pruebas automatizadas reales:        5% - 10%
Preparacion para produccion:        15% - 25%
```

Avance global estimado:

```text
40% - 50%
```

La razon de esta estimacion es que ya existe una base clara de backend con entidades, repositorios, servicios y endpoints importantes, pero todavia faltan piezas esenciales para considerarlo completo: autenticacion, validaciones, manejo robusto de errores, endpoints de consulta, documentacion de API, pruebas reales, seguridad de credenciales, frontend y separacion de ambientes.

## Verificacion realizada

Se ejecuto:

```powershell
.\mvnw.cmd test
```

Resultado:

```text
BUILD FAILURE
Tests run: 1, Failures: 0, Errors: 1
```

La causa principal fue que Spring Boot intento iniciar JPA conectandose a SQL Server en `localhost:1433`, pero la conexion fue rechazada. El error indica que no habia una instancia de SQL Server aceptando conexiones en ese puerto durante la prueba.

Esto no significa necesariamente que el codigo no pueda funcionar, pero si confirma que las pruebas no estan aisladas y dependen de infraestructura local.

## Problemas tecnicos observados

1. **Credenciales expuestas**

   El archivo `application.properties` contiene usuario y contrasena de base de datos. Esto deberia moverse a variables de entorno o perfiles locales ignorados por Git.

2. **Contrasenas sin cifrar**

   La entidad `Usuario` almacena la contrasena directamente. Se deberia usar hashing, por ejemplo BCrypt.

3. **Sin autenticacion ni autorizacion**

   Cualquier cliente podria llamar endpoints de registro, preguntas, evaluacion o reportes si la API esta expuesta.

4. **Validacion insuficiente**

   Los DTOs no usan validaciones. Esto puede permitir datos vacios, correos invalidos, tipos incorrectos, niveles inexistentes o listas nulas.

5. **Manejo de errores basico**

   Varios controladores capturan `Exception` y devuelven `badRequest` con el mensaje. Seria mejor usar excepciones especificas y un `@ControllerAdvice`.

6. **Texto con problemas de codificacion**

   En varios archivos aparecen textos como `contraseÃ±a`, `relaciÃ³n` y `SEGÃšN`, lo que indica problemas de encoding. Esto podria afectar nombres de propiedades JSON y legibilidad.

7. **Pruebas casi inexistentes**

   Solo existe `contextLoads`, y actualmente falla si no hay SQL Server local. No hay pruebas unitarias de servicios ni pruebas de controladores.

8. **No hay documentacion funcional de API**

   `HELP.md` es el documento generado por Spring Initializr. No describe el negocio, endpoints, ejemplos ni flujo de uso.

9. **No hay datos iniciales**

   El registro de alumnos depende de que existan cursos en la base de datos. No hay `data.sql`, migraciones ni seeder.

10. **Ausencia de frontend**

   El sistema no incluye interfaz grafica para alumnos, profesores o administradores.

## Lo que falta para completarlo

Prioridad alta:

- Agregar autenticacion y roles para alumno/profesor.
- Hashear contrasenas.
- Sacar credenciales del repositorio.
- Corregir problemas de codificacion.
- Agregar validaciones en DTOs.
- Crear perfil de test con base H2 o contenedor para que `mvn test` no dependa de SQL Server local.
- Documentar endpoints con ejemplos de request/response.

Prioridad media:

- Crear endpoints para listar cursos, preguntas, alumnos, profesores y evaluaciones.
- Agregar filtros para reportes.
- Implementar calculo de nivel en backend, no solo recibirlo desde el cliente.
- Agregar migraciones de base de datos.
- Mejorar manejo de errores con `@ControllerAdvice`.

Prioridad baja o siguiente etapa:

- Crear frontend.
- Agregar Swagger/OpenAPI.
- Agregar dashboards de rendimiento.
- Implementar recomendaciones adaptativas reales.
- Integrar IA si el objetivo del proyecto es ser verdaderamente un tutor inteligente.

## Conclusion

TutorInteligente es un backend Java/Spring Boot para un sistema educativo con registro de usuarios, preguntas, evaluaciones y reportes de rendimiento. La base tecnica esta encaminada y ya tiene una estructura por capas comprensible, pero todavia se encuentra en etapa inicial/intermedia.

Actualmente puede servir como prototipo de API, especialmente si se levanta junto a SQL Server y una base con cursos precargados. Para considerarlo un sistema completo faltan seguridad, pruebas, validaciones, documentacion real, frontend y una logica adaptativa mas fuerte.
