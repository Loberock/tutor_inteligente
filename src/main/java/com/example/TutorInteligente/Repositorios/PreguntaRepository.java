package com.example.TutorInteligente.Repositorios;

import com.example.TutorInteligente.Entidades.Pregunta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PreguntaRepository
        extends JpaRepository<Pregunta,Integer> {

    Optional<Pregunta> findById(Integer id);


}