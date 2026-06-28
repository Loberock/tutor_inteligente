package com.example.TutorInteligente.Servicios;

import com.example.TutorInteligente.ClasesDTO.PreguntaBatchResponse;
import com.example.TutorInteligente.ClasesDTO.PreguntaRequest;
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

    public PreguntaBatchResponse registrarLista(
            List<PreguntaRequest> lista
    ) {

        try {

            for (PreguntaRequest dto : lista) {

                Profesor profesor =
                        profesorRepo.findById(dto.getProfesorId())
                                .orElseThrow(() ->
                                        new RuntimeException("Profesor no encontrado"));

                Curso curso =
                        cursoRepo.findById(dto.getCursoId())
                                .orElseThrow(() ->
                                        new RuntimeException("Curso no encontrado"));

                Pregunta pregunta = new Pregunta();

                pregunta.setContenidoPregunta(dto.getContenidoPregunta());
                pregunta.setGrado(dto.getGrado());
                pregunta.setDificultad(dto.getDificultad());

                pregunta.setOpcionA(dto.getOpcionA());
                pregunta.setOpcionB(dto.getOpcionB());
                pregunta.setOpcionC(dto.getOpcionC());
                pregunta.setOpcionD(dto.getOpcionD());

                pregunta.setRespuestaCorrecta(dto.getRespuestaCorrecta());
                pregunta.setRefuerzo(dto.getRefuerzo());

                pregunta.setProfesor(profesor);
                pregunta.setCurso(curso);

                preguntaRepo.save(pregunta);
            }

            PreguntaBatchResponse response = new PreguntaBatchResponse();
            response.setExitoso(true);
            response.setMensaje("Preguntas registradas correctamente");

            return response;

        } catch (Exception e) {

            // si algo falla, por @Transactional se revierte todo
            PreguntaBatchResponse response = new PreguntaBatchResponse();
            response.setExitoso(false);
            response.setMensaje("Error al registrar preguntas: " + e.getMessage());

            return response;
        }
    }
}