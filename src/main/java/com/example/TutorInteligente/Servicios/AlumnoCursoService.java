package com.example.TutorInteligente.Servicios;

import com.example.TutorInteligente.ClasesDTO.ActualizarNivelesRequest;
import com.example.TutorInteligente.ClasesDTO.CursoNivelDTO;
import com.example.TutorInteligente.Entidades.Alumno;
import com.example.TutorInteligente.Entidades.AlumnoCurso;
import com.example.TutorInteligente.Repositorios.AlumnoCursoRepository;
import com.example.TutorInteligente.Repositorios.AlumnoRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Transactional
public class AlumnoCursoService {

    @Autowired
    private AlumnoCursoRepository alumnoCursoRepo;

    @Autowired
    private AlumnoRepository alumnoRepo;

    public String actualizarNiveles(ActualizarNivelesRequest dto) {

        Alumno alumno = alumnoRepo
                .findByUsuario_UsuarioId(dto.getUsuarioId())
                .orElseThrow(() ->
                        new RuntimeException("Alumno no encontrado")
                );

        for (CursoNivelDTO cursoDto : dto.getCursos()) {

            AlumnoCurso ac = alumnoCursoRepo
                    .findByAlumno_AlumnoIdAndCurso_CursoId(
                            alumno.getAlumnoId(),
                            cursoDto.getCursoId()
                    )
                    .orElseThrow(() ->
                            new RuntimeException(
                                    "No existe relación alumno-curso"
                            )
                    );

            ac.setNivel(cursoDto.getNivel());

            alumnoCursoRepo.save(ac);
        }

        return "NIVELES ACTUALIZADOS";
    }
}