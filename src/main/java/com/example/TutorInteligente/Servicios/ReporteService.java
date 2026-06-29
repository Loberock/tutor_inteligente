package com.example.TutorInteligente.Servicios;

import com.example.TutorInteligente.ClasesDTO.AlumnoRendimientoDTO;
import com.example.TutorInteligente.Entidades.Alumno;
import com.example.TutorInteligente.Entidades.AlumnoCurso;
import com.example.TutorInteligente.Entidades.PreguntasResueltas;
import com.example.TutorInteligente.Repositorios.AlumnoCursoRepository;
import com.example.TutorInteligente.Repositorios.AlumnoRepository;
import com.example.TutorInteligente.Repositorios.PreguntasResueltasRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReporteService {

    @Autowired
    private AlumnoRepository alumnoRepo;

    @Autowired
    private AlumnoCursoRepository alumnoCursoRepo;

    @Autowired
    private PreguntasResueltasRepository prRepo;

    public List<AlumnoRendimientoDTO> obtenerRendimiento(
            Integer alumnoId,
            Integer cursoId,
            String grado,
            Boolean soloRefuerzo
    ) {
        List<Alumno> alumnos = alumnoId == null
                ? alumnoRepo.findAll()
                : alumnoRepo.findById(alumnoId)
                        .map(List::of)
                        .orElseThrow(() -> new RuntimeException("Alumno no encontrado"));

        List<AlumnoRendimientoDTO> resultado = new ArrayList<>();

        for (Alumno alumno : alumnos) {
            if (grado != null && !grado.isBlank() && !alumno.getGrado().equalsIgnoreCase(grado.trim())) {
                continue;
            }

            List<AlumnoCurso> cursos = alumnoCursoRepo.findByAlumno_AlumnoId(alumno.getAlumnoId());

            for (AlumnoCurso alumnoCurso : cursos) {
                if (cursoId != null && !alumnoCurso.getCurso().getCursoId().equals(cursoId)) {
                    continue;
                }

                List<PreguntasResueltas> respuestas =
                        prRepo.findByAlumno_AlumnoIdAndPregunta_Curso_CursoId(
                                alumno.getAlumnoId(),
                                alumnoCurso.getCurso().getCursoId()
                        );

                if (respuestas.isEmpty()) {
                    continue;
                }

                long total = respuestas.size();
                long correctas = respuestas.stream()
                        .filter(PreguntasResueltas::getCorrecta)
                        .count();

                double porcentaje = (correctas * 100.0) / total;
                String estado = estadoPorPorcentaje(porcentaje);

                if (Boolean.TRUE.equals(soloRefuerzo) && !"NECESITA REFUERZO".equals(estado)) {
                    continue;
                }

                resultado.add(new AlumnoRendimientoDTO(
                        alumno.getAlumnoId(),
                        alumno.getNombre() + " " + alumno.getApellido(),
                        alumno.getGrado(),
                        alumnoCurso.getCurso().getCursoId(),
                        alumnoCurso.getCurso().getNombreCurso(),
                        alumnoCurso.getNivel(),
                        total,
                        correctas,
                        porcentaje,
                        estado
                ));
            }
        }

        return resultado;
    }

    private String estadoPorPorcentaje(double porcentaje) {
        if (porcentaje <= 50) {
            return "NECESITA REFUERZO";
        }

        if (porcentaje <= 75) {
            return "EN PROCESO";
        }

        return "AVANZA BIEN";
    }
}
