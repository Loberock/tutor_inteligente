package com.example.TutorInteligente.Repositorios;

import com.example.TutorInteligente.Entidades.Profesor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProfesorRepository
        extends JpaRepository<Profesor,Integer> {

}