package com.example.TutorInteligente.ClasesDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PreguntaResponse {

    private Integer preguntaId;

    private Integer profesorId;

    private String profesorNombre;

    private Integer cursoId;

    private String cursoNombre;

    private String contenidoPregunta;

    private String grado;

    private String dificultad;

    private String opcionA;

    private String opcionB;

    private String opcionC;

    private String opcionD;

    private String respuestaCorrecta;

    private String refuerzo;
}
