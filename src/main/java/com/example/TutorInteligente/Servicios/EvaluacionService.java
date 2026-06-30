package com.example.TutorInteligente.Servicios;

import com.example.TutorInteligente.ClasesDTO.CursoNivelDTO;
import com.example.TutorInteligente.ClasesDTO.EjercicioResponse;
import com.example.TutorInteligente.ClasesDTO.EvaluacionProgresoResponse;
import com.example.TutorInteligente.ClasesDTO.EvaluacionRequest;
import com.example.TutorInteligente.ClasesDTO.EvaluacionResultadoResponse;
import com.example.TutorInteligente.ClasesDTO.RefuerzoResponse;
import com.example.TutorInteligente.ClasesDTO.RespuestaPreguntaDTO;
import com.example.TutorInteligente.Entidades.Alumno;
import com.example.TutorInteligente.Entidades.AlumnoCurso;
import com.example.TutorInteligente.Entidades.Curso;
import com.example.TutorInteligente.Entidades.Evaluacion;
import com.example.TutorInteligente.Entidades.Pregunta;
import com.example.TutorInteligente.Entidades.PreguntasResueltas;
import com.example.TutorInteligente.Repositorios.AlumnoCursoRepository;
import com.example.TutorInteligente.Repositorios.AlumnoRepository;
import com.example.TutorInteligente.Repositorios.CursoRepository;
import com.example.TutorInteligente.Repositorios.EvaluacionRepository;
import com.example.TutorInteligente.Repositorios.PreguntaRepository;
import com.example.TutorInteligente.Repositorios.PreguntasResueltasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class EvaluacionService {

    @Autowired
    private PreguntasResueltasRepository prRepo;

    @Autowired
    private PreguntaRepository preguntaRepo;

    @Autowired
    private AlumnoRepository alumnoRepo;

    @Autowired
    private AlumnoCursoRepository alumnoCursoRepo;

    @Autowired
    private CursoRepository cursoRepo;

    @Autowired
    private EvaluacionRepository evaluacionRepo;

    public List<EjercicioResponse> obtenerDiagnostico(
            Integer cursoId,
            String grado,
            String nivel,
            Integer cantidad
    ) {
        Curso curso = cursoRepo.findById(cursoId)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));

        String dificultad = dificultadPorNivel(nivel);
        int limite = cantidad == null || cantidad <= 0 ? 10 : cantidad;

        return preguntaRepo.buscarPorFiltros(curso.getCursoId(), grado, dificultad)
                .stream()
                .limit(limite)
                .map(this::toEjercicioResponse)
                .toList();
    }

    public EvaluacionResultadoResponse procesarEvaluacion(EvaluacionRequest dto) {
        Alumno alumno = alumnoRepo.findById(dto.getAlumnoId())
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));

        int correctas = 0;
        List<RefuerzoResponse> refuerzos = new ArrayList<>();
        List<PreguntasResueltas> respuestasResueltas = new ArrayList<>();
        Curso cursoEvaluado = null;

        for (RespuestaPreguntaDTO r : dto.getRespuestas()) {
            Pregunta pregunta = preguntaRepo.findById(r.getPreguntaId())
                    .orElseThrow(() -> new RuntimeException("Pregunta no encontrada"));
            cursoEvaluado = pregunta.getCurso();

            String respuestaSeleccionada = r.getRespuestaSeleccionada().trim().toUpperCase();
            boolean esCorrecta = pregunta.getRespuestaCorrecta().equalsIgnoreCase(respuestaSeleccionada);

            if (esCorrecta) {
                correctas++;
            } else {
                refuerzos.add(new RefuerzoResponse(
                        pregunta.getPreguntaId(),
                        pregunta.getContenidoPregunta(),
                        respuestaSeleccionada,
                        pregunta.getRespuestaCorrecta(),
                        pregunta.getRefuerzo()
                ));
            }

            PreguntasResueltas pr = new PreguntasResueltas();
            pr.setAlumno(alumno);
            pr.setPregunta(pregunta);
            pr.setRespuestaSeleccionada(respuestaSeleccionada);
            pr.setCorrecta(esCorrecta);
            pr.setFecha(LocalDate.now());
            respuestasResueltas.add(pr);
        }

        int total = dto.getRespuestas().size();
        double porcentaje = total == 0 ? 0.0 : (correctas * 100.0) / total;
        String nivelAsignado = nivelPorPorcentaje(porcentaje);

        if (cursoEvaluado == null && !dto.getCursos().isEmpty()) {
            Integer cursoId = dto.getCursos().get(0).getCursoId();
            cursoEvaluado = cursoRepo.findById(cursoId)
                    .orElseThrow(() -> new RuntimeException("Curso no encontrado"));
        }

        Evaluacion evaluacion = new Evaluacion();
        evaluacion.setAlumno(alumno);
        evaluacion.setCurso(cursoEvaluado);
        evaluacion.setTotalPreguntas(total);
        evaluacion.setRespuestasCorrectas(correctas);
        evaluacion.setPorcentaje(porcentaje);
        evaluacion.setNivelAsignado(nivelAsignado);
        evaluacion.setFecha(LocalDateTime.now());
        evaluacion = evaluacionRepo.save(evaluacion);

        for (PreguntasResueltas respuestaResuelta : respuestasResueltas) {
            respuestaResuelta.setEvaluacion(evaluacion);
            prRepo.save(respuestaResuelta);
        }

        for (CursoNivelDTO c : dto.getCursos()) {
            AlumnoCurso ac = obtenerRelacionAlumnoCurso(alumno, c.getCursoId());
            ac.setNivel(nivelAsignado);
            alumnoCursoRepo.save(ac);
        }

        return new EvaluacionResultadoResponse(
                "EVALUACION REGISTRADA CORRECTAMENTE",
                evaluacion.getEvaluacionId(),
                cursoEvaluado == null ? null : cursoEvaluado.getCursoId(),
                cursoEvaluado == null ? null : cursoEvaluado.getNombreCurso(),
                total,
                correctas,
                porcentaje,
                nivelAsignado,
                evaluacion.getFecha(),
                refuerzos
        );
    }

    @Transactional(readOnly = true)
    public EvaluacionProgresoResponse obtenerUltimoProgreso(Integer alumnoId, Integer cursoId) {
        Evaluacion evaluacion = (cursoId == null
                ? evaluacionRepo.findTopByAlumno_AlumnoIdOrderByFechaDesc(alumnoId)
                : evaluacionRepo.findTopByAlumno_AlumnoIdAndCurso_CursoIdOrderByFechaDesc(alumnoId, cursoId))
                .orElse(null);

        if (evaluacion == null) {
            return null;
        }

        return toProgresoResponse(evaluacion);
    }

    private AlumnoCurso obtenerRelacionAlumnoCurso(Alumno alumno, Integer cursoId) {
        return alumnoCursoRepo
                .findByAlumno_AlumnoIdAndCurso_CursoId(alumno.getAlumnoId(), cursoId)
                .orElseGet(() -> {
                    Curso curso = cursoRepo.findById(cursoId)
                            .orElseThrow(() -> new RuntimeException("Curso no encontrado"));

                    AlumnoCurso alumnoCurso = new AlumnoCurso();
                    alumnoCurso.setAlumno(alumno);
                    alumnoCurso.setCurso(curso);
                    alumnoCurso.setNivel("BASICO");
                    return alumnoCurso;
                });
    }

    private String nivelPorPorcentaje(double porcentaje) {
        if (porcentaje <= 50) {
            return "BASICO";
        }

        if (porcentaje <= 75) {
            return "INTERMEDIO";
        }

        return "AVANZADO";
    }

    private String dificultadPorNivel(String nivel) {
        if (nivel == null || nivel.isBlank()) {
            return "BASICO";
        }

        return switch (nivel.trim().toUpperCase()) {
            case "BASICO" -> "BASICO";
            case "INTERMEDIO" -> "INTERMEDIO";
            case "AVANZADO" -> "AVANZADO";
            default -> throw new RuntimeException("Nivel no valido");
        };
    }

    private EjercicioResponse toEjercicioResponse(Pregunta pregunta) {
        Curso curso = pregunta.getCurso();

        return new EjercicioResponse(
                pregunta.getPreguntaId(),
                curso.getCursoId(),
                curso.getNombreCurso(),
                pregunta.getContenidoPregunta(),
                pregunta.getGrado(),
                pregunta.getDificultad(),
                pregunta.getOpcionA(),
                pregunta.getOpcionB(),
                pregunta.getOpcionC(),
                pregunta.getOpcionD()
        );
    }

    private EvaluacionProgresoResponse toProgresoResponse(Evaluacion evaluacion) {
        List<RefuerzoResponse> refuerzos = prRepo.findByEvaluacion_EvaluacionId(evaluacion.getEvaluacionId())
                .stream()
                .filter(respuesta -> !Boolean.TRUE.equals(respuesta.getCorrecta()))
                .map(respuesta -> {
                    Pregunta pregunta = respuesta.getPregunta();
                    return new RefuerzoResponse(
                            pregunta.getPreguntaId(),
                            pregunta.getContenidoPregunta(),
                            respuesta.getRespuestaSeleccionada(),
                            pregunta.getRespuestaCorrecta(),
                            pregunta.getRefuerzo()
                    );
                })
                .toList();

        Curso curso = evaluacion.getCurso();

        return new EvaluacionProgresoResponse(
                evaluacion.getEvaluacionId(),
                evaluacion.getAlumno().getAlumnoId(),
                curso == null ? null : curso.getCursoId(),
                curso == null ? null : curso.getNombreCurso(),
                evaluacion.getTotalPreguntas(),
                evaluacion.getRespuestasCorrectas(),
                evaluacion.getPorcentaje(),
                evaluacion.getNivelAsignado(),
                evaluacion.getFecha(),
                refuerzos
        );
    }
}
