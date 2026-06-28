package com.example.TutorInteligente.Servicios;

import com.example.TutorInteligente.ClasesDTO.LoginRequest;
import com.example.TutorInteligente.ClasesDTO.LoginResponse;
import com.example.TutorInteligente.Entidades.Alumno;
import com.example.TutorInteligente.Entidades.Profesor;
import com.example.TutorInteligente.Entidades.Usuario;
import com.example.TutorInteligente.Repositorios.AlumnoRepository;
import com.example.TutorInteligente.Repositorios.ProfesorRepository;
import com.example.TutorInteligente.Repositorios.UsuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsuarioRepository usuarioRepo;

    @Autowired
    private AlumnoRepository alumnoRepo;

    @Autowired
    private ProfesorRepository profesorRepo;

    @Autowired
    private JwtService jwtService;

    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getCorreo(),
                        request.getContrasena()
                )
        );

        Usuario usuario = usuarioRepo.findByCorreo(request.getCorreo())
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        LoginResponse response = new LoginResponse();
        response.setToken(jwtService.generarToken(usuario));
        response.setTipo(usuario.getRol());
        response.setUsuarioId(usuario.getUsuarioId());
        response.setNombre(obtenerNombre(usuario));

        return response;
    }

    private String obtenerNombre(Usuario usuario) {
        if ("ALUMNO".equalsIgnoreCase(usuario.getRol())) {
            return alumnoRepo.findByUsuario_UsuarioId(usuario.getUsuarioId())
                    .map(this::nombreAlumno)
                    .orElse(usuario.getCorreo());
        }

        return profesorRepo.findByUsuario_UsuarioId(usuario.getUsuarioId())
                .map(this::nombreProfesor)
                .orElse(usuario.getCorreo());
    }

    private String nombreAlumno(Alumno alumno) {
        return alumno.getNombre() + " " + alumno.getApellido();
    }

    private String nombreProfesor(Profesor profesor) {
        return profesor.getNombre() + " " + profesor.getApellido();
    }
}
