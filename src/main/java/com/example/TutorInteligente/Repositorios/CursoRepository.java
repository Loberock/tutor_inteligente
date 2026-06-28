package com.example.TutorInteligente.Repositorios;

import com.example.TutorInteligente.Entidades.Curso;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CursoRepository
        extends JpaRepository<Curso,Integer> {

}