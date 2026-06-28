package com.example.TutorInteligente.Servicios;

import com.example.TutorInteligente.ClasesDTO.RegistroRequest;
import com.example.TutorInteligente.ClasesDTO.RegistroResponse;
import com.example.TutorInteligente.Entidades.*;
import com.example.TutorInteligente.Repositorios.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class RegistroService {

    @Autowired
    UsuarioRepository usuarioRepo;

    @Autowired
    AlumnoRepository alumnoRepo;

    @Autowired
    ProfesorRepository profesorRepo;

    @Autowired
    private CursoRepository cursoRepo;

    @Autowired
    private AlumnoCursoRepository alumnoCursoRepo;

    public RegistroResponse registrar(
            RegistroRequest dto
    ) {

        if (usuarioRepo.existsByCorreo(
                dto.getCorreo()
        )) {

            throw new RuntimeException(
                    "Correo ya registrado"
            );

        }


        Usuario usuario = new Usuario();

        usuario.setCorreo(
                dto.getCorreo()
        );

        usuario.setContraseña(
                dto.getContraseña()
        );

        usuario.setEstado(true);

        usuario =
                usuarioRepo.save(usuario);


        RegistroResponse response =
                new RegistroResponse();

        response.setCreado(true);


        if ("ALUMNO".equalsIgnoreCase(
                dto.getTipo()
        )) {

            Alumno alumno =
                    new Alumno();

            alumno.setNombre(
                    dto.getNombre()
            );

            alumno.setApellido(
                    dto.getApellido()
            );

            alumno.setGrado(
                    dto.getGrado()
            );

            alumno.setUsuario(
                    usuario
            );

            alumno = alumnoRepo.save(alumno);

            // 1. obtener todos los cursos
            List<Curso> cursos = cursoRepo.findAll();

            // 2. crear relación alumno-curso para cada uno
            for (Curso curso : cursos) {

                AlumnoCurso ac = new AlumnoCurso();

                ac.setAlumno(alumno);

                ac.setCurso(curso);

                ac.setNivel("BASICO");

                alumnoCursoRepo.save(ac);
            }


            response.setTipo(
                    "ALUMNO"
            );

            response.setAlumno(
                    alumno
            );

        }

        else {

            Profesor profesor =
                    new Profesor();

            profesor.setNombre(
                    dto.getNombre()
            );

            profesor.setApellido(
                    dto.getApellido()
            );

            profesor.setUsuario(
                    usuario
            );

            profesor =
                    profesorRepo.save(
                            profesor
                    );

            response.setTipo(
                    "PROFESOR"
            );

            response.setProfesor(
                    profesor
            );

        }

        return response;

    }

}