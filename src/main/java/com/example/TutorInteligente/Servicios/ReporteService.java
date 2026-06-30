package com.example.TutorInteligente.Servicios;

import com.example.TutorInteligente.ClasesDTO.AlumnoRendimientoDTO;
import com.example.TutorInteligente.Entidades.Alumno;
import com.example.TutorInteligente.Entidades.AlumnoCurso;
import com.example.TutorInteligente.Entidades.Evaluacion;
import com.example.TutorInteligente.Repositorios.AlumnoCursoRepository;
import com.example.TutorInteligente.Repositorios.AlumnoRepository;
import com.example.TutorInteligente.Repositorios.EvaluacionRepository;
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
    private EvaluacionRepository evaluacionRepo;

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
            if (grado != null
                    && !grado.isBlank()
                    && (alumno.getGrado() == null || !alumno.getGrado().equalsIgnoreCase(grado.trim()))) {
                continue;
            }

            List<AlumnoCurso> cursos = alumnoCursoRepo.findByAlumno_AlumnoId(alumno.getAlumnoId());

            for (AlumnoCurso alumnoCurso : cursos) {
                if (cursoId != null && !alumnoCurso.getCurso().getCursoId().equals(cursoId)) {
                    continue;
                }

                List<Evaluacion> evaluaciones = evaluacionRepo.findByAlumno_AlumnoIdAndCurso_CursoId(
                        alumno.getAlumnoId(),
                        alumnoCurso.getCurso().getCursoId()
                );

                if (evaluaciones.isEmpty()) {
                    continue;
                }

                long total = evaluaciones.stream()
                        .mapToLong(Evaluacion::getTotalPreguntas)
                        .sum();
                long correctas = evaluaciones.stream()
                        .mapToLong(Evaluacion::getRespuestasCorrectas)
                        .sum();

                double porcentaje = (correctas * 100.0) / total;
                String estado = estadoPorPorcentaje(porcentaje);
                String ultimoNivel = evaluaciones.stream()
                        .max((a, b) -> a.getFecha().compareTo(b.getFecha()))
                        .map(Evaluacion::getNivelAsignado)
                        .orElse(alumnoCurso.getNivel());

                if (Boolean.TRUE.equals(soloRefuerzo) && !"NECESITA REFUERZO".equals(estado)) {
                    continue;
                }

                resultado.add(new AlumnoRendimientoDTO(
                        alumno.getAlumnoId(),
                        alumno.getNombre() + " " + alumno.getApellido(),
                        alumno.getGrado(),
                        alumnoCurso.getCurso().getCursoId(),
                        alumnoCurso.getCurso().getNombreCurso(),
                        ultimoNivel,
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
