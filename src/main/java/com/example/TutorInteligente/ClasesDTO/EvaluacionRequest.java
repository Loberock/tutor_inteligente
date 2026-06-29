package com.example.TutorInteligente.ClasesDTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EvaluacionRequest {

    @NotNull(message = "El alumnoId es obligatorio")
    private Integer alumnoId;

    @NotEmpty(message = "Debe enviar al menos una respuesta")
    private List<@Valid RespuestaPreguntaDTO> respuestas;

    @NotEmpty(message = "Debe enviar al menos un curso con nivel")
    private List<@Valid CursoNivelDTO> cursos;
}
