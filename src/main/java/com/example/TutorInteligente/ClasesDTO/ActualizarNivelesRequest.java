package com.example.TutorInteligente.ClasesDTO;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ActualizarNivelesRequest {

    private Integer usuarioId;

    private List<CursoNivelDTO> cursos;
}
