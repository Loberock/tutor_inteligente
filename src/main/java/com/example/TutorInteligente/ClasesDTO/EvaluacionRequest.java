package com.example.TutorInteligente.ClasesDTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EvaluacionRequest {

    private Integer alumnoId;

    private List<RespuestaPreguntaDTO> respuestas;

    private List<CursoNivelDTO> cursos;
}
