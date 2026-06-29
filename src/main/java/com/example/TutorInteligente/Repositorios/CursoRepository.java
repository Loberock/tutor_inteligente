package com.example.TutorInteligente.Repositorios;

import com.example.TutorInteligente.Entidades.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CursoRepository
        extends JpaRepository<Curso,Integer> {

    Optional<Curso> findByNombreCursoIgnoreCase(String nombreCurso);
}
