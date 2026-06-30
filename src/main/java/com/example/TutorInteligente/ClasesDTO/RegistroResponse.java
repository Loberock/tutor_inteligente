package com.example.TutorInteligente.ClasesDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RegistroResponse {

    private Boolean creado;

    private String tipo;

    private Integer usuarioId;

    private Integer perfilId;

    private String nombreCompleto;
}
