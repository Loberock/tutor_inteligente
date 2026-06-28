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


    public List<AlumnoRendimientoDTO> obtenerRendimiento() {

        List<Alumno> alumnos = alumnoRepo.findAll();

        List<AlumnoRendimientoDTO> resultado = new ArrayList<>();

        for (Alumno alumno : alumnos) {

            // =========================
            // 1. OBTENER CURSOS
            // =========================
            List<AlumnoCurso> cursos =
                    alumnoCursoRepo.findByAlumno_AlumnoId(
                            alumno.getAlumnoId()
                    );

            if (cursos.isEmpty()) continue;

            // =========================
            // 2. ESCOGER PEOR CURSO
            // =========================
            AlumnoCurso peorCurso = null;
            int peorValor = Integer.MAX_VALUE;

            for (AlumnoCurso ac : cursos) {

                int valor = valorNivel(ac.getNivel());

                if (valor < peorValor) {
                    peorValor = valor;
                    peorCurso = ac;
                }
            }

            Integer cursoId = peorCurso.getCurso().getCursoId();

            // =========================
            // 3. OBTENER RESPUESTAS
            // =========================
            List<PreguntasResueltas> respuestas =
                    prRepo.findByAlumno_AlumnoIdAndPregunta_Curso_CursoId(
                            alumno.getAlumnoId(),
                            cursoId
                    );

            System.out.println("antes del continues"+alumno.getNombre());
            if (respuestas.isEmpty()) continue;
            System.out.println("Despues del continues");
            long total = respuestas.size();

            long correctas = respuestas.stream()
                    .filter(PreguntasResueltas::getCorrecta)
                    .count();

            double porcentaje = (correctas * 100.0) / total;

            System.out.println(porcentaje);
            // =========================
            // 4. ESTADO SEGÚN PORCENTAJE
            // =========================
            String estado;

            if (porcentaje <= 50) {
                estado = "NECESITA REFUERZO";
            } else if (porcentaje <= 75) {
                estado = "EN PROCESO";
            } else {
                estado = "AVANZA BIEN";
            }

            // =========================
            // 5. RESPONSE
            // =========================
            AlumnoRendimientoDTO dto = new AlumnoRendimientoDTO();

            dto.setNombreCompleto(
                    alumno.getNombre() + " " + alumno.getApellido()
            );

            dto.setGrado(alumno.getGrado());

            dto.setCursoNombre(
                    peorCurso.getCurso().getNombreCurso()
            );

            dto.setPorcentaje(porcentaje);

            dto.setEstado(estado);

            resultado.add(dto);
        }

        return resultado;
    }


    private int valorNivel(String nivel) {

        switch (nivel.toUpperCase()) {
            case "BASICO": return 1;
            case "INTERMEDIO": return 2;
            case "AVANZADO": return 3;
            default: return 0;
        }
    }
}