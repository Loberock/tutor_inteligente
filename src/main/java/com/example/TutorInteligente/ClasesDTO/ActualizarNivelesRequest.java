package com.example.TutorInteligente.ClasesDTO;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ActualizarNivelesRequest {

    @NotNull(message = "El usuarioId es obligatorio")
    private Integer usuarioId;

    @NotEmpty(message = "Debe enviar al menos un curso")
    private List<@Valid CursoNivelDTO> cursos;
}
