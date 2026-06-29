package com.example.TutorInteligente.ClasesDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RefuerzoResponse {

    private Integer preguntaId;

    private String contenidoPregunta;

    private String respuestaSeleccionada;

    private String respuestaCorrecta;

    private String refuerzo;
}
