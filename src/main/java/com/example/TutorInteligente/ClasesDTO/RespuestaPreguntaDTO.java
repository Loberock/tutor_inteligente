package com.example.TutorInteligente.ClasesDTO;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RespuestaPreguntaDTO {

    @NotNull(message = "El preguntaId es obligatorio")
    private Integer preguntaId;

    @NotBlank(message = "La respuesta seleccionada es obligatoria")
    private String respuestaSeleccionada;
}
