package com.example.TutorInteligente.Repositorios;

import com.example.TutorInteligente.Entidades.AlumnoCurso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlumnoCursoRepository
        extends JpaRepository<AlumnoCurso, Integer> {

    Optional<AlumnoCurso> findByAlumno_AlumnoIdAndCurso_CursoId(
            Integer alumnoId,
            Integer cursoId
    );

    List<AlumnoCurso> findByAlumno_AlumnoId(Integer alumnoId);

}