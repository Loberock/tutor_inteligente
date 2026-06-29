package com.example.TutorInteligente.Servicios;

import com.example.TutorInteligente.ClasesDTO.PreguntaBatchResponse;
import com.example.TutorInteligente.ClasesDTO.PreguntaRequest;
import com.example.TutorInteligente.ClasesDTO.PreguntaResponse;
import com.example.TutorInteligente.Entidades.Curso;
import com.example.TutorInteligente.Entidades.Pregunta;
import com.example.TutorInteligente.Entidades.Profesor;
import com.example.TutorInteligente.Repositorios.CursoRepository;
import com.example.TutorInteligente.Repositorios.PreguntaRepository;
import com.example.TutorInteligente.Repositorios.ProfesorRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Transactional
public class PreguntaService {

    @Autowired
    private PreguntaRepository preguntaRepo;

    @Autowired
    private ProfesorRepository profesorRepo;

    @Autowired
    private CursoRepository cursoRepo;

    public List<PreguntaResponse> listar(Integer cursoId, String grado, String dificultad) {
        return preguntaRepo.buscarPorFiltros(
                        cursoId,
                        limpiarFiltro(grado),
                        limpiarFiltro(dificultad)
                )
                .stream()
                .map(this::toResponse)
                .toList();
    }

    public PreguntaResponse obtenerPorId(Integer preguntaId) {
        return toResponse(buscarPregunta(preguntaId));
    }

    public PreguntaBatchResponse registrarLista(List<PreguntaRequest> lista) {
        for (PreguntaRequest dto : lista) {
            crearEntidad(dto);
        }

        PreguntaBatchResponse response = new PreguntaBatchResponse();
        response.setExitoso(true);
        response.setMensaje("Preguntas registradas correctamente");

        return response;
    }

    public PreguntaResponse crear(PreguntaRequest dto) {
        return toResponse(crearEntidad(dto));
    }

    public PreguntaResponse actualizar(Integer preguntaId, PreguntaRequest dto) {
        Pregunta pregunta = buscarPregunta(preguntaId);
        Profesor profesor = buscarProfesor(dto.getProfesorId());
        Curso curso = buscarCurso(dto.getCursoId());

        aplicarDatos(pregunta, dto, profesor, curso);

        return toResponse(preguntaRepo.save(pregunta));
    }

    public String eliminar(Integer preguntaId) {
        Pregunta pregunta = buscarPregunta(preguntaId);
        preguntaRepo.delete(pregunta);
        return "PREGUNTA ELIMINADA";
    }

    private Pregunta crearEntidad(PreguntaRequest dto) {
        Profesor profesor = buscarProfesor(dto.getProfesorId());
        Curso curso = buscarCurso(dto.getCursoId());
        Pregunta pregunta = new Pregunta();

        aplicarDatos(pregunta, dto, profesor, curso);

        return preguntaRepo.save(pregunta);
    }

    private void aplicarDatos(
            Pregunta pregunta,
            PreguntaRequest dto,
            Profesor profesor,
            Curso curso
    ) {
        pregunta.setContenidoPregunta(dto.getContenidoPregunta().trim());
        pregunta.setGrado(dto.getGrado().trim());
        pregunta.setDificultad(dto.getDificultad().trim().toUpperCase());
        pregunta.setOpcionA(dto.getOpcionA().trim());
        pregunta.setOpcionB(dto.getOpcionB().trim());
        pregunta.setOpcionC(dto.getOpcionC().trim());
        pregunta.setOpcionD(dto.getOpcionD().trim());
        pregunta.setRespuestaCorrecta(dto.getRespuestaCorrecta().trim().toUpperCase());
        pregunta.setRefuerzo(dto.getRefuerzo().trim());
        pregunta.setProfesor(profesor);
        pregunta.setCurso(curso);
    }

    private Pregunta buscarPregunta(Integer preguntaId) {
        return preguntaRepo.findById(preguntaId)
                .orElseThrow(() -> new RuntimeException("Pregunta no encontrada"));
    }

    private Profesor buscarProfesor(Integer profesorId) {
        return profesorRepo.findById(profesorId)
                .orElseThrow(() -> new RuntimeException("Profesor no encontrado"));
    }

    private Curso buscarCurso(Integer cursoId) {
        return cursoRepo.findById(cursoId)
                .orElseThrow(() -> new RuntimeException("Curso no encontrado"));
    }

    private String limpiarFiltro(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }

    private PreguntaResponse toResponse(Pregunta pregunta) {
        Profesor profesor = pregunta.getProfesor();
        Curso curso = pregunta.getCurso();

        return new PreguntaResponse(
                pregunta.getPreguntaId(),
                profesor.getProfesorId(),
                profesor.getNombre() + " " + profesor.getApellido(),
                curso.getCursoId(),
                curso.getNombreCurso(),
                pregunta.getContenidoPregunta(),
                pregunta.getGrado(),
                pregunta.getDificultad(),
                pregunta.getOpcionA(),
                pregunta.getOpcionB(),
                pregunta.getOpcionC(),
                pregunta.getOpcionD(),
                pregunta.getRespuestaCorrecta(),
                pregunta.getRefuerzo()
        );
    }
}
