package com.example.TutorInteligente.Repositorios;

import com.example.TutorInteligente.Entidades.Pregunta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PreguntaRepository
        extends JpaRepository<Pregunta,Integer> {

    @Query("""
            SELECT p
            FROM Pregunta p
            WHERE (:cursoId IS NULL OR p.curso.cursoId = :cursoId)
              AND (:grado IS NULL OR LOWER(p.grado) = LOWER(:grado))
              AND (:dificultad IS NULL OR LOWER(p.dificultad) = LOWER(:dificultad))
            """)
    List<Pregunta> buscarPorFiltros(
            Integer cursoId,
            String grado,
            String dificultad
    );

}
