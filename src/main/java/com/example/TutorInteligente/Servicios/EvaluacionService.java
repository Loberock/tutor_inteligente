package com.example.TutorInteligente.Servicios;

import com.example.TutorInteligente.ClasesDTO.CursoNivelDTO;
import com.example.TutorInteligente.ClasesDTO.EvaluacionRequest;
import com.example.TutorInteligente.ClasesDTO.RespuestaPreguntaDTO;
import com.example.TutorInteligente.Entidades.Alumno;
import com.example.TutorInteligente.Entidades.AlumnoCurso;
import com.example.TutorInteligente.Entidades.Pregunta;
import com.example.TutorInteligente.Entidades.PreguntasResueltas;
import com.example.TutorInteligente.Repositorios.AlumnoCursoRepository;
import com.example.TutorInteligente.Repositorios.AlumnoRepository;
import com.example.TutorInteligente.Repositorios.PreguntaRepository;
import com.example.TutorInteligente.Repositorios.PreguntasResueltasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

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


    public String procesarEvaluacion(EvaluacionRequest dto) {

        Alumno alumno = alumnoRepo.findById(dto.getAlumnoId())
                .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));

        // ==============================
        // 1. GUARDAR RESPUESTAS
        // ==============================
        for (RespuestaPreguntaDTO r : dto.getRespuestas()) {

            Pregunta pregunta = preguntaRepo.findById(r.getPreguntaId())
                    .orElseThrow(() -> new RuntimeException("Pregunta no encontrada"));

            PreguntasResueltas pr = new PreguntasResueltas();

            pr.setAlumno(alumno);
            pr.setPregunta(pregunta);
            pr.setRespuestaSeleccionada(r.getRespuestaSeleccionada());

            boolean esCorrecta =
                    pregunta.getRespuestaCorrecta()
                            .equalsIgnoreCase(r.getRespuestaSeleccionada());

            pr.setCorrecta(esCorrecta);

            pr.setFecha(LocalDate.now());

            prRepo.save(pr);
        }

        // ==============================
        // 2. ACTUALIZAR NIVELES
        // ==============================
        for (CursoNivelDTO c : dto.getCursos()) {

            AlumnoCurso ac = alumnoCursoRepo
                    .findByAlumno_AlumnoIdAndCurso_CursoId(
                            alumno.getAlumnoId(),
                            c.getCursoId()
                    )
                    .orElseThrow(() ->
                            new RuntimeException("Relación alumno-curso no encontrada")
                    );

            ac.setNivel(c.getNivel());

            alumnoCursoRepo.save(ac);
        }

        return "EVALUACION REGISTRADA CORRECTAMENTE";
    }
}