package com.example.TutorInteligente.ClasesDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class EvaluacionResultadoResponse {

    private String mensaje;

    private Integer totalPreguntas;

    private Integer respuestasCorrectas;

    private Double porcentaje;

    private List<RefuerzoResponse> refuerzos;
}
