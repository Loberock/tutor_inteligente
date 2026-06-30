package com.example.TutorInteligente.Repositorios;

import com.example.TutorInteligente.Entidades.Evaluacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EvaluacionRepository extends JpaRepository<Evaluacion, Integer> {

    List<Evaluacion> findByAlumno_AlumnoIdAndCurso_CursoId(Integer alumnoId, Integer cursoId);

    Optional<Evaluacion> findTopByAlumno_AlumnoIdOrderByFechaDesc(Integer alumnoId);

    Optional<Evaluacion> findTopByAlumno_AlumnoIdAndCurso_CursoIdOrderByFechaDesc(Integer alumnoId, Integer cursoId);
}
