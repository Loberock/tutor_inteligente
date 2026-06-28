package com.example.TutorInteligente.Repositorios;

import com.example.TutorInteligente.Entidades.Profesor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfesorRepository
        extends JpaRepository<Profesor,Integer> {

    Optional<Profesor> findByUsuario_UsuarioId(Integer usuarioId);
}
