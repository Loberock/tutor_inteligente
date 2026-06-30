package com.example.TutorInteligente.ClasesDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class EvaluacionProgresoResponse {

    private Integer evaluacionId;

    private Integer alumnoId;

    private Integer cursoId;

    private String cursoNombre;

    private Integer totalPreguntas;

    private Integer respuestasCorrectas;

    private Double porcentaje;

    private String nivelAsignado;

    private LocalDateTime fecha;

    private List<RefuerzoResponse> refuerzos;
}
