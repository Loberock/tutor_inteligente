package com.example.TutorInteligente.Repositorios;

import com.example.TutorInteligente.Entidades.PreguntasResueltas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PreguntasResueltasRepository extends JpaRepository<PreguntasResueltas,Integer> {

    List<PreguntasResueltas> findByAlumno_AlumnoIdAndPregunta_Curso_CursoId(
            Integer alumnoId,
            Integer cursoId
    );

    List<PreguntasResueltas> findByEvaluacion_EvaluacionId(Integer evaluacionId);
}
