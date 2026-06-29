package com.example.TutorInteligente.Servicios;

import com.example.TutorInteligente.ClasesDTO.CursoNivelDTO;
import com.example.TutorInteligente.ClasesDTO.EjercicioResponse;
import com.example.TutorInteligente.ClasesDTO.EvaluacionRequest;
import com.example.TutorInteligente.ClasesDTO.EvaluacionResultadoResponse;
import com.example.TutorInteligente.ClasesDTO.RefuerzoResponse;
import com.example.TutorInteligente.ClasesDTO.RespuestaPreguntaDTO;
import com.example.TutorInteligente.Entidades.Alumno;
import com.example.TutorInteligente.Entidades.AlumnoCurso;
import com.example.TutorInteligente.Entidades.Curso;
import com.example.TutorInteligente.Entidades.Pregunta;
import com.example.TutorInteligente.Entidades.PreguntasResueltas;
import com.example.TutorInteligente.Repositorios.AlumnoCursoRepository;
import com.example.TutorInteligente.Repositorios.AlumnoRepository;
import com.example.TutorInteligente.Repositorios.CursoRepository;
import com.example.TutorInteligente.Repositorios.PreguntaRepository;
import com.example.TutorInteligente.Repositorios.PreguntasResueltasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
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

        for (RespuestaPreguntaDTO r : dto.getRespuestas()) {
            Pregunta pregunta = preguntaRepo.findById(r.getPreguntaId())
                    .orElseThrow(() -> new RuntimeException("Pregunta no encontrada"));

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
            prRepo.save(pr);
        }

        for (CursoNivelDTO c : dto.getCursos()) {
            AlumnoCurso ac = alumnoCursoRepo
                    .findByAlumno_AlumnoIdAndCurso_CursoId(
                            alumno.getAlumnoId(),
                            c.getCursoId()
                    )
                    .orElseThrow(() ->
                            new RuntimeException("Relacion alumno-curso no encontrada")
                    );

            ac.setNivel(c.getNivel().trim().toUpperCase());
            alumnoCursoRepo.save(ac);
        }

        int total = dto.getRespuestas().size();
        double porcentaje = total == 0 ? 0.0 : (correctas * 100.0) / total;

        return new EvaluacionResultadoResponse(
                "EVALUACION REGISTRADA CORRECTAMENTE",
                total,
                correctas,
                porcentaje,
                refuerzos
        );
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
}
