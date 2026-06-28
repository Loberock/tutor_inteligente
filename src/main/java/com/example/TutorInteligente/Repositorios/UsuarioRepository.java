package com.example.TutorInteligente.Repositorios;

import com.example.TutorInteligente.Entidades.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRepository
        extends JpaRepository<Usuario, Integer> {

    boolean existsByCorreo(String correo);

    Optional<Usuario> findByCorreo(String correo);
}
