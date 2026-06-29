package com.example.TutorInteligente.ClasesDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AlumnoRendimientoDTO {

    private Integer alumnoId;

    private String nombreCompleto;

    private String grado;

    private Integer cursoId;

    private String cursoNombre;

    private String nivel;

    private Long totalRespuestas;

    private Long respuestasCorrectas;

    private Double porcentaje;

    private String estado;
}
