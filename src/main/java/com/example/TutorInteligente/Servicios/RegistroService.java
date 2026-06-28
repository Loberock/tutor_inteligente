package com.example.TutorInteligente.Servicios;

import com.example.TutorInteligente.ClasesDTO.RegistroRequest;
import com.example.TutorInteligente.ClasesDTO.RegistroResponse;
import com.example.TutorInteligente.Entidades.Alumno;
import com.example.TutorInteligente.Entidades.AlumnoCurso;
import com.example.TutorInteligente.Entidades.Curso;
import com.example.TutorInteligente.Entidades.Profesor;
import com.example.TutorInteligente.Entidades.Usuario;
import com.example.TutorInteligente.Repositorios.AlumnoCursoRepository;
import com.example.TutorInteligente.Repositorios.AlumnoRepository;
import com.example.TutorInteligente.Repositorios.CursoRepository;
import com.example.TutorInteligente.Repositorios.ProfesorRepository;
import com.example.TutorInteligente.Repositorios.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    private PasswordEncoder passwordEncoder;

    public RegistroResponse registrar(RegistroRequest dto) {

        if (usuarioRepo.existsByCorreo(dto.getCorreo())) {
            throw new RuntimeException("Correo ya registrado");
        }

        String tipo = dto.getTipo() == null ? "" : dto.getTipo().trim().toUpperCase();

        if (!"ALUMNO".equals(tipo) && !"PROFESOR".equals(tipo)) {
            throw new RuntimeException("Tipo de usuario no valido");
        }

        Usuario usuario = new Usuario();
        usuario.setCorreo(dto.getCorreo());
        usuario.setContrasena(passwordEncoder.encode(dto.getContrasena()));
        usuario.setRol(tipo);
        usuario.setEstado(true);
        usuario = usuarioRepo.save(usuario);

        RegistroResponse response = new RegistroResponse();
        response.setCreado(true);

        if ("ALUMNO".equals(tipo)) {
            Alumno alumno = new Alumno();
            alumno.setNombre(dto.getNombre());
            alumno.setApellido(dto.getApellido());
            alumno.setGrado(dto.getGrado());
            alumno.setUsuario(usuario);
            alumno = alumnoRepo.save(alumno);

            List<Curso> cursos = cursoRepo.findAll();

            for (Curso curso : cursos) {
                AlumnoCurso ac = new AlumnoCurso();
                ac.setAlumno(alumno);
                ac.setCurso(curso);
                ac.setNivel("BASICO");
                alumnoCursoRepo.save(ac);
            }

            response.setTipo("ALUMNO");
            response.setAlumno(alumno);
            return response;
        }

        Profesor profesor = new Profesor();
        profesor.setNombre(dto.getNombre());
        profesor.setApellido(dto.getApellido());
        profesor.setUsuario(usuario);
        profesor = profesorRepo.save(profesor);

        response.setTipo("PROFESOR");
        response.setProfesor(profesor);

        return response;
    }
}
