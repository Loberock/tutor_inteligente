package com.example.TutorInteligente.ClasesDTO;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PreguntaRequest {

    private Integer profesorId;

    private Integer cursoId;

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
